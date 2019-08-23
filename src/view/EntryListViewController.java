package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import application.Main;
import controller.PMController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Entry;
import util.BindingUtils;
import util.ClipboardUtils;

public class EntryListViewController extends TableView<Entry> {
    // TODO: Maybe replace this with ObjectProperty<Tag> since general filters are supposed to be handled by the controller
    private ObjectProperty<ObservableList<Entry>> entries = new SimpleObjectProperty<>();
    private ObjectProperty<Predicate<Entry>> filter = new SimpleObjectProperty<>();
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
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        // Use getter with specific string formatting
        validUntilColumn.setCellValueFactory(new PropertyValueFactory<>("validUntilString"));

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

        // Set row factory to include context menu
        ContextMenu contextMenu = buildContextMenu();
        setRowFactory(table -> {
            TableRow<Entry> row = new TableRow<>();
            row.setContextMenu(contextMenu);

            return row;
        });

        // Entry setting and filtering
        entries.addListener((obs, oldEntries, newEntries) -> applyFilter());
        filter.addListener((obs, oldPred, newPred) -> applyFilter());
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

    public ObjectProperty<Predicate<Entry>> filterProperty() {
        return filter;
    }

    public void setEntries(ObservableList<Entry> entries) {
        this.entries.set(entries);
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }

    private void applyFilter() {
        ObservableList<Entry> newEntries = entries.getValue();
        Predicate<Entry> newFilter = filter.getValue();

        if (newFilter != null && newEntries != null) setItems(newEntries.filtered(newFilter));
        else if (entries != null) setItems(newEntries);
        else setEntries(FXCollections.emptyObservableList());
    }

    private ContextMenu buildContextMenu() {
        ObservableValue<Entry> entry = getSelectionModel().selectedItemProperty();

        // Totally didn't steal the accelerators for these from KeePass
        // TODO: Maybe deduplicate the clipboard code a bit more
        List<MenuItem> items = new ArrayList<>();
        items.add(createCopyUsername(entry));
        items.add(createCopyPassword(entry));
        items.addAll(createUrlItems(entry));
        items.add(new SeparatorMenuItem());
        items.add(createAddEntry());

        // TODO: Add buttons to edit and delete an entry

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
                // TODO: Show error popup
                e.printStackTrace();
            }
        }, urlIsNull, new KeyCharacterCombination("U", KeyCombination.CONTROL_DOWN)));

        // For some reason, IntelliJ wants to break this one into multiple lines but not the others
        // ¯\_(ツ)_/¯
        menuItems.add(createMenuItem("URL kopieren",
                                     event -> {
                                         ClipboardUtils.copyToClipboard(entry, Entry::getUrlString);
                                     },
                                     urlIsNull,
                                     new KeyCharacterCombination("U",
                                                                 KeyCombination.CONTROL_DOWN,
                                                                 KeyCombination.SHIFT_DOWN)));

        return menuItems;
    }

    private MenuItem createCopyPassword(ObservableValue<Entry> entry) {
        ObservableValue<Boolean> entryIsNull = BindingUtils.makeStaticBinding(entry, false, true);

        return createMenuItem("Passwort kopieren", event -> {
            ClipboardUtils.copyToClipboard(entry, Entry::getPassword);
        }, entryIsNull, new KeyCharacterCombination("C", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createCopyUsername(ObservableValue<Entry> entry) {

        ObservableValue<Boolean> usernameIsNull = BindingUtils.makeBinding(entry,
                                                                           currEntry -> currEntry.getUsername() == null,
                                                                           true);

        return createMenuItem("Nutzername kopieren", event -> {
            ClipboardUtils.copyToClipboard(entry, Entry::getUsername);
        }, usernameIsNull, new KeyCharacterCombination("B", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createAddEntry() {
        // TODO: Generalize this for edit entry as well
        return createMenuItem("Eintrag hinzufügen",
                              event -> {
                                  // TODO: Change once stage factory is done
                                  Stage dialog = new Stage();
                                  dialog.setTitle("Eintrag erstellen");
                                  dialog.initModality(Modality.APPLICATION_MODAL);
                                  dialog.initStyle(StageStyle.UTILITY);

                                  CreateModifyEntryViewController createModifyEntryViewController = new CreateModifyEntryViewController();
                                  // TODO: Uncomment this once it's merged in
                                  // createModifyEntryViewController.setPmController(pmController);
                                  // createModifyEntryViewController.init()

                                  Scene scene = new Scene(createModifyEntryViewController);
                                  scene.getStylesheets()
                                       .add(Main.class.getResource("application.css")
                                                      .toExternalForm());
                                  dialog.setScene(scene);

                                  dialog.showAndWait();
                              },
                              new ReadOnlyBooleanWrapper(false),
                              new KeyCharacterCombination("I", KeyCombination.CONTROL_DOWN));
    }
}
