package view;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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


    public CustomExpirationDateViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomExpirationDate.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
       
        daysUntilExpiration.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE));
    }

    @FXML
    void initialize() {
        checkBoxExpirationDate.setSelected(false);
        datePickerExpirationDate.setDisable(true);
		daysUntilExpiration.setDisable(true);
        
        //datePickerExpirationDate.setValue(LocalDate.MIN.plusDays(100));
        
        
        datePickerExpirationDate.setOnAction(event ->{
        	if (event.getSource() instanceof DatePicker) {
        		LocalDate date = datePickerExpirationDate.getValue();
        		if (!LocalDate.now().isAfter(date)){
        			int noOfDaysBetween = (int) ChronoUnit.DAYS.between(LocalDate.now(), date);
        			SpinnerValueFactory<Integer> temp = daysUntilExpiration.getValueFactory();
        			temp.setValue(noOfDaysBetween);
        		}
        	}
        });
        
        daysUntilExpiration.valueProperty().addListener((obs, oldValue, newValue) -> {
            datePickerExpirationDate.setValue(LocalDate.now().plusDays(newValue));
        });
        
        /*datePickerExpirationDate.valueProperty().bind(Bindings.createObjectBinding(
        	() -> LocalDate.now().plusDays(daysUntilExpiration.getValue(),
        	daysUntilExpiration.valueProperty()
        ));*/
        
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
