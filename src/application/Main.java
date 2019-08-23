package application;

import controller.PMController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.MainWindowViewController;


public class Main extends Application {
    private PMController pmController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        pmController = new PMController();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            MainWindowViewController mainWindowViewController = new MainWindowViewController();
            mainWindowViewController.setPmController(pmController);
            Scene scene = new Scene(mainWindowViewController);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
