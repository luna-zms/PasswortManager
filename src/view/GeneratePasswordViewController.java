package  view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class GeneratePasswordViewController extends GridPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TitledPane signList;

    @FXML
    private Button pwButton;

    @FXML
    private Button accButton;

    @FXML
    private Button canButton;

    @FXML
    private PasswordQualityBarController securityBar;

    @FXML
    private Slider lengthSlider;

    @FXML
    private Spinner<Integer> lengthSpinner;

    @FXML
    private CustomPasswordFieldViewController pwField;

    private SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory;

    @FXML
    void initialize() {
        assert signList != null : "fx:id=\"signList\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert pwButton != null : "fx:id=\"pwButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert accButton != null : "fx:id=\"accButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert canButton != null : "fx:id=\"canButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert securityBar != null : "fx:id=\"securityBar\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert lengthSlider != null : "fx:id=\"lengthSlider\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert lengthSpinner != null : "fx:id=\"LengthSpinner\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert pwField != null : "fx:id=\"pwField\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";

        spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 256);

        lengthSpinner.setValueFactory(spinnerValueFactory);

        lengthSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double aDouble) {
                if( aDouble == 1.0 ) return "kurz";
                else if( aDouble == 2.0 ) return "normal";
                else if( aDouble == 3.0 ) return "lang";
                return "-";
            }

            @Override
            public Double fromString(String s) {
                if( s.equals("kurz") ) return 1.0;
                else if( s.equals("normal") ) return 2.0;
                else if( s.equals("lang") ) return 3.0;
                return 0.0;
            }
        });

        lengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            spinnerValueFactory.setValue(8 * newVal.intValue());
        });

        spinnerValueFactory.valueProperty().addListener((obs, oldVal, newVal) -> {
                lengthSlider.setValue(Math.min(3, Math.max(1, (int) (newVal/8))));
                spinnerValueFactory.setValue(newVal);
        });
        spinnerValueFactory.setValue(12);
    }

    public GeneratePasswordViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GeneratePasswordView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
