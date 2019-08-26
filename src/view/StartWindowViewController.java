package view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private CustomPasswordFieldViewController customPasswordField;

    public StartWindowViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StartWindowView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
        
        initFileChooser();
    }
    
    private void initFileChooser() {
        fileButton.setOnAction(event -> {
        	Stage dialog = WindowFactory.createStage();
            FileChooser fileChooser = new FileChooser();
            
            if(!choosePasswordAchivePath.getText().equals("")) {
            	Path path = Paths.get(choosePasswordAchivePath.getText());
            	fileChooser.setInitialDirectory(path.getParent().toFile());
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
}
