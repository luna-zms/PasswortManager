package factory;

import application.Main;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        Stage stage = createStage(title);
        stage.setScene(createScene(content));
        stage.setResizable(resizable);
        stage.showAndWait();
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
}
