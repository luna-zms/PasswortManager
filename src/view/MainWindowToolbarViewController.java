package view;

import java.io.File;
import java.io.IOException;
import java.time.chrono.Chronology;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainWindowToolbarViewController extends GridPane {

    @FXML
    private Button addEntryToolbar;

    @FXML
    private Button saveDatabaseToolbar;

    @FXML
    private Button openDatabaseToolbar;

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

    private void initializeActionsAddEntry() {
        addEntryToolbar.setOnAction(event -> {
            Stage dialog = new Stage();
            dialog.setTitle("Eintrag erstellen");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UTILITY);
            CreateModifyEntryViewController dialogController = new CreateModifyEntryViewController();
            dialogController.setPmController(pmController);
            Scene scene = new Scene(dialogController);
            dialog.setScene(scene);
            dialog.showAndWait();
        });
    }

    private void initializeActionsSaveDatabase() {
        saveDatabaseToolbar.setOnAction(event -> {

        });
    }

    private void initializeActionsOpenDatabase() {
        openDatabaseToolbar.setOnAction(event -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Öffne Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-Dateien", ".pwds");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showOpenDialog(dialog);

            if (file == null) return;
        });
    }

    private void initializeActionsSetMasterPassword() {
        setMasterPasswordToolbar.setOnAction(event -> {
            Stage dialog = new Stage();
            dialog.setTitle("Einstellungen: Master-Passwort setzen");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UTILITY);
            SetMasterPasswordViewController dialogController = new SetMasterPasswordViewController();
            dialogController.setPmController(pmController);
            Scene scene = new Scene(dialogController);
            dialog.setScene(scene);
            dialog.showAndWait();
        });
    }

    private void initializeActionsImportDatabase() {
        importDatabaseToolbar.setOnAction(event -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importiere Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-CSV-Dateien", ".csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showOpenDialog(dialog);

            if (file == null) return;
        });
    }

    private void initializeActionsExportDatabase() {
        exportDatabaseToolbar.setOnAction(event -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportiere Datei");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-CSV-Dateien", ".csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showSaveDialog(dialog);

            if (file == null) return;
        });
    }

    private void initializeActionsGeneratePassword() {
        generatePasswordToolbar.setOnAction(event -> {
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Suchen: Fehler");
                alert.setHeaderText("Keine Spalten ausgewählt!");
                alert.setContentText("Mindestens eine Spalte muss in die Suche\nmiteinbezogen werden, aber keine ist ausgewählt!");
                alert.showAndWait();
                return;
            }
            ;

            Chronology expiredUntil = filterExpiringSearchbar.getChronology();

            boolean searchEverywhere = searchEverywhereSearchbar.isSelected();

            // TODO: start search and filter (using EntryController.filter)
        });

    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }
}
