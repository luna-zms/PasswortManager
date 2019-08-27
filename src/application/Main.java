package application;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;

import controller.*;
import javafx.application.Application;
import javafx.stage.Stage;
import model.Entry;
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

        PasswordManager passwordManager = new PasswordManager(null);
        pmController.setPasswordManager(passwordManager);

        pmController.setPasswordController(new PasswordController());
        pmController.setEntryController(new EntryController(pmController));
        pmController.setTagController(new TagController());
        pmController.setLoadSaveController(new LoadSaveController(passwordManager));
        pmController.setImportExportController(new ImportExportController(passwordManager));

        // TODO: Replace with actually loading the data
        try {
            pmController.getImportExportController().load(
                    Paths.get(getClass().getResource("/application/resources/test_entries.csv").toURI())
            );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            MainWindowViewController mainWindowViewController = new MainWindowViewController();
            mainWindowViewController.setPmController(pmController);
            mainWindowViewController.init();
            primaryStage.setScene(WindowFactory.createScene(mainWindowViewController));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
