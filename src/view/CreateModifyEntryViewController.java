package view;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Entry;
import model.SecurityQuestion;
import model.Tag;
import util.WindowFactory;
import util.PasswordQualityUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateModifyEntryViewController extends AnchorPane {

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

        passwordQualityBar.setQuality(0);

        cancelButton.setOnAction(event -> {
            boolean cancel = !isModified();
            // Modified:
            if (!cancel) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Abbrechen bestätigen");
                alert.setHeaderText("Wollen Sie wirklich abbrechen?");
                alert.setContentText("Alle ihre Änderungen gehen verloren!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) cancel = true;
            }
            if (cancel) {
                Stage stage = (Stage) getScene().getWindow();
                stage.close();
            }
        });

        okButton.setOnAction(event -> {
            String entryNameString = entryName.getText();
            String passwordString = password.getText();
            String repeatPasswordString = repeatPassword.getText();

            if (entryNameString.isEmpty()) {
                errorMessage(errorTitle, "Eintragsname ist leer", "Das Feld Eintragsname darf nicht leer sein.");
                return;
            }

            if (passwordString.isEmpty()) {
                errorMessage(errorTitle, "Passwort ist leer", "Das Feld Passwort darf nicht leer sein.");
                return;
            }

            if (!passwordString.equals(repeatPasswordString)) {
                errorMessage(errorTitle, "Passwörter sind nicht gleich", "Bitte geben sie zweimal das gleiche Passwort ein.");
                return;
            }

            Entry newEntry = new Entry(entryNameString, passwordString);
            newEntry.setUsername(userName.getText());
            try {
                URL rUrl = null;
                if (!url.getText().isEmpty()) rUrl = new URL(url.getText());
                newEntry.setUrl(rUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                errorMessage(errorTitle, "Ungültige URL", "Tragen Sie bitte eine gültige URL ein oder " +
                        "lassen Sie das Feld leer!");
                return;
            }
            newEntry.setValidUntil(validDatePicker.getExpirationDate());
            newEntry.setNote(notes.getText());
            newEntry.getTags().addAll(tagTree.getCheckedTags());
            newEntry.getTags().add(pmController.getPasswordManager().getRootTag());

            if (oldEntry == null) {
                newEntry.setCreatedAt(LocalDateTime.now());
                pmController.getEntryController().addEntry(newEntry);
            } else {
                newEntry.setCreatedAt(oldEntry.getCreatedAt());
                newEntry.setLastModified(oldEntry.getPassword().equals(newEntry.getPassword()) ?
                        oldEntry.getLastModified() : LocalDateTime.now());
                pmController.getEntryController().editEntry(oldEntry, newEntry);
            }

            Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });

        generatePasswordButton.setOnAction(event -> {
            GeneratePasswordViewController dialogController = new GeneratePasswordViewController();
            dialogController.setPmController(pmController);
            WindowFactory.showDialog("Passwort generieren", dialogController, false);
            if (dialogController.receivedOkay()) {
                password.setText(dialogController.getPassword());
                String genPassword = dialogController.getPassword();
                repeatPassword.setText(genPassword);
            }
        });

        password.onPasswordChanged((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                passwordQualityBar.setQuality(0);
            } else {
                passwordQualityBar.setQuality(PasswordQualityUtil.getNormalizedScore(newValue));
            }
        });
    }

    /**
     * Helper method to show an Alert dialog.
     *
     * @param title   Title of the Alert dialog.
     * @param header  Header of the Alert dialog.
     * @param content Content of the Alert dialog.
     */
    private void errorMessage(String title, String header, String content) {
        WindowFactory.showError(header, content, title);
    }

    private boolean isModified() {
        if (oldEntry == null) {
            if (!entryName.getText().isEmpty()) return true;
            if (!userName.getText().isEmpty()) return true;
            if (!password.getText().isEmpty()) return true;
            if (!repeatPassword.getText().isEmpty()) return true;
            if (!url.getText().isEmpty()) return true;
            if (!securityQuestion.getText().isEmpty()) return true;
            if (!answer.getText().isEmpty()) return true;
            if (!notes.getText().isEmpty()) return true;

            return false;
        } else {
            if (!entryName.equals(oldEntry.getTitle())) return true;
            if (!userName.equals(oldEntry.getUsername())) return true;
            if (!password.equals(oldEntry.getPassword())) return true;
            if (!repeatPassword.equals(oldEntry.getPassword())) return true;
            if (!url.equals(oldEntry.getUrlString())) return true;

            SecurityQuestion question = oldEntry.getSecurityQuestion();

            if (!securityQuestion.equals(question.getQuestion())) return true;
            if (!answer.equals(question.getAnswer())) return true;
            if (!notes.equals(oldEntry.getNote())) return true;

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

        Image generatePasswordImage = new Image(getClass().getResourceAsStream("/view/resources/generate_password_toolbar_icon_small.png"));
        generatePasswordButton.setGraphic(new ImageView(generatePasswordImage));
    }

    public void setOldEntry(Entry entry) {
        oldEntry = entry;
        entryName.setText(entry.getTitle());
        userName.setText(entry.getUsername());
        password.setText(entry.getPassword());
        repeatPassword.setText(entry.getPassword());
        url.setText(entry.getUrlString());
        validDatePicker.setExpirationDate(entry.getValidUntil());
        validDatePicker.checkExpirationDate();
        SecurityQuestion question = entry.getSecurityQuestion();
        securityQuestion.setText(question.getQuestion());
        answer.setText(question.getAnswer());
        notes.setText(entry.getNote());
        tagTree.setCheckedTags(entry.getTags());
    }

    public void setCheckedTags(List<Tag> tags) {
        tagTree.setCheckedTags(tags);
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }

    public void init() {
        tagTree.init(true, pmController);
        tagTree.setShowRoot(false);
    }
}
