package view;

/**
 * Sample Skeleton for 'CustomPasswordField.fxml' Controller Class
 */

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomPasswordFieldViewController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="textField"
    private PasswordField textField; // Value injected by FXMLLoader

    @FXML // fx:id="eyeButton"
    private Button eyeButton; // Value injected by FXMLLoader

    @FXML // fx:id="checkButton"
    private Button checkButton; // Value injected by FXMLLoader

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert textField != null : "fx:id=\"textField\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";
        assert eyeButton != null : "fx:id=\"eyeButton\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";
        assert checkButton != null : "fx:id=\"checkButton\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";

    }
}

