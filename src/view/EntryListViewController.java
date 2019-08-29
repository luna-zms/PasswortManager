package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import controller.PMController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import model.Entry;
import model.Tag;
import util.BindingUtils;
import util.ClipboardUtils;
import util.WindowFactory;

public class EntryListViewController extends TableView<Entry> {
    private ObservableList<Entry> entries;
    private ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
    private PMController pmController;

    public EntryListViewController() {
        TableColumn<Entry, String> titleColumn = new TableColumn<>("Titel");
        TableColumn<Entry, String> usernameColumn = new TableColumn<>("Nutzername");
        TableColumn<Entry, String> passwordColumn = new TableColumn<>("Passwort");
        TableColumn<Entry, String> urlColumn = new TableColumn<>("URL");
        TableColumn<Entry, String> validUntilColumn = new TableColumn<>("Gültig bis");

        // Bind columns to getters of Entry
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper("*****"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("urlString"));
        // Use getter with specific string formatting
        validUntilColumn.setCellValueFactory(new PropertyValueFactory<>("validUntilString"));

        // Make password column unsortable as it contains just static data anyway
        passwordColumn.setSortable(false);

        // Right-align date column
        validUntilColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Ensure columns use available width
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        // Make placeholder German
        setPlaceholder(new Label("Keine Einträge gefunden."));

        // Make Java happy by specifying the exact types
        List<TableColumn<Entry, String>> columns = new ArrayList<>();
        columns.add(titleColumn);
        columns.add(usernameColumn);
        columns.add(passwordColumn);
        columns.add(urlColumn);
        columns.add(validUntilColumn);
        getColumns().setAll(columns);

        setContextMenu(buildContextMenu());

        // Highlight expired tags
        columns.forEach(col -> col.setCellFactory(innerCol -> new EntryListCell()));
        getSelectionModel().selectedItemProperty().addListener((obs, oldEntry, newEntry) -> {
            if (newEntry != null && newEntry.getValidUntil() != null &&
                newEntry.getValidUntil().isBefore(LocalDate.now())) {
                setStyle("-fx-selection-bar: red;");
            } else {
                setStyle("");
            }
        });

        setRowFactory(table -> {
            TableRow<Entry> row = new TableRow<>();

            // Cursed bind
            row.styleProperty().bind(BindingUtils.makeBinding(row.itemProperty(), entry -> {
                if (getSelectionModel().getSelectedItem() != entry && entry.getValidUntil() != null &&
                    entry.getValidUntil().isBefore(LocalDate.now())) {
                    return "-fx-background-color: darkred";
                } else {
                    return "";
                }
            }, "", getSelectionModel().selectedItemProperty()));

            // Clear selection on background click
            row.setOnMouseClicked(event -> {
                // User clicked background => item null => clear selection
                if (row.getItem() == null) getSelectionModel().clearSelection();
            });

            return row;
        });

        // Entry setting and filtering
        tag.addListener((obs, oldTag, newTag) -> applyFilter());

        setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                modifyEntry(tag);
            }
        });

        this.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if( keyEvent.getCode() == KeyCode.ENTER ) {
                keyEvent.consume();

                modifyEntry(tag);
            }
        });
    }

    private void modifyEntry(ObjectProperty<Tag> tag) {
        CreateModifyEntryViewController createModifyEntryViewController = createCreateModifyEntryViewController();
        Entry entry = getSelectionModel().getSelectedItem();
        if (entry != null) {
            createModifyEntryViewController.setOldEntry(entry);
        } else {
            createModifyEntryViewController.setCheckedTags(Collections.singletonList(tag.getValue()));
        }

        WindowFactory.showDialog("Eintrag erstellen", createModifyEntryViewController);
    }

    private static MenuItem createMenuItem(
            String text,
            EventHandler<ActionEvent> handler,
            ObservableValue<Boolean> disabled,
            KeyCombination accelerator
    ) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.disableProperty().bind(disabled);
        menuItem.setOnAction(handler);
        menuItem.setAccelerator(accelerator);

        return menuItem;
    }

    public void filterOnce(Predicate<Entry> predicate) {
        setItems(entries.filtered(predicate));
    }


    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }

    public ObjectProperty<Tag> tagProperty() {
        return tag;
    }

    public void init() {
        entries = pmController.getPasswordManager().entriesObservable();
        setItems(entries);
    }

    private void applyFilter() {
        // Always non-null as it's initialized to the root tag
        Tag newTag = tag.getValue();

        // De-inlined because it gets massacred by autoformat otherwise
        Predicate<Entry> predicate = entry -> entry.getTags().contains(newTag);
        // FilteredList cannot be sorted => wrap in SortedList
        SortedList<Entry> sortedList = new SortedList<>(entries.filtered(predicate));
        // Necessary if manually passing a SortedList to setItems
        sortedList.comparatorProperty().bind(comparatorProperty());

        setItems(sortedList);
    }

    private ContextMenu buildContextMenu() {
        ObservableValue<Entry> entry = getSelectionModel().selectedItemProperty();
        ObservableValue<Boolean> entryIsNull = BindingUtils.makeStaticBinding(entry, false, true);

        // Totally didn't steal the accelerators for these from KeePass
        // TODO: Maybe deduplicate the clipboard code a bit more
        List<MenuItem> items = new ArrayList<>();
        items.add(createCopyUsername(entry));
        items.add(createCopyPassword(entry, entryIsNull));
        items.addAll(createUrlItems(entry));

        items.add(new SeparatorMenuItem());

        items.add(createAddEntry());
        items.add(createEditEntry(entry, entryIsNull));
        items.add(createDeleteEntry(entry, entryIsNull));

        ContextMenu contextMenu = new ContextMenu(items.toArray(new MenuItem[]{}));
        contextMenu.setOnAction(event -> contextMenu.hide());  // Why in the name of fuck isn't this a default???
        return contextMenu;
    }

    private List<MenuItem> createUrlItems(ObservableValue<Entry> entry) {
        ObservableValue<Boolean> urlIsNull = BindingUtils.makeBinding(entry,
                                                                      currEntry -> currEntry.getUrl() == null,
                                                                      true);
        List<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(createMenuItem("URL öffnen", event -> {
            try {
                Desktop.getDesktop().browse(entry.getValue().getUrl().toURI());  // Yikes
            } catch (IOException | URISyntaxException e) {
                WindowFactory.showError("Fehler: URL öffnen",
                                        "Der Link konnte nicht geöffnet werden. Bitte öffnen Sie ihn manuell indem Sie den Link in Ihren Browser kopieren.");
            }
        }, urlIsNull, new KeyCharacterCombination("U", KeyCombination.CONTROL_DOWN)));

        // For some reason, IntelliJ wants to break this one into multiple lines but not the others
        // ¯\_(ツ)_/¯
        menuItems.add(createMenuItem("URL kopieren",
                                     event -> ClipboardUtils.copyToClipboard(entry, Entry::getUrlString),
                                     urlIsNull,
                                     new KeyCharacterCombination("U",
                                                                 KeyCombination.CONTROL_DOWN,
                                                                 KeyCombination.SHIFT_DOWN)));

        return menuItems;
    }

    private MenuItem createCopyPassword(
            ObservableValue<Entry> entry, ObservableValue<Boolean> entryIsNull
    ) {

        return createMenuItem("Passwort kopieren",
                              event -> ClipboardUtils.copyToClipboard(entry, Entry::getPassword),
                              entryIsNull,
                              new KeyCharacterCombination("C", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createCopyUsername(ObservableValue<Entry> entry) {

        ObservableValue<Boolean> usernameIsNull = BindingUtils.makeBinding(entry,
                                                                           currEntry -> currEntry.getUsername() == null,
                                                                           true);

        return createMenuItem("Nutzername kopieren",
                              event -> ClipboardUtils.copyToClipboard(entry, Entry::getUsername),
                              usernameIsNull,
                              new KeyCharacterCombination("B", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createAddEntry() {
        return createMenuItem("Eintrag erstellen",
                              event -> WindowFactory.showDialog("Eintrag erstellen",
                                                                createCreateModifyEntryViewController()),
                              new ReadOnlyBooleanWrapper(false),
                              new KeyCharacterCombination("I", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createEditEntry(
            ObservableValue<Entry> entry, ObservableValue<Boolean> entryIsNull
    ) {
        return createMenuItem("Eintrag bearbeiten", event -> {
            CreateModifyEntryViewController createModifyEntryViewController = createCreateModifyEntryViewController();
            createModifyEntryViewController.setOldEntry(entry.getValue());

            WindowFactory.showDialog("Eintrag bearbeiten", createModifyEntryViewController);

            // TODO: Maybe update displayed entry if necessary
        }, entryIsNull, new KeyCodeCombination(KeyCode.ENTER));
    }

    private MenuItem createDeleteEntry(
            ObservableValue<Entry> entry, ObservableValue<Boolean> entryIsNull
    ) {
        return createMenuItem("Eintrag löschen", event -> {
            // Prompt before deleting
            Entry entryValue = entry.getValue();
            String prompt = "Möchten Sie den Eintrag \"" + entryValue.getTitle() + "\" wirklich löschen?";
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, prompt);

            // Necessary because alerts just truncate their contents by default
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            alert.showAndWait()
                 .filter(type -> type == ButtonType.OK)
                 .ifPresent(res -> pmController.getEntryController().removeEntry(entryValue));
        }, entryIsNull, new KeyCodeCombination(KeyCode.DELETE));
    }

    private CreateModifyEntryViewController createCreateModifyEntryViewController() {
        CreateModifyEntryViewController createModifyEntryViewController = new CreateModifyEntryViewController();
        createModifyEntryViewController.setPmController(pmController);
        createModifyEntryViewController.init();

        return createModifyEntryViewController;
    }

    private class EntryListCell extends TableCell<Entry, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            TableRow<Entry> row = (TableRow<Entry>) getTableRow();
            Entry entry = row.getItem();

            super.updateItem(item, empty);

            if (empty || item == null || item.isEmpty()) {
                setText(null);
            } else {
                setText(item);
            }

            textFillProperty().unbind();
            if (entry != null && entry.getValidUntil() != null && entry.getValidUntil().isBefore(LocalDate.now())) {
                setTextFill(Color.WHITE);
            } else {
                // Necessary for highlighting to have the proper font color
                textFillProperty().bind(BindingUtils.makeBinding(getSelectionModel().selectedItemProperty(), newEntry -> {
                    if (newEntry == entry) return Color.WHITE;
                    else return Color.BLACK;
                }, Color.BLACK));
            }
        }
    }
}
