package view;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.scene.layout.GridPane;
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

    public LocalDate getExpirationDate(){
		if (!checkBoxExpirationDate.isSelected()) return null;
		return datePickerExpirationDate.getValue();
    }
    
    public void setExpirationDate(LocalDate date){
    	if (date == null){
    		checkBoxExpirationDate.setSelected(false);	
    	}else if (date.isBefore(LocalDate.now().plusDays(1))){
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("Achtung: Neues Ablaufdatum");
    		alert.setHeaderText("Ihr Aktuelles Ablaufdatum liegt in der Vergangenheit");
    		alert.setContentText("Wollen sie ihr Ablaufdatum auf den: " + LocalDate.now().plusDays(30).toString() +" setzen?");
    		Optional<ButtonType> result = alert.showAndWait();
    		if (result.get() == ButtonType.OK){
    			setExpirationDate(LocalDate.now().plusDays(30));
    		} else {
    			checkBoxExpirationDate.setSelected(false);
    			datePickerExpirationDate.setDisable(true);
    			daysUntilExpiration.setDisable(true);
    		}
    	}else{
    		checkBoxExpirationDate.setSelected(true);
    		datePickerExpirationDate.setValue(date);
    		datePickerExpirationDate.setDisable(false);
			daysUntilExpiration.setDisable(false);
			datePickerExpirationDate.fireEvent(new ActionEvent());
    	}
    	
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
        datePickerExpirationDate.setValue(LocalDate.now().plusDays(1));
        daysUntilExpiration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    @FXML
    void initialize() {
        checkBoxExpirationDate.setSelected(false);
        datePickerExpirationDate.setDisable(true);
        daysUntilExpiration.setDisable(true);
        
        
        datePickerExpirationDate.setOnAction(event -> {
        	LocalDate date = datePickerExpirationDate.getValue();
        	if (LocalDate.now().isBefore(date)) {
        		int noOfDaysBetween = (int) ChronoUnit.DAYS.between(LocalDate.now(), date);
        		SpinnerValueFactory<Integer> temp = daysUntilExpiration.getValueFactory();
        		temp.setValue(noOfDaysBetween);
        	} else {
        		datePickerExpirationDate.setValue(LocalDate.now().plusDays(1));
        	}
        });
        
        daysUntilExpiration.valueProperty().addListener((obs, oldValue, newValue) -> {
            datePickerExpirationDate.setValue(LocalDate.now().plusDays(newValue));
        });
        
        checkBoxExpirationDate.setOnAction(event -> {
        	if (event.getSource() instanceof CheckBox) {
        		if(checkBoxExpirationDate.isSelected()){
        			
        			datePickerExpirationDate.setDisable(false);
        			daysUntilExpiration.setDisable(false);
        		}else{
        			datePickerExpirationDate.setDisable(true);
        			daysUntilExpiration.setDisable(true);
        		}
            }
        });
    }


}
