package view;

import java.io.IOException;
import java.net.URL;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import factory.WindowFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import javafx.util.converter.FormatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CustomExpirationDateViewController extends GridPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private CheckBox checkBoxExpirationDate;

    @FXML
    private DatePicker datePickerExpirationDate;

    @FXML
    private Spinner<Integer> daysUntilExpiration;

    public LocalDate getExpirationDate() {
        if (!checkBoxExpirationDate.isSelected()) return null;
        return datePickerExpirationDate.getValue();
    }

    public void setExpirationDate(LocalDate date) {
        if (date == null) {
            checkBoxExpirationDate.setSelected(false);
        } else {
            checkBoxExpirationDate.setSelected(true);
            datePickerExpirationDate.setValue(date);
            datePickerExpirationDate.setDisable(false);
            daysUntilExpiration.setDisable(false);
            //checkExpirationDate();
            //datePickerExpirationDate.fireEvent(new ActionEvent());
        }
    }

    public void checkExpirationDate() {
        if (checkBoxExpirationDate.isSelected() &&
                datePickerExpirationDate.getValue().isBefore(LocalDate.now().plusDays(1))) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Achtung: Neues Ablaufdatum");
            alert.setHeaderText("Ihr aktuelles Ablaufdatum liegt in der Vergangenheit");
            alert.setContentText("Wollen Sie Ihr Ablaufdatum auf den " + LocalDate.now().plusDays(30).toString() + " setzen?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) setExpirationDate(LocalDate.now().plusDays(30));
            else {
                checkBoxExpirationDate.setSelected(false);
                datePickerExpirationDate.setDisable(true);
                daysUntilExpiration.setDisable(true);
            }
        }
        datePickerExpirationDate.fireEvent(new ActionEvent());
    }

    public CustomExpirationDateViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomExpirationDate.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
    
        daysUntilExpiration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,2147483647-1));//LocalDate.now().until(LocalDate.MAX).getDays())
      
    }
    @FXML
    void initialize() {
        checkBoxExpirationDate.setSelected(false);
        datePickerExpirationDate.setDisable(true);
        daysUntilExpiration.setDisable(true);
        
        UnaryOperator<Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]*)?")) { 
                return change;
            }
            return null;
        };
        daysUntilExpiration.getEditor().setTextFormatter(
        	    new TextFormatter<Integer>(new IntegerStringConverter(), 1, integerFilter));
        
        datePickerExpirationDate.setOnAction(event -> {
            LocalDate date = datePickerExpirationDate.getValue();
            if(date != null){
                if (LocalDate.now().isBefore(date)) {
                    int noOfDaysBetween = (int) ChronoUnit.DAYS.between(LocalDate.now(), date);
                    SpinnerValueFactory<Integer> temp = daysUntilExpiration.getValueFactory();
                    temp.setValue(noOfDaysBetween);
                } else {
                    datePickerExpirationDate.setValue(LocalDate.now().plusDays(1));
                } 
            } else datePickerExpirationDate.setValue(LocalDate.now().plusDays(1));
        });

        daysUntilExpiration.valueProperty().addListener((obs, oldValue, newValue) -> {
        	if (newValue != null) {
        		datePickerExpirationDate.setValue(LocalDate.now().plusDays(newValue));
        	} else {
        		daysUntilExpiration.getValueFactory().setValue(1);
        	}
        });

        checkBoxExpirationDate.setOnAction(event -> {
            if (event.getSource() instanceof CheckBox) {
                if (checkBoxExpirationDate.isSelected()) {

                    datePickerExpirationDate.setDisable(false);
                    daysUntilExpiration.setDisable(false);
                } else {
                    datePickerExpirationDate.setDisable(true);
                    daysUntilExpiration.setDisable(true);
                }
            }
        });
    }


}
