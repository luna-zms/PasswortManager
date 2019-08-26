package view;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Entry;
import util.CsvException;
import util.WindowFactory;

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

    private BiConsumer<Predicate<Entry>, Boolean> onSearchRefreshAction;

    private Consumer<String> openDatabaseFileAction;

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
        Image addEntryImage = new Image(getClass().getResourceAsStream("/view/resources/add_entry_toolbar_icon.png"));
        addEntryToolbar.setGraphic(new ImageView(addEntryImage));
        initializeActionsAddEntry();

        Image saveDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/save_toolbar_icon.png"));
        saveDatabaseToolbar.setGraphic(new ImageView(saveDatabaseImage));
        initializeActionsSaveDatabase();

        Image openDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/open_toolbar_icon.png"));
        openDatabaseToolbar.setGraphic(new ImageView(openDatabaseImage));
        initializeActionsOpenDatabase();

        Image saveAsDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/save_as_toolbar_icon.png"));
        saveAsDatabaseToolbar.setGraphic(new ImageView(saveAsDatabaseImage));
        initializeActionsSaveAsDatabase();

        Image setMasterPwImage = new Image(getClass().getResourceAsStream("/view/resources/change_master_password_toolbar_icon.png"));
        setMasterPasswordToolbar.setGraphic(new ImageView(setMasterPwImage));
        initializeActionsSetMasterPassword();

        Image importImage = new Image(getClass().getResourceAsStream("/view/resources/import_toolbar_icon.png"));
        importDatabaseToolbar.setGraphic(new ImageView(importImage));
        initializeActionsImportDatabase();

        Image exportImage = new Image(getClass().getResourceAsStream("/view/resources/export_toolbar_icon.png"));
        exportDatabaseToolbar.setGraphic(new ImageView(exportImage));
        initializeActionsExportDatabase();

        Image generatePasswordImage = new Image(getClass().getResourceAsStream("/view/resources/generate_password_toolbar_icon.png"));
        generatePasswordToolbar.setGraphic(new ImageView(generatePasswordImage));
        initializeActionsGeneratePassword();

        Image searchButtonImage = new Image(getClass().getResourceAsStream("/view/resources/search_icon_15px.png"));
        searchButtonSearchbar.setGraphic(new ImageView(searchButtonImage));
        initializeActionsSearchButton();

        Image filterButtonImage = new Image(getClass().getResourceAsStream("/view/resources/filter_icon_20px.png"));
        selectedColumnsSearchbar.setGraphic(new ImageView(filterButtonImage));
    }

    /**
     * Helper method to show an Alert dialog.
     * @param title Title of the Alert dialog.
     * @param content Content of the Alert dialog.
     */
    void errorMessage(String title, String content) {
        WindowFactory.showError(title, content);
    }

    private void initializeActionsAddEntry() {
        addEntryToolbar.setOnAction(event -> {
            CreateModifyEntryViewController dialogController = new CreateModifyEntryViewController();
            dialogController.setPmController(pmController);
            WindowFactory.showDialog("Eintrag erstellen", dialogController);
        });
    }

    private void initializeActionsSaveDatabase() {
        saveDatabaseToolbar.setOnAction(event -> {
            try {
                pmController.getLoadSaveController().save(pmController.getSavePath());
            } catch( CsvException exc ) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines internen Fehler ist das Speichern " +
                        "fehlgeschlagen. Versuchen Sie es später erneut und starten sie eventuell Ihren Computer neu. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
            } catch( IOException ioExc ) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines Ausgabefehlers ist das Speichern " +
                        "fehlgeschlagen. Stellen Sie sicher, dass Sie Zugriffsrechte auf die Datei haben und der Pfad " +
                        "zu ihr existiert.");
            }
        });
    }

    private void initializeActionsSaveAsDatabase() {
        saveAsDatabaseToolbar.setOnAction(event -> {
            Stage dialog = WindowFactory.createStage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Speichere Kopie als");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-Dateien", "*.pwds");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showSaveDialog(dialog);

            if (file == null) return;

            try {
                pmController.getLoadSaveController().save(file.toPath());
            } catch( CsvException exc) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines internen Fehler ist das Speichern" +
                        "fehlgeschlagen. Versuchen Sie es später erneut und starten sie eventuell Ihren Computer neu. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
            } catch( IOException ioExc ) {
                errorMessage("Speichern fehlgeschlagen", "Aufgrund eines Ausgabefehlers ist das Speichern " +
                        "fehlgeschlagen. Stellen Sie sicher, dass Sie Zugriffsrechte auf die Datei haben und der Pfad " +
                        "zu ihr existiert.");
            }
        });
    }

    private void initializeActionsOpenDatabase() {
        openDatabaseToolbar.setOnAction(event -> {
            Stage dialog = WindowFactory.createStage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Öffne Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-Dateien", "*.pwds");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showOpenDialog(dialog);

            if (file == null) return;
            if( !file.exists() ) {
                errorMessage("Fehler beim Öffnen", "Die gewählte Datei existiert nicht!");
                return;
            }

            openDatabaseFileAction.accept(file.getPath());
        });
    }

    private void initializeActionsSetMasterPassword() {
        setMasterPasswordToolbar.setOnAction(event -> {
            SetMasterPasswordViewController dialogController = new SetMasterPasswordViewController();
            dialogController.setPmController(pmController);
            WindowFactory.showDialog("Einstellungen: Master-Passwort setzen", dialogController);
        });
    }

    private void initializeActionsImportDatabase() {
        importDatabaseToolbar.setOnAction(event -> {
            Stage dialog = WindowFactory.createStage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importiere Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-CSV-Dateien", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showOpenDialog(dialog);

            if (file == null) return;
            if( !file.exists() ) {
                errorMessage("Fehler beim Öffnen", "Die gewählte Datei existiert nicht!");
                return;
            }

            try {
                pmController.getImportExportController().load(file.toPath());
            } catch( CsvException exc ) {
                errorMessage("Fehler beim Import", "Beim Importieren der Datei ist ein Fehler aufgetreten. " +
                        "Überprüfen Sie, ob die Datei das nötige Format erfüllt. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
            }
        });
    }

    private void initializeActionsExportDatabase() {
        exportDatabaseToolbar.setOnAction(event -> {
            Stage dialog = WindowFactory.createStage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportiere Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-CSV-Dateien", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showSaveDialog(dialog);

            if (file == null) return;

            try {
                pmController.getImportExportController().save(file.toPath());
            } catch( CsvException exc ) {
                errorMessage("Fehler beim Export", "Beim Exportieren der Datenbank ist ein Fehler " +
                        "aufgetreten. Starten Sie das Programm erneut, wenn weiterhin Fehler auftreten. " +
                        "Nähere Beschreibung: \"" + exc.getMessage() + "\"");
            }
        });
    }

    private void initializeActionsGeneratePassword() {
        generatePasswordToolbar.setOnAction(event -> {
            GeneratePasswordViewController dialogController = new GeneratePasswordViewController();
            dialogController.setPmController(pmController);
            WindowFactory.showDialog("Einstellungen: Master-Passwort setzen", dialogController, false);
        });
    }

    private void initializeActionsSearchButton() {
        searchButtonSearchbar.setOnAction(event -> {
            String searchQuery = searchFieldSearchbar.getText();

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

            onSearchRefreshAction.accept((entry -> {
                LocalDate validUntil = entry.getValidUntil();
                if( expiredUntil != null && validUntil != null && (
                        expiredUntil.isAfter( validUntil ) || expiredUntil.isEqual( validUntil )
                ) )
                    return false;
                List<String> queryParts = Arrays.asList(searchQuery.split(" "));
                List<String> notYetFound = new ArrayList<>(queryParts);

                for (MenuItem menuItem : selectedColumnsSearchbar.getItems()) {
                    CheckBox checkBox = ((CheckBox) ((CustomMenuItem) menuItem).getContent());
                    if( !checkBox.isSelected() ) continue;

                    String value = null;
                    switch (checkBox.getText()) {
                        case "Titel": value = entry.getTitle(); break;
                        case "Nutzername": value = entry.getUsername(); break;
                        case "URL": value = entry.getUrlString(); break;
                        case "Notiz": value = entry.getNote(); break;
                        case "Sicherheitsfrage": value = entry.getSecurityQuestion().getAnswer() + " " +
                                entry.getSecurityQuestion().getQuestion(); break;
                    }
                    if( value == null ) continue;

                    for( String queriedValue : queryParts ) {
                        if( value.contains(queriedValue) ) {
                            notYetFound.remove(queriedValue);
                        }
                    }

                    if( notYetFound.isEmpty() ) break;
                }

                return notYetFound.isEmpty();
            }), searchEverywhere);
        });
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }

    public void setOnSearchRefreshAction(BiConsumer<Predicate<Entry>, Boolean> onSearchRefreshAction) {
        this.onSearchRefreshAction = onSearchRefreshAction;
    }

    public void setOpenDatabaseFileAction(Consumer<String> openDatabaseFileAction) {
        this.openDatabaseFileAction = openDatabaseFileAction;
    }
}
