package  view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

public class GeneratePasswordViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<?> signList;

    @FXML
    private Button pwButton;

    @FXML
    private Button accButton;

    @FXML
    private Button canButton;

    @FXML
    private ProgressBar securityBar;

    @FXML
    private Slider Lengthslider;

    @FXML
    private Spinner<?> LengthSpinner;

    @FXML
    private HBox pwHbox;

    @FXML
    void initialize() {
        assert signList != null : "fx:id=\"signList\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert pwButton != null : "fx:id=\"pwButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert accButton != null : "fx:id=\"accButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert canButton != null : "fx:id=\"canButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert securityBar != null : "fx:id=\"securityBar\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert Lengthslider != null : "fx:id=\"Lengthslider\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert LengthSpinner != null : "fx:id=\"LengthSpinner\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert pwHbox != null : "fx:id=\"pwHbox\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";

    }
}
