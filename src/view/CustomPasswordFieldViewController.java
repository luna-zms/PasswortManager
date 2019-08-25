package view;

import java.io.IOException;
import java.util.HashMap;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.InputMethodEvent;

import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class CustomPasswordFieldViewController extends HBox {

    @FXML // fx:id="textField"
    private TextField textField; // Value injected by FXMLLoader

    @FXML // fx:id="passwordField"
    private PasswordField passwordField; // Value injected by FXMLLoader

    @FXML // fx:id="eyeButton"
    private ToggleButton eyeButton; // Value injected by FXMLLoader

    @FXML // fx:id="copyButton"
    private Button copyButton; // Value injected by FXMLLoader

    @FXML // fx:id="toggleShowPassword"
    private Tooltip toggleShowPassword;

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
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'CustomPasswordField.fxml'.";
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
            toggleShowPassword.setText(eyeButton.isSelected() ? "Passwort ausblenden" : "Passwort anzeigen");
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
     *
     * @param prompt Prompt text to be displayed inside the password field.
     */
    public void setPromptText(String prompt) {
        passwordField.setPromptText(prompt);
        textField.setPromptText(prompt);
    }

    /**
     * Set content of the password field manually.
     *
     * @param text Self-explaining
     */
    public void setText(String text) {
        passwordField.setText(text);
    }

    /**
     * Get content of the password field manually.
     *
     * @returns You may have a guess
     */
    public String getText() {
        return passwordField.getText();
    }

    /**
     * Choose whether to hide the copy button
     *
     * @param hide true if the copy button should not be shown else (default) false
     */
    public void hideCopyButton(boolean hide) {
        copyButton.setManaged(!hide);
        copyButton.setVisible(!hide);
    }

    /**
     * Register an event handler to be called as the password field's value changes.
     * @param handler
     */
    public void onPasswordChanged(ChangeListener<? super String> handler) {
        textField.textProperty().addListener(handler);
    }
}