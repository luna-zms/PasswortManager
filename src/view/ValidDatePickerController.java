package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ValidDatePickerController extends GridPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox hasValidDate;

    @FXML
    private DatePicker validDate;

    @FXML
    private TextField daysValid;

    public ValidDatePickerController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ValidDatePicker.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        assert hasValidDate != null : "fx:id=\"hasValidDate\" was not injected: check your FXML file 'ValidDatePicker.fxml'.";
        assert validDate != null : "fx:id=\"validDate\" was not injected: check your FXML file 'ValidDatePicker.fxml'.";
        assert daysValid != null : "fx:id=\"daysValid\" was not injected: check your FXML file 'ValidDatePicker.fxml'.";

    }
}
