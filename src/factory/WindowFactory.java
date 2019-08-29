package factory;

import java.util.List;import java.util.Optional;

import application.Main;
import controller.PMController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Entry;
import model.Tag;
import view.CreateModifyEntryViewController;

public class WindowFactory {
    private WindowFactory() {
    }

    public static Stage createStage(String title) {
        // Don't set to utility as that breaks alert windows
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);

        return stage;
    }

    public static Stage createStage() {
        return createStage(null);
    }

    public static Scene createScene(Parent content) {
        Scene scene = new Scene(content);
        scene.getStylesheets().add(Main.class.getResource("resources/application.css").toExternalForm());

        return scene;
    }

    public static void showDialog(String title, Parent content) {
        showDialog(title, content, true);
    }

    public static void showDialog(String title, Parent content, boolean resizable) {
        Stage stage = prepareDialog(title, content, resizable);
        stage.showAndWait();
    }
    
    public static Stage prepareDialog(String title, Parent content, boolean resizable) {
        Stage stage = createStage(title);
        stage.setScene(createScene(content));
        stage.setResizable(resizable);
        return stage;
    }

    public static void showError(String header, String content) {
        createError(header, content).showAndWait();
    }

    public static void showError(String header, String content, String title) {
        createError(header, content, title).showAndWait();
    }

    public static Alert createError(String header, String content) {
        Alert alert = createAlert(Alert.AlertType.ERROR, content);
        alert.setHeaderText(header);

        return alert;
    }

    public static Alert createError(String header, String content, String title) {
        Alert alert = createError(header, content);
        alert.setTitle(title);

        return alert;
    }

    public static Alert createAlert(Alert.AlertType type, String content) {
        Alert alert = new Alert(type, content);
        // Necessary because content text gets truncated otherwise
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        // Initialize Header to null in case someone doesn't want to set it
        alert.setHeaderText(null);

        return alert;
    }
    
    public static void showCreateModifyEntryView(PMController pmController, Entry oldEntry) {
    	CreateModifyEntryViewController controller = new CreateModifyEntryViewController();
    	controller.setPmController(pmController);
    	controller.init();
    	controller.setOldEntry(oldEntry);

    	
    	Stage stage = prepareDialog("Eintrag bearbeiten", controller, false);
    	stage.setOnCloseRequest(evt -> {
    		if (controller.isModified()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Abbrechen bestätigen");
                alert.setHeaderText("Wollen Sie wirklich abbrechen?");
                alert.setContentText("Alle ihre Änderungen gehen verloren!");
                Optional<ButtonType> result = alert.showAndWait();
                
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                	evt.consume();
                	return;
                }
            }
            stage.close();
    	});
    	stage.showAndWait();
    }
    
    public static void showCreateModifyEntryView(PMController pmController, List<Tag> checkedTags) {
    	CreateModifyEntryViewController controller = new CreateModifyEntryViewController();
    	controller.setPmController(pmController);
    	controller.init();
    	controller.setCheckedTags(checkedTags);

    	
    	Stage stage = prepareDialog("Eintrag erstellen", controller, false);
    	stage.setOnCloseRequest(evt -> {
    		if (controller.isModified()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Abbrechen bestätigen");
                alert.setHeaderText("Wollen Sie wirklich abbrechen?");
                alert.setContentText("Alle ihre Änderungen gehen verloren!");
                Optional<ButtonType> result = alert.showAndWait();
                
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                	return;
                }
            }
            stage.close();
    	});
    	stage.showAndWait();
    }
    
    public static void showCreateModifyEntryView(PMController pmController) {
    	CreateModifyEntryViewController controller = new CreateModifyEntryViewController();
    	controller.setPmController(pmController);
    	controller.init();
    	
    	Stage stage = prepareDialog("Eintrag erstellen", controller, false);
    	stage.setOnCloseRequest(evt -> {
    		if (controller.isModified()) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Abbrechen bestätigen");
                alert.setHeaderText("Wollen Sie wirklich abbrechen?");
                alert.setContentText("Alle ihre Änderungen gehen verloren!");
                Optional<ButtonType> result = alert.showAndWait();
                
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                	return;
                }
            }
            stage.close();
    	});
    	stage.showAndWait();
    }
}
