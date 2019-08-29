package view;

import java.io.IOException;
import java.util.HashMap;

import controller.PMController;
import controller.PasswordController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import util.CharGroup;

import static util.CharGroup.*;

import util.PasswordGeneratorSettings;
import util.PasswordQualityUtil;
import factory.WindowFactory;

public class GeneratePasswordViewController extends GridPane {

    private static final HashMap<String, CharGroup> NAME_TO_CHAR_GROUP = new HashMap<>();

    static {
        NAME_TO_CHAR_GROUP.put("Kleinbuchstaben", LOWER_CASE_LETTER);
        NAME_TO_CHAR_GROUP.put("Großbuchstaben", UPPER_CASE_LETTER);
        NAME_TO_CHAR_GROUP.put("Zahlen", NUMBERS);
        NAME_TO_CHAR_GROUP.put("Sonderzeichen", SPECIAL_CHARS);
    }

    @FXML
    private TitledPane signList;

    @FXML
    private Button pwButton;

    @FXML
    private Button accButton;

    @FXML
    private Button canButton;

    @FXML
    private PasswordQualityBarController securityBar;

    @FXML
    private Slider lengthSlider;

    @FXML
    private Spinner<Integer> lengthSpinner;

    @FXML
    private CustomPasswordFieldViewController pwField;

    private SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory;

    private PMController pmController;

    private boolean okay = false;

    @FXML
    void initialize() {
        assert signList != null : "fx:id=\"signList\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert pwButton != null : "fx:id=\"pwButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert accButton != null : "fx:id=\"accButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert canButton != null : "fx:id=\"canButton\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert securityBar != null : "fx:id=\"securityBar\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert lengthSlider != null : "fx:id=\"lengthSlider\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert lengthSpinner != null : "fx:id=\"LengthSpinner\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";
        assert pwField != null : "fx:id=\"pwField\" was not injected: check your FXML file 'GeneratePasswordView.fxml'.";

        spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 256);

        lengthSpinner.setValueFactory(spinnerValueFactory);

        lengthSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double aDouble) {
                if (aDouble == 1.0) return "kurz";
                else if (aDouble == 2.0) return "normal";
                else if (aDouble == 3.0) return "lang";
                return "-";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "kurz":
                        return 1.0;
                    case "normal":
                        return 2.0;
                    case "lang":
                        return 3.0;
                }
                return 0.0;
            }
        });

        lengthSpinner.getEditor().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty()) {
                    try {
                        long pointI = Integer.parseInt(newValue);
                        lengthSpinner.getEditor().setText(String.valueOf(pointI));
                        lengthSpinner.increment(0);
                    } catch (Exception e) {
                    	lengthSpinner.getEditor().clear();
                    	lengthSpinner.getEditor().setText(getNumber(oldValue));
                    	lengthSpinner.increment(0);
                    }
                } else {
                	lengthSpinner.getEditor().clear();
                	lengthSpinner.getEditor().setText(getNumber(oldValue));
                	lengthSpinner.increment(0);
                }
            }
        });

        lengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            spinnerValueFactory.setValue(8 * newVal.intValue());
        });

        spinnerValueFactory.valueProperty().addListener((obs, oldVal, newVal) -> {
            lengthSlider.setValue(Math.min(3, Math.max(1, (int) (newVal / 8))));
            spinnerValueFactory.setValue(newVal);
        });
        spinnerValueFactory.setValue(12);

        pwButton.setOnAction(event -> this.generatePassword());

        pwField.onPasswordChanged((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                securityBar.setQuality(0);
            } else {
                securityBar.setQuality(PasswordQualityUtil.getNormalizedScore(newValue));
            }
        });

        canButton.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            setPassword("");
            stage.close();
        });

        accButton.setOnAction(e -> {
            Stage stage = (Stage) getScene().getWindow();
            if (getPassword().equals(""))
                errorMessage(
                        "Kein Passwort generiert",
                        "Sie haben kein Passwort eingegeben oder generieren lassen!"
                );
            else {
                stage.close();
                okay = true;
            }
        });
    }

    /**
     * Helper method to show an Alert dialog.
     *
     * @param title   Title of the Alert dialog.
     * @param content Content of the Alert dialog.
     */
    void errorMessage(String title, String content) {
        WindowFactory.showError(title, content);
    }

    public GeneratePasswordViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GeneratePasswordView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void setPmController(PMController pmController) {
        this.pmController = pmController;
    }

    /**
     * Generate a random password and updates the password field.
     */
    private void generatePassword() {
        PasswordController pwController = pmController.getPasswordController();
        PasswordGeneratorSettings pwGenSettings = new PasswordGeneratorSettings();

        pwGenSettings.setLength(spinnerValueFactory.getValue());

        for (Node node : ((GridPane) signList.getContent()).getChildren()) {
            CheckBox actualNode = (CheckBox) node;
            if (actualNode.isSelected()) pwGenSettings.selectCharGroup(NAME_TO_CHAR_GROUP.get(actualNode.getText()));
        }

        pwField.setText(pwController.generatePassword(pwGenSettings));
    }

    private String getNumber(String value) {
        String n = "";
        try {
            return String.valueOf(Integer.parseInt(value));
        } catch (Exception e) {
            String[] array = value.split("");
            for (String tab : array) {
                try {
                    //System.out.println(tab);
                    n = n.concat(String.valueOf(Integer.parseInt(String.valueOf(tab))));
                } catch (Exception ex) {
                    //System.out.println("not nomber");
                }
            }
            return n;
        }
    }

    public String getPassword() {
        return pwField.getText();
    }

    public void setPassword(String password) {
        pwField.setText(password);
    }

    public boolean receivedOkay() {
        return okay;
    }
}
