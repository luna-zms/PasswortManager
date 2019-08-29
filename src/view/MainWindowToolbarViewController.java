package view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import controller.PMController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Entry;
import util.CsvException;
import factory.WindowFactory;
import util.Tuple;

import javax.crypto.SecretKey;

public class MainWindowToolbarViewController extends GridPane {

    @FXML
    private Button addEntryToolbar;

    @FXML
    private Button saveDatabaseToolbar;

    @FXML
    private Button openDatabaseToolbar;

    @FXML
    private Button saveAsDatabaseToolbar;

    @FXML
    private Button setMasterPasswordToolbar;

    @FXML
    private Button importDatabaseToolbar;

    @FXML
    private Button exportDatabaseToolbar;

    @FXML
    private Button generatePasswordToolbar;

    @FXML
    private Button showExpiredEntriesToolbar;

    @FXML
    private Button searchButtonSearchbar;

    @FXML
    private MenuButton selectedColumnsSearchbar;

    @FXML
    private TextField searchFieldSearchbar;

    @FXML
    private CheckBox searchEverywhereSearchbar;

    @FXML
    private DatePicker filterExpiringSearchbar;

    private PMController pmController;

    private BiConsumer<Predicate<Entry>, Tuple<Boolean,Boolean>> onSearchRefreshAction;

    private Consumer<Path> openDatabaseFileAction;

    private Runnable onTreeViewRefresh;

    private boolean dirtyProblemSignal = true;

    public MainWindowToolbarViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindowToolbar.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void initialize() {
        Image addEntryImage;
        Image saveDatabaseImage;
        Image openDatabaseImage;
        Image saveAsDatabaseImage;
        Image setMasterPwImage;
        Image importImage;
        Image exportImage;
        Image generatePasswordImage;
        Image searchButtonImage;
        Image filterButtonImage;
        Image showExpiredEntriesImage;
        try {
            addEntryImage = new Image(getClass().getResourceAsStream("/view/resources/add_entry_toolbar_icon.png"));
            saveDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/save_toolbar_icon.png"));
            openDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/open_toolbar_icon.png"));
            saveAsDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/save_as_toolbar_icon.png"));
            setMasterPwImage = new Image(getClass().getResourceAsStream("/view/resources/change_master_password_toolbar_icon.png"));
            importImage = new Image(getClass().getResourceAsStream("/view/resources/import_toolbar_icon.png"));
            exportImage = new Image(getClass().getResourceAsStream("/view/resources/export_toolbar_icon.png"));
            generatePasswordImage = new Image(getClass().getResourceAsStream("/view/resources/generate_password_toolbar_icon.png"));
            showExpiredEntriesImage = new Image(getClass().getResourceAsStream("/view/resources/entries_expired_toolbar_icon.png"));
            searchButtonImage = new Image(getClass().getResourceAsStream("/view/resources/search_icon_15px.png"));
            filterButtonImage = new Image(getClass().getResourceAsStream("/view/resources/filter_icon_20px.png"));
        } catch( Exception e ) {
            errorMessage("Kritischer Fehler", "Beim Laden der Programmdaten ist ein Fehler aufgetreten! Vergewissern Sie sich, dass Sie das Programm korrekt installiert haben! Das Programm schließt sich nach dieser Meldung.");
            System.exit(1);
            return;
        }

        addEntryToolbar.setGraphic(new ImageView(addEntryImage));
        initializeActionsAddEntry();

        saveDatabaseToolbar.setGraphic(new ImageView(saveDatabaseImage));
        initializeActionsSaveDatabase();

        openDatabaseToolbar.setGraphic(new ImageView(openDatabaseImage));
        initializeActionsOpenDatabase();

        saveAsDatabaseToolbar.setGraphic(new ImageView(saveAsDatabaseImage));
        initializeActionsSaveAsDatabase();

        setMasterPasswordToolbar.setGraphic(new ImageView(setMasterPwImage));
        initializeActionsSetMasterPassword();

        importDatabaseToolbar.setGraphic(new ImageView(importImage));
        initializeActionsImportDatabase();

        exportDatabaseToolbar.setGraphic(new ImageView(exportImage));
        initializeActionsExportDatabase();

        generatePasswordToolbar.setGraphic(new ImageView(generatePasswordImage));
        initializeActionsGeneratePassword();

        showExpiredEntriesToolbar.setGraphic(new ImageView(showExpiredEntriesImage));

        searchButtonSearchbar.setGraphic(new ImageView(searchButtonImage));
        initializeActionsSearchButton();

        selectedColumnsSearchbar.setGraphic(new ImageView(filterButtonImage));

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.R,
                KeyCombination.CONTROL_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination, () -> {
            searchFieldSearchbar.clear();
            searchButtonSearchbar.getOnAction().handle(null);
            searchFieldSearchbar.requestFocus();
        }));
    }

    /**
     * Helper method to show an Alert dialog.
     *
     * @param title   Title of the Alert dialog.
     * @param content Content of the Alert dialog.
     */
    void errorMessage(String title, String content) {
        WindowFactory.showError(title, content);
    }

    private void initializeActionsAddEntry() {
        addEntryToolbar.setOnAction(event -> {
            WindowFactory.showCreateModifyEntryView(pmController);
        });
        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.PLUS,
                KeyCombination.CONTROL_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> addEntryToolbar.getOnAction().handle(null)));
    }

    private void initializeActionsSaveDatabase() {
        saveDatabaseToolbar.setOnAction(event -> {
            try {
                pmController.getLoadSaveController().save(pmController.getSavePath());
            } catch (CsvException exc) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines internen Fehler ist das Speichern " +
                        "fehlgeschlagen. Versuchen Sie es später erneut und starten sie eventuell Ihren Computer neu. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
                return;
            } catch (IOException ioExc) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines Ausgabefehlers ist das Speichern " +
                        "fehlgeschlagen. Stellen Sie sicher, dass Sie Zugriffsrechte auf die Datei haben und der Pfad " +
                        "zu ihr existiert.");
                return;
            } catch (RuntimeException exception) {
                WindowFactory.showError("Interner Fehler beim Speichern", "Die Datei konnte aufgrund eines unspezifizierten Fehlers nicht gespeichert werden:\n\n" + exception.getLocalizedMessage());
                return;
            }
             saveDatabaseToolbar.setEffect(new InnerShadow(BlurType.TWO_PASS_BOX, Color.LIGHTGREEN, 50, 0.2, 0.0, 0.0));
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(2000),
                    ae -> saveDatabaseToolbar.setEffect(null)));
            timeline.play();
            dirtyProblemSignal = false;
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> saveDatabaseToolbar.getOnAction().handle(null)));
    }

    private void initializeActionsSaveAsDatabase() {
        saveAsDatabaseToolbar.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Speichere Kopie als");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GateKeeper-Dateien", "*.gate");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showSaveDialog((Stage) getScene().getWindow());

            if (file == null) return;

            try {
                pmController.getLoadSaveController().save(file.toPath());
            } catch (CsvException exc) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines internen Fehler ist das Speichern" +
                        "fehlgeschlagen. Versuchen Sie es später erneut und starten Sie eventuell Ihren Computer neu. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
            } catch (IOException ioExc) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines Ausgabefehlers ist das Speichern " +
                        "fehlgeschlagen. Stellen Sie sicher, dass Sie Zugriffsrechte auf die Datei haben und der Pfad " +
                        "zu ihr existiert.");
            }
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> saveAsDatabaseToolbar.getOnAction().handle(null)));
    }

    private void initializeActionsOpenDatabase() {
        openDatabaseToolbar.setOnAction(event -> {
            if (pmController.isDirty()) {
                Alert alert = WindowFactory.createAlert(Alert.AlertType.CONFIRMATION, "Sie haben ungespeicherte Änderungen. Alle ihre Änderungen gehen verloren!");

                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                alert.setTitle("Verwerfen bestätigen");
                alert.setHeaderText("Möchten Sie ihre Änderungen wirklich verwerfen?");

                Optional<ButtonType> result = alert.showAndWait();

                if (!result.isPresent() || result.get() != ButtonType.YES) {
                    return;
                }
            }

            openDatabaseFileAction.accept(pmController.getSavePath());
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.O,
                KeyCombination.CONTROL_DOWN);
        KeyCombination keyCombination2 = new KeyCodeCombination(KeyCode.N,
                KeyCombination.CONTROL_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> openDatabaseToolbar.getOnAction().handle(null)));
        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination2,
                () -> openDatabaseToolbar.getOnAction().handle(null)));
    }

    private void initializeActionsSetMasterPassword() {
        setMasterPasswordToolbar.setOnAction(event -> {
            SecretKey oldKey = pmController.getPasswordManager().getMasterPasswordKey();
            SetMasterPasswordViewController dialogController = new SetMasterPasswordViewController();
            dialogController.setPmController(pmController);
            dialogController.setMode(true);
            WindowFactory.showDialog("Einstellungen: Master-Passwort setzen", dialogController);
            if( dialogController.getPasswordSet() ) {
                saveDatabaseToolbar.getOnAction().handle(null);
                if( !dirtyProblemSignal ) {
                    // Make sure to don't show the information alert, even though the "save" task
                    // did fail
                    dirtyProblemSignal = true;
                    WindowFactory.createAlert(Alert.AlertType.INFORMATION,
                            "Ihr Passwort wurde erfolgreich gesetzt!").showAndWait();
                } else {
                    errorMessage("Fehlgeschlagen", "Ihr Passwort konnte nicht gespeichert werden!");
                    pmController.getPasswordManager().setMasterPasswordKey(oldKey);
                    // If something failed when trying to save reset the password, so
                    // the user doesn't have to worry, which of his passwords is now
                    // in the system.
                }
            }
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> setMasterPasswordToolbar.getOnAction().handle(null)));
    }

    private void initializeActionsImportDatabase() {
        importDatabaseToolbar.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importiere Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-CSV-Dateien", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showOpenDialog((Stage) getScene().getWindow());

            if (file == null) return;
            if (!file.exists()) {
                errorMessage("Fehler beim Öffnen", "Die gewählte Datei existiert nicht!");
                return;
            }

            try {
                pmController.getImportExportController().load(file.toPath());
            } catch (CsvException exc) {
                errorMessage("Fehler beim Import", "Beim Importieren der Datei ist ein Fehler aufgetreten. " +
                        "Überprüfen Sie, ob die Datei das nötige Format erfüllt. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
                return;
            }
            if( onTreeViewRefresh != null ) onTreeViewRefresh.run();
            // if( on)

            WindowFactory.createAlert(Alert.AlertType.INFORMATION, "Ihre Daten wurden erfolgreich importiert!")
                    .showAndWait();
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.I,
                KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> importDatabaseToolbar.getOnAction().handle(null)));
    }

    private void initializeActionsExportDatabase() {
        exportDatabaseToolbar.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportiere Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-CSV-Dateien", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showSaveDialog((Stage) getScene().getWindow());

            if (file == null) return;

            try {
                pmController.getImportExportController().save(file.toPath());
            } catch (CsvException exc) {
                errorMessage("Fehler beim Export", "Beim Exportieren der Datenbank ist ein Fehler " +
                        "aufgetreten. Starten Sie das Programm erneut, wenn weiterhin Fehler auftreten. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
                return;
            }

            WindowFactory.createAlert(Alert.AlertType.INFORMATION, "Ihre Daten wurden erfolgreich exportiert!")
                    .showAndWait();
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.E,
                KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> exportDatabaseToolbar.getOnAction().handle(null)));
    }

    private void initializeActionsGeneratePassword() {
        generatePasswordToolbar.setOnAction(event -> {
            GeneratePasswordViewController dialogController = new GeneratePasswordViewController();
            dialogController.setPmController(pmController);
            WindowFactory.showDialog("Extra: Passwort generieren", dialogController, false);
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.G,
                KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> generatePasswordToolbar.getOnAction().handle(null)));
    }

    private void checkExpiredExisting() {
        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.E,
                KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN);
        if( !pmController.getEntryController().filter(Entry::isExpired).isEmpty() ) {
            showExpiredEntriesToolbar.setManaged(true);
            showExpiredEntriesToolbar.setVisible(true);

            getScene().getAccelerators().put(keyCombination,
                    () -> showExpiredEntriesToolbar.getOnAction().handle(null));
        } else {
            showExpiredEntriesToolbar.setManaged(false);
            showExpiredEntriesToolbar.setVisible(false);

            getScene().getAccelerators().remove(keyCombination);
        }
    }

    private void initializeActionsShowExpiredEntries() {
        Predicate<Entry> filterExpired = Entry::isExpired;
        showExpiredEntriesToolbar.setOnAction(event -> {
            onSearchRefreshAction.accept(filterExpired, new Tuple<>(true, false));
        });

        Platform.runLater(this::checkExpiredExisting);

        pmController.getPasswordManager().entriesObservable().addListener(
                (InvalidationListener) event -> checkExpiredExisting()
        );
    }

    private void initializeActionsSearchButton() {
        searchButtonSearchbar.setOnAction(event -> {
            String searchQuery = searchFieldSearchbar.getText().toLowerCase();

            if (
                    selectedColumnsSearchbar
                            .getItems()
                            .stream()
                            .noneMatch(menuButton -> ((CheckBox) ((CustomMenuItem) menuButton).getContent()).isSelected())
            ) {
                WindowFactory.showError("Keine Spalten ausgewählt!",
                                        "Mindestens eine Spalte muss in die Suche\nmiteinbezogen werden, aber keine ist ausgewählt!",
                                        "Suchen: Fehler");
                return;
            }

            LocalDate expiredUntil = filterExpiringSearchbar.getValue();

            boolean searchEverywhere = searchEverywhereSearchbar.isSelected();
            boolean ghostsActivated = searchQuery.matches("^who you gonna call[?]?[!]?$");

            onSearchRefreshAction.accept((entry -> {
                LocalDate validUntil = entry.getValidUntil();
                if (expiredUntil != null && validUntil != null && !(
                        expiredUntil.isAfter(validUntil) || expiredUntil.isEqual(validUntil)
                ))
                	return false;
                else if (expiredUntil != null && validUntil == null)
                    return false; // Don't show dates without expiration date when expiredUntil is selected

                List<String> queryParts = Arrays.asList(searchQuery.split(" "));
                List<String> notYetFound = new ArrayList<>(queryParts);

                for (MenuItem menuItem : selectedColumnsSearchbar.getItems()) {
                    CheckBox checkBox = ((CheckBox) ((CustomMenuItem) menuItem).getContent());
                    if (!checkBox.isSelected()) continue;

                    String value = null;
                    switch (checkBox.getText()) {
                        case "Titel":
                            value = entry.getTitle();
                            break;
                        case "Nutzername":
                            value = entry.getUsername();
                            break;
                        case "URL":
                            value = entry.getUrlString();
                            break;
                        case "Notiz":
                            value = entry.getNote();
                            break;
                        case "Sicherheitsfrage":
                            value = entry.getSecurityQuestion().getAnswer() + " " +
                                    entry.getSecurityQuestion().getQuestion();
                            break;
                    }
                    if (value == null) continue;

                    value = value.toLowerCase();

                    for (String queriedValue : queryParts) {
                        if (value.contains(queriedValue)) {
                            notYetFound.remove(queriedValue);
                        }
                    }

                    if (notYetFound.isEmpty()) break;
                }

                return notYetFound.isEmpty();
            }), new Tuple<>(searchEverywhere, ghostsActivated));
        });

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.F,
                KeyCombination.CONTROL_DOWN);

        Platform.runLater(() -> getScene().getAccelerators().put(keyCombination,
                () -> searchFieldSearchbar.requestFocus()));
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;

        initializeActionsShowExpiredEntries();
    }

    public void setOnSearchRefreshAction(BiConsumer<Predicate<Entry>, Tuple<Boolean,Boolean>> onSearchRefreshAction) {
        this.onSearchRefreshAction = onSearchRefreshAction;
    }

    public void setOpenDatabaseFileAction(Consumer<Path> openDatabaseFileAction) {
        this.openDatabaseFileAction = openDatabaseFileAction;
    }

    public void setOnTreeViewRefresh(Runnable refreshAction) {
        this.onTreeViewRefresh = refreshAction;
    }
}
