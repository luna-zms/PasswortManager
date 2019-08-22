package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CreateModifyEntryViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField entryName;

    @FXML
    private TextField userName;

    @FXML
    private TextField repeatPassword;

    @FXML
    private PasswordQualityBarController passwordQualityBar;

    @FXML
    private TextField password;

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
}
