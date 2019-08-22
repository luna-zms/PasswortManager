package view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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
    private Button searchButtonSearchbar;

    @FXML
    private MenuButton selectedColumnsSearchbar;

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

        Image saveDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/save_toolbar_icon.png"));
        saveDatabaseToolbar.setGraphic(new ImageView(saveDatabaseImage));

        Image openDatabaseImage = new Image(getClass().getResourceAsStream("/view/resources/open_toolbar_icon.png"));
        openDatabaseToolbar.setGraphic(new ImageView(openDatabaseImage));

        Image setMasterPwImage = new Image(getClass().getResourceAsStream("/view/resources/change_master_password_toolbar_icon.png"));
        setMasterPasswordToolbar.setGraphic(new ImageView(setMasterPwImage));

        Image importImage = new Image(getClass().getResourceAsStream("/view/resources/import_toolbar_icon.png"));
        importDatabaseToolbar.setGraphic(new ImageView(importImage));

        Image exportImage = new Image(getClass().getResourceAsStream("/view/resources/export_toolbar_icon.png"));
        exportDatabaseToolbar.setGraphic(new ImageView(exportImage));


        Image searchButtonImage = new Image(getClass().getResourceAsStream("/view/resources/search_icon_15px.png"));
        searchButtonSearchbar.setGraphic(new ImageView(searchButtonImage));

        Image filterButtonImage = new Image(getClass().getResourceAsStream("/view/resources/filter_icon_20px.png"));
        selectedColumnsSearchbar.setGraphic(new ImageView(filterButtonImage));
    }
}
