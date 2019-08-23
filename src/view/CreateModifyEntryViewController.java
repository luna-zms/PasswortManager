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
import model.Entry;
import model.SecurityQuestion;

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
    private CustomExpirationDateViewController validDatePicker;

    @FXML
    private TextField securityQuestion;

    @FXML
    private TextField answer;

    @FXML
    private TextArea notes;

    @FXML
    private TagTree tagTree;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
    
    private Entry oldEntry = null;
    
    private PMController pmController = null;
    
    public CreateModifyEntryViewController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateModifyEntryView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		repeatPassword.setPromptText("Passwort wiederholen");
		String errorTitle = "Fehler: Eintrag erstellen";
		
		cancelButton.setOnAction(event -> {
			if(isModified()){
				//TODO: open dialog
			} else {
				Stage stage = (Stage) getScene().getWindow();
				stage.close();
			}
		});
		
		okButton.setOnAction(event -> {
			String entryNameString = entryName.getText();
			String passwordString = password.getText();		//TODO: Implement getText in CutomPasswordField
			String repeatPasswordString = repeatPassword.getText();
			
			if(entryNameString.isEmpty()) {
				errorMessage(errorTitle, "Eintragsname ist leer" ,"Das Feld Eintragsname darf nicht leer sein.");
				return;
			}
			
			if(!passwordString.equals(repeatPasswordString)) {
				errorMessage(errorTitle, "Passwörter sind nicht gleich", "Bitte geben sie zweimal das gleiche Passwort ein.");
				return;
			}
			Stage stage = (Stage) getScene().getWindow();
			stage.close();
		});
	}
    
    private void errorMessage(String title, String header, String content) {
    	Alert errorAlert = new Alert(AlertType.ERROR);
    	errorAlert.setTitle(title);
    	errorAlert.setHeaderText(header);
    	errorAlert.setContentText(content);
    	errorAlert.showAndWait();
    }
    
    private boolean isModified() {
    	if(oldEntry == null) {
    		if(!entryName.getText().isEmpty()) return true;
    		if(!userName.getText().isEmpty()) return true;
    		if(!password.getText().isEmpty()) return true;
    		if(!repeatPassword.getText().isEmpty()) return true;
    		if(!url.getText().isEmpty()) return true;
    		if(!securityQuestion.getText().isEmpty()) return true;
    		if(!answer.getText().isEmpty()) return true;
    		if(!notes.getText().isEmpty()) return true;
    		
    		return false;
    	} else {
    		if(entryName.equals(oldEntry.getTitle())) return true;
    		if(userName.equals(oldEntry.getUsername())) return true;
    		if(password.equals(oldEntry.getPassword())) return true;
    		if(repeatPassword.equals(oldEntry.getPassword())) return true;
    		if(url.equals(oldEntry.getUrlString())) return true;
    		
    		SecurityQuestion question = oldEntry.getSecurityQuestion();
    		
    		if(securityQuestion.equals(question.getQuestion())) return true;
    		if(answer.equals(question.getAnswer())) return true;
    		if(notes.equals(oldEntry.getNote())) return true;
    		
    		return false;
    	}
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
        assert notes != null : "fx:id=\"notes\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert tagTree != null : "fx:id=\"tagTree\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'CreateModifyEntryView.fxml'.";

    }
    public void setOldEntry(Entry entry) {
    	oldEntry = entry;
    	entryName.setText(entry.getTitle());
    	userName.setText(entry.getUsername());
    	//password.setText(entry.getPassword());
    	//repeatPassword.setText(entry.getPassword());
    	url.setText(entry.getUrlString());
    	//validDatePicker.setDate(entry.getValidUntil());
    	SecurityQuestion question = entry.getSecurityQuestion();
    	securityQuestion.setText(question.getQuestion());
    	answer.setText(question.getAnswer());
    	notes.setText(entry.getNote());
    	//tagTree.setCheckedTags(entry.getTags());
    }
    
    public void setPmController(PMController pmController) {
        this.pmController = pmController;
        tagTree.setPmController(pmController);
    }
    
    public void init() {
    	tagTree.init(true, pmController.getPasswordManager().getRootTag());
    }
}
