package view;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class SetMasterPasswordViewController extends GridPane
{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
    
	@FXML
	private HBox passwordFieldBase;
	
	@FXML
	private CustomPasswordFieldViewController customPasswordFieldBaseViewController;
	
	@FXML
	private HBox passwordFieldRepeat;
	
	@FXML
	private CustomPasswordFieldViewController customPasswordFieldViewController;
	
	@FXML
	private HBox passwordFieldOldPassword;
	
	@FXML 
	private CustomPasswordFieldViewController customPasswordFieldOldPasswordViewController;
	
	@FXML
	private PasswordQualityBarController masterPasswordQualityBar;
	
	public SetMasterPasswordViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SetMasterPasswordView.fxml"));
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
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";
    }
}