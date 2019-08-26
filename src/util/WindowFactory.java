package util;

import application.Main;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

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
}
