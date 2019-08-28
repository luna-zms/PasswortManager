package view;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import controller.PMController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.WindowFactory;

public class StartWindowViewController extends GridPane {

    @FXML
    private TextField choosePasswordAchivePath;

    @FXML
    private Button fileButton;

    @FXML
    private Button openButton;

    @FXML
    private Button createArchiveButton;

    @FXML
    private CustomPasswordFieldViewController customPasswordField;

    private Path path = null;

    private boolean createNew = false;

    public StartWindowViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StartWindowView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }

        loadPathFromProps();

        initFileChooser();

        openButton.setOnAction(event -> {
        	if(choosePasswordAchivePath.getText().isEmpty()) {
        		WindowFactory.showError("Pfad nicht gesetzt", "Bitte geben sie einen Pfad zu einem Archiv an oder erstellen Sie ein neues Archiv");
        		return;
        	}
        	File file = new File(choosePasswordAchivePath.getText());
        	if (!file.isFile()) {
        		WindowFactory.showError("Fehler beim Öffnen", "Die gewählte Datei existiert nicht!");
                return;
        	}

        	if (!file.getPath().endsWith(".pwds")) {
        		WindowFactory.showError("Fehler beim Öffnen", "Die gewählte Datei ist kein Passwort-Archiv!");
                return;
        	}
        	if(customPasswordField.getText().isEmpty()) {
        		WindowFactory.showError("Passwortfeld ist leer", "Bitte geben sie das Paswort für das gewählte Archiv ein oder erstellen Sie ein neues Archiv");
        		return;
        	}
        	path = file.toPath();
        	Stage stage = (Stage) getScene().getWindow();
            stage.close();
        });

        createArchiveButton.setOnAction(event -> {
        	Stage dialog = WindowFactory.createStage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.setTitle("Erstelle neues Passwort-Archiv");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-Dateien", "*.pwds");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showSaveDialog(dialog);
            if (file == null) return;
            path = file.toPath();
            if(!file.getPath().endsWith(".pwds")) {
            	path = Paths.get(path.toString() + ".pwds");
            }
            Stage stage = (Stage) getScene().getWindow();
            createNew = true;
            stage.close();
        });

    }

    private void loadPathFromProps() {
        try (FileInputStream fis = new FileInputStream(PMController.configFile);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)
        ) {
            Properties properties = new Properties();
            properties.load(reader);

            setPath(Paths.get(properties.getProperty("savePath")));
        } catch (IOException e) {
            // No need to do anything as it's just a convenience feature
        }
    }

    private void initFileChooser() {
        fileButton.setOnAction(event -> {
        	Stage dialog = WindowFactory.createStage();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            if(!choosePasswordAchivePath.getText().isEmpty()) {
            	Path path = Paths.get(choosePasswordAchivePath.getText());
            	if(path.toFile().exists()) {
            		fileChooser.setInitialDirectory(path.getParent().toFile());
            	}
            }
            fileChooser.setTitle("Wähle Passwort-Archiv");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PasswortManager-Dateien", "*.pwds");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setSelectedExtensionFilter(extFilter);
            File file = fileChooser.showOpenDialog(dialog);

            if (file == null) return;
            choosePasswordAchivePath.setText(file.toString());
        });
    }

    public Path getPath() {
    	return path;
    }

    public void setPath(Path path) {
    	choosePasswordAchivePath.setText(path.toString());
    }

    public String getPassword() {
    	return customPasswordField.getText();
    }

  //If true, new archive will be created
    public boolean create() {
    	return createNew;
    }
}
