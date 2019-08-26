package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.WindowFactory;

public class CreateModifyEntryViewController extends AnchorPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField entryName;

    @FXML
    private TextField userName;

    @FXML
    private CustomPasswordFieldViewController repeatPassword;

    @FXML
    private PasswordQualityBarController passwordQualityBar;

    @FXML
    private CustomPasswordFieldViewController password;

    @FXML
    private Button generatePasswordButton;

    @FXML
    private TextField url;

    @FXML
    private TextField tags;

    @FXML
    private Button tagListButton;

    @FXML
    private TextArea notes;

    @FXML
    private TagTree tagTree;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    private PMController pmController;

    public CreateModifyEntryViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateModifyEntryView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }

        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });

        okButton.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Helper method to show an Alert dialog.
     * @param title Title of the Alert dialog.
     * @param content Content of the Alert dialog.
     */
    void errorMessage(String title, String content) {
        WindowFactory.showError(title, content);
    }

    @FXML
    void initialize() {
        assert entryName != null : "fx:id=\"entryName\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert userName != null : "fx:id=\"userName\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert repeatPassword != null : "fx:id=\"repeatPassword\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert passwordQualityBar != null : "fx:id=\"passwordQualityBar\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert generatePasswordButton != null : "fx:id=\"generatePasswordButton\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert url != null : "fx:id=\"url\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert tags != null : "fx:id=\"tags\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert tagListButton != null : "fx:id=\"tagListButton\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert notes != null : "fx:id=\"notes\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert tagTree != null : "fx:id=\"tagTree\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";

    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
        tagTree.setPmController(pmController);
    }
}
