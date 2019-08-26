package application;

import controller.*;
import javafx.application.Application;
import javafx.stage.Stage;
import model.PasswordManager;
import model.Tag;
import util.WindowFactory;
import view.MainWindowViewController;


public class Main extends Application {
    private PMController pmController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        pmController = new PMController();
        pmController.setPasswordController(new PasswordController());
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            MainWindowViewController mainWindowViewController = new MainWindowViewController();
            mainWindowViewController.setPmController(pmController);
            primaryStage.setScene(WindowFactory.createScene(mainWindowViewController));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
