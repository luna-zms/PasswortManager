package view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SetMasterPasswordViewController extends GridPane {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private CustomPasswordFieldViewController customPasswordFieldBase;

    @FXML
    private CustomPasswordFieldViewController customPasswordFieldRepeat;

    @FXML
    private CustomPasswordFieldViewController customPasswordFieldOldPassword;

    @FXML
    private PasswordQualityBarController masterPasswordQualityBar;

    @FXML
    private CustomExpirationDateViewController customExpirationDateViewController;

    private PMController pmController;
    
    private boolean mode;
    
    private void errorMessage(String title, String content) {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setHeaderText(title);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
        
    }
    
    private boolean setNewPassword(String neu , String wdh){
    	if (neu.equals("")) errorMessage("Passwort Fehler", "Bitte geben sie ein Passwort ein");
    	else if (!neu.equals(wdh)) errorMessage("Passwort Fehler", "Passwort stimmt nicht überein");
    	else {
    		pmController.setMasterPassword(neu);
    		return true;
    	}
    	return false;
    }
    
    private boolean checkOldPassword(String pw){
    	if(pw.equals("")) errorMessage("Password Fehler", "Bitte geben sie ihr Altes Passwort ein");
    	else return pmController.validateMasterPassword(pw);
    	return false;
    }
    public SetMasterPasswordViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SetMasterPasswordView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        mode = true;
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
        
    }
    
    public void setMode(boolean other){
    	mode = other;
    }
    public boolean getMode(){
    	return mode;
    }
    
    @FXML
    void initialize() {
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";
        
        customPasswordFieldBase.onPasswordChanged((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
            		masterPasswordQualityBar.setQuality(0);
            } else {
            		masterPasswordQualityBar.setQuality(pmController.getPasswordController().checkPasswordQuality(newValue));
            }
        });
        
       cancelButton.setOnAction(event ->{
    	   Stage stage = (Stage) getScene().getWindow();
           stage.close();
       });
       
       okButton.setOnAction(event -> {
    	   if (mode){ 
    		   if (!checkOldPassword(customPasswordFieldOldPassword.getText())) {
    			   errorMessage("Passwort Fehler", "Altes Passwort ist Falsch");
    		   }
    		   else if(setNewPassword(customPasswordFieldBase.getText(), customPasswordFieldRepeat.getText())){
    			   Stage stage = (Stage) getScene().getWindow();
    			   stage.close();
    		   }
    	   }else{
    		   if(setNewPassword(customPasswordFieldBase.getText(), customPasswordFieldRepeat.getText())){
    			   Stage stage = (Stage) getScene().getWindow();
    			   stage.close();
    		   }
    	   }
    	   
       });
       
    }
     
    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }
}
