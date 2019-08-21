package view;

/**
 * Sample Skeleton for 'CustomPasswordField.fxml' Controller Class
 */

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CustomPasswordFieldViewController extends HBox {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="textField"
    private TextField textField; // Value injected by FXMLLoader

    @FXML // fx:id="passwordField"
    private PasswordField passwordField; // Value injected by FXMLLoader

    @FXML // fx:id="eyeButton"
    private ToggleButton eyeButton; // Value injected by FXMLLoader

    @FXML // fx:id="copyButton"
    private Button copyButton; // Value injected by FXMLLoader

    public CustomPasswordFieldViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomPasswordField.fxml"));
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
        assert passwordField != null: "fx:id=\"passwordField\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";
        assert textField != null : "fx:id=\"textField\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";
        assert eyeButton != null : "fx:id=\"eyeButton\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";
        assert copyButton != null : "fx:id=\"checkButton\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";

        Image eyeButtonImage = new Image(getClass().getResourceAsStream("/view/resources/visible_password_icon.png"));
        Image strokeEyeButtonImage = new Image(getClass().getResourceAsStream("/view/resources/hidden_password_icon.png"));
        eyeButton.setGraphic(new ImageView(strokeEyeButtonImage));

        Image copyButtonImage = new Image(getClass().getResourceAsStream("/view/resources/copy_password_icon.png"));
        Image copyButtonSelectedImage = new Image(getClass().getResourceAsStream("/view/resources/copy_password_icon_highlighted.png"));
        copyButton.setGraphic(new ImageView(copyButtonImage));

        textField.managedProperty().bind(eyeButton.selectedProperty());
        textField.visibleProperty().bind(eyeButton.selectedProperty());

        passwordField.managedProperty().bind(eyeButton.selectedProperty().not());
        passwordField.visibleProperty().bind(eyeButton.selectedProperty().not());

        textField.textProperty().bindBidirectional(passwordField.textProperty());

        eyeButton.setOnAction(event -> {
            Image eyeButtonToggleImage = eyeButton.isSelected() ? eyeButtonImage : strokeEyeButtonImage;
            eyeButton.setGraphic(new ImageView(eyeButtonToggleImage));
        });

        copyButton.setOnAction(event -> {
            copyButton.setGraphic(new ImageView(copyButtonSelectedImage));

            Clipboard clipboard = Clipboard.getSystemClipboard();
            HashMap<DataFormat, Object> clipboardMap = new HashMap<>();
            clipboardMap.put(DataFormat.PLAIN_TEXT, textField.getText());
            clipboard.setContent(clipboardMap);

            PauseTransition delay = new PauseTransition(Duration.millis(200));
            delay.setOnFinished(finished -> copyButton.setGraphic(new ImageView(copyButtonImage)));
            delay.play();
        });
    }

    /**
     * Set prompt text of the password field manually.
     * @param prompt Prompt text to be displayed inside the password field.
     */
    public void setPromptText(String prompt) {
        passwordField.setPromptText(prompt);
    }
}

