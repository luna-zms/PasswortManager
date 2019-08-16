import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class SetMaser {

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
	private CustomPasswordFieldViewController passwordFieldBaseController;
	
	@FXML
	private HBox passwordFieldRepeat;
	
	@FXML
	private CustomPasswordFieldViewController passwordFieldReapeatController;
	
    @FXML
    void initialize() {
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";

    }
}

