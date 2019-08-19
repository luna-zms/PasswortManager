package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.MainWindowViewController;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MainWindowViewController mainWindowViewController = new MainWindowViewController();
            Scene scene = new Scene(mainWindowViewController);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
