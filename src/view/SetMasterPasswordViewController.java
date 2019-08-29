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
import javafx.stage.Stage;
import util.PasswordQualityUtil;

public class SetMasterPasswordViewController extends GridPane {
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

    private boolean passwordSet;

    private void errorMessage(String title, String content) {
        Alert errorAlert = new Alert(AlertType.ERROR);
        errorAlert.setHeaderText(title);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();

    }

    private boolean setNewPassword(String newPassword, String repeatedPassword) {
        if (newPassword.equals("")) errorMessage("Passwort-Fehler", "Bitte geben Sie ein Passwort ein.");
        else if (!newPassword.equals(repeatedPassword))
            errorMessage("Passwort-Fehler", "Passwörter stimmen nicht überein!");
        else {
            pmController.setMasterPassword(newPassword);
            return true;
        }
        return false;
    }

    private boolean checkOldPassword(String pwd) {
        if (pwd.isEmpty()) errorMessage("Password-Fehler", "Bitte geben Sie Ihr altes Passwort ein.");
        else return pmController.validateMasterPassword(pwd);
        return false;
    }

    public SetMasterPasswordViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SetMasterPasswordView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        mode = false;

        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
        customPasswordFieldOldPassword.setEnabled(mode);
        passwordSet = false;
        if (!mode) okButton.setText("Erstellen");
    }

    public void setMode(boolean other) {
        mode = other;
        customPasswordFieldOldPassword.setEnabled(mode);
        if (!mode) okButton.setText("Erstellen");
        else okButton.setText("Konfigurieren");
    }

    public boolean getMode() {
        return mode;
    }

    @FXML
    void initialize() {
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'SetMasterPasswordView.fxml'.";

        customPasswordFieldRepeat.setPromptText("Passwort wiederholen");

        customPasswordFieldBase.onPasswordChanged((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                masterPasswordQualityBar.setQuality(0);
            } else {
                masterPasswordQualityBar.setQuality(PasswordQualityUtil.getNormalizedScore(newValue));
            }
        });

        cancelButton.setOnAction(event -> {
            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });

        okButton.setOnAction(event -> {
            if (mode) {
                if (!checkOldPassword(customPasswordFieldOldPassword.getText())) {
                    if (!customPasswordFieldOldPassword.getText().isEmpty())errorMessage("Passwort-Fehler", "Altes Passwort ist falsch!");
                } else if (setNewPassword(customPasswordFieldBase.getText(), customPasswordFieldRepeat.getText())) {
                    passwordSet = true;
                    Stage stage = (Stage) getScene().getWindow();
                    stage.close();
                }
            } else {
                if (setNewPassword(customPasswordFieldBase.getText(), customPasswordFieldRepeat.getText())) {
                    passwordSet = true;
                    Stage stage = (Stage) getScene().getWindow();
                    stage.close();
                }
            }
        });
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }

    public boolean getPasswordSet() {
        return passwordSet;
    }
}
