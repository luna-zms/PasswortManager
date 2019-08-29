package view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import controller.PMController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.TransformationList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.Entry;
import model.Tag;
import util.BindingUtil;
import util.ClipboardUtil;
import util.DateFormatUtil;
import factory.WindowFactory;

public class EntryListViewController extends TableView<Entry> {
    private ObservableList<Entry> entries;
    private ObjectProperty<Tag> tag = new SimpleObjectProperty<>();
    private PMController pmController;

    private static final int GHOST_CHANCE = 33;

    Media sound;

    public EntryListViewController() {
        TableColumn<Entry, String> titleColumn = new TableColumn<>("Titel");
        TableColumn<Entry, String> usernameColumn = new TableColumn<>("Nutzername");
        TableColumn<Entry, String> passwordColumn = new TableColumn<>("Passwort");
        TableColumn<Entry, URL> urlColumn = new TableColumn<>("URL");
        TableColumn<Entry, LocalDate> validUntilColumn = new TableColumn<>("Gültig bis");

        // Bind columns to getters of Entry
        titleColumn.setCellFactory(col -> new EntryListCell<>());
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        usernameColumn.setCellFactory(col -> new EntryListCell<>());
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellFactory(col -> new EntryListCell<>());
        passwordColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper("*****"));
        urlColumn.setCellFactory(col -> new EntryListCell<>());
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        validUntilColumn.setCellFactory(col -> new EntryListCell<>(DateFormatUtil::formatDate));
        validUntilColumn.setCellValueFactory(new PropertyValueFactory<>("validUntil"));

        try {
            URI musicFile = getClass().getResource("/util/resources/ghostChasing.dict").toURI();
            sound = new Media(musicFile.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            sound = null;
        }

        // Make password column unsortable as it contains just static data anyway
        passwordColumn.setSortable(false);

        // Right-align date column
        validUntilColumn.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Ensure columns use available width
        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        // Make placeholder German
        setPlaceholder(new Label("Keine Einträge gefunden."));

        // Make Java happy by specifying the exact types
        List<TableColumn<Entry, ?>> columns = new ArrayList<>();
        columns.add(titleColumn);
        columns.add(usernameColumn);
        columns.add(passwordColumn);
        columns.add(urlColumn);
        columns.add(validUntilColumn);
        getColumns().setAll(columns);

        setContextMenu(buildContextMenu());

        // Highlight expired tags
        getSelectionModel().selectedItemProperty().addListener((obs, oldEntry, newEntry) -> {
            if (newEntry != null && newEntry.isExpired()) {
                setStyle("-fx-selection-bar: red;");
            } else if( getSelectionModel().getSelectedItem() != newEntry && newEntry.getTags().isEmpty() ) {
                setStyle("-fx-selection-bar: white;");
            } else {
                setStyle("");
            }
        });

        setRowFactory(table -> {
            TableRow<Entry> row = new TableRow<>();

            // Cursed bind
            row.styleProperty().bind(BindingUtil.makeBinding(row.itemProperty(), entry -> {
                if (getSelectionModel().getSelectedItem() != entry && entry.isExpired()) {
                    return "-fx-background-color: darkred";
                } else if( getSelectionModel().getSelectedItem() != entry && entry.getTags().isEmpty() ) {
                    return "-fx-effect: innershadow( gaussian , gray, 10, 0.5 , 0, 0 ); -fx-background-color: lightgray;";
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
            Entry entry = getSelectionModel().getSelectedItem();
            easterEgg(entry);

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

    private boolean easterEgg(Entry entry) {
        if( entry != null && entry.getTags().isEmpty() && sound != null ) {
            ((GhostTransformationList) getItems()).remove(entry);
            try {
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
            } catch (Exception e) {
                // Ignore
            }
            getSelectionModel().clearSelection();
            return true;
        }
        return false;
    }

    private void modifyEntry(ObjectProperty<Tag> tag) {
        Entry entry = getSelectionModel().getSelectedItem();
        if( easterEgg(entry) ) return;
        if (entry != null) {
            WindowFactory.showCreateModifyEntryView(pmController, entry);
        } else {
            WindowFactory.showCreateModifyEntryView(pmController, Collections.singletonList(tag.getValue()));

        }
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

    class GhostTransformationList extends TransformationList<Entry, Entry> {
        private Entry phantom = null;
        private int phantomIndex;

        private final String[] GHOST_NAMES = new String[] {
                "Vigo",
                "Slimer",
                "Torb",
                "Library ghost",
                "Rowan North",
                "Nearly Neckless Nick",
                "The Bloody Baron",
                "Hui Buh"
        };
        private final int MINIMUM_LIST_SIZE = 5;

        GhostTransformationList(ObservableList<? extends Entry> observableList) {
            super(observableList);

            Timeline oneSecondWonder = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                if (phantom == null ) return;
                if( (new Random()).nextInt(6) == 0 ) {
                    phantom = null;
                    phantomIndex = 0;
                } else {
                    phantomIndex = (new Random()).nextInt(getSource().size());
                }
                refresh();
            }));
            oneSecondWonder.setCycleCount(Animation.INDEFINITE);
            oneSecondWonder.play();

            sourceChanged(null);
        }

        protected void sourceChanged(ListChangeListener.Change<? extends Entry> change) {
            if( (new Random()).nextInt(100) < GHOST_CHANCE && getSource().size() > MINIMUM_LIST_SIZE ) {
                phantom = new Entry("Ghost", "secret");
                try {
                    phantom.setUrl(new URL("https://www.youtube.com/watch?v=m9We2XsVZfc"));
                } catch (MalformedURLException e) {
                    // Ignore
                }
                phantom.setUsername(GHOST_NAMES[(new Random()).nextInt(GHOST_NAMES.length)]);
                phantomIndex = (new Random()).nextInt(getSource().size());
            } else {
                phantom = null;
                phantomIndex = 0;
            }

            if(change != null) fireChange(change);
        }

        public void remove(Entry element) {
            if( element == null ) return;
            if( element.getTags().isEmpty() ) {
                phantom = null;
                phantomIndex = 0;
                refresh();
            } else super.remove(element);
        }

        public int getSourceIndex(int index) {
            if( phantom == null || index < phantomIndex ) return index;
            else return index-1;
        }

        public int getViewIndex(int index) {
            return index;
        }

        public Entry get(int index) {
            if( phantom != null && index == phantomIndex ) return phantom;
            return getSource().get(getSourceIndex(index));
        }

        public int size() {
            int actualSize = getSource().size();
            return phantom == null ? actualSize : actualSize+1;
        }
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

        GhostTransformationList observableList = new GhostTransformationList(sortedList);
        setItems(observableList);
    }

    private ContextMenu buildContextMenu() {
        ObservableValue<Entry> entry = getSelectionModel().selectedItemProperty();
        ObservableValue<Boolean> entryIsNull = BindingUtil.makeStaticBinding(entry, false, true);

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
        ObservableValue<Boolean> urlIsNull = BindingUtil.makeBinding(entry,
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
                                     event -> ClipboardUtil.copyToClipboard(entry, Entry::getUrlString),
                                     urlIsNull,
                                     new KeyCharacterCombination("U",
                                                                 KeyCombination.CONTROL_DOWN,
                                                                 KeyCombination.SHIFT_DOWN)));

        return menuItems;
    }

    private MenuItem createCopyPassword(
            ObservableValue<Entry> entry, ObservableValue<Boolean> entryIsNull
    ) {

        return createMenuItem("Passwort kopieren", event -> {
            ClipboardUtil.copyToClipboard(entry, Entry::getPassword);
            // Clear clipboard
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(20),
                                                          innerEv -> ClipboardUtil.copyToClipboard("")));
            timeline.setCycleCount(1);
            timeline.play();

        }, entryIsNull, new KeyCharacterCombination("C", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createCopyUsername(ObservableValue<Entry> entry) {

        ObservableValue<Boolean> usernameIsNull = BindingUtil.makeBinding(entry,
                                                                           currEntry -> currEntry.getUsername() == null,
                                                                           true);

        return createMenuItem("Nutzername kopieren",
                              event -> ClipboardUtil.copyToClipboard(entry, Entry::getUsername),
                              usernameIsNull,
                              new KeyCharacterCombination("B", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createAddEntry() {
        return createMenuItem("Eintrag erstellen",
                              event -> WindowFactory.showCreateModifyEntryView(pmController),
                              new ReadOnlyBooleanWrapper(false),
                              new KeyCharacterCombination("I", KeyCombination.CONTROL_DOWN));
    }

    private MenuItem createEditEntry(
            ObservableValue<Entry> entry, ObservableValue<Boolean> entryIsNull
    ) {
        return createMenuItem("Eintrag bearbeiten", event -> {
            if( easterEgg(entry.getValue()) ) return;
            WindowFactory.showCreateModifyEntryView(pmController, entry.getValue());
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

    private class EntryListCell<T> extends TableCell<Entry, T> {
        private Function<T, String> stringifier;

        EntryListCell() {
            stringifier = Objects::toString;
        }

        EntryListCell(Function<T, String> stringifier) {
            this.stringifier = stringifier;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            TableRow<Entry> row = (TableRow<Entry>) getTableRow();
            Entry entry = row.getItem();

            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
            } else {
                setText(stringifier.apply(item));
            }

            textFillProperty().unbind();
            if (entry != null && entry.isExpired()) {
                setTextFill(Color.WHITE);
            } else {
                // Necessary for highlighting to have the proper font color
                textFillProperty().bind(BindingUtil.makeBinding(getSelectionModel().selectedItemProperty(), newEntry -> {
                    if (newEntry == entry) return Color.WHITE;
                    else return Color.BLACK;
                }, Color.BLACK));
            }
        }
    }
}
