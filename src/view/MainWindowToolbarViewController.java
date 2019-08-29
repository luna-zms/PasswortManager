package view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import controller.PMController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        try {
            addEntryImage = new Image(getClass().getResourceAsStream("/view/resources/add_entry_toolbar_icon.png"));
            saveDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/save_toolbar_icon.png"));
            openDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/open_toolbar_icon.png"));
            saveAsDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/save_as_toolbar_icon.png"));
            setMasterPwImage = new Image(getClass().getResourceAsStream("/view/resources/change_master_password_toolbar_icon.png"));
            importImage = new Image(getClass().getResourceAsStream("/view/resources/import_toolbar_icon.png"));
            exportImage = new Image(getClass().getResourceAsStream("/view/resources/export_toolbar_icon.png"));
            generatePasswordImage = new Image(getClass().getResourceAsStream("/view/resources/generate_password_toolbar_icon.png"));
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

        searchButtonSearchbar.setGraphic(new ImageView(searchButtonImage));
        initializeActionsSearchButton();

        selectedColumnsSearchbar.setGraphic(new ImageView(filterButtonImage));
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

        });
    }

    private void initializeActionsSaveAsDatabase() {
        saveAsDatabaseToolbar.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Speichere Kopie als");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Gatekeeper-Dateien", "*.gate");
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
    }

    private void initializeActionsOpenDatabase() {
        openDatabaseToolbar.setOnAction(event -> {
            openDatabaseFileAction.accept(pmController.getSavePath());
        });
    }

    private void initializeActionsSetMasterPassword() {
        setMasterPasswordToolbar.setOnAction(event -> {
            SetMasterPasswordViewController dialogController = new SetMasterPasswordViewController();
            dialogController.setPmController(pmController);
            dialogController.setMode(true);
            WindowFactory.showDialog("Einstellungen: Master-Passwort setzen", dialogController);
        });
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
    }

    private void initializeActionsGeneratePassword() {
        generatePasswordToolbar.setOnAction(event -> {
            GeneratePasswordViewController dialogController = new GeneratePasswordViewController();
            dialogController.setPmController(pmController);
            WindowFactory.showDialog("Extra: Passwort generieren", dialogController, false);
        });
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
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
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
