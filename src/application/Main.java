package application;

import java.net.MalformedURLException;
import java.net.URL;
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
        pmController.setPasswordController(new PasswordController());
        PasswordManager passwordManager = new PasswordManager(null);
        // TODO: Replace with actually loading the data
        initSampleData(passwordManager);
        pmController.setPasswordManager(passwordManager);
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

    private void initSampleData(PasswordManager passwordManager) {
        Tag rootTag = new Tag("Root Tag");
        Tag subTag = new Tag("Subtag");
        Tag subSubTag = new Tag("Subsubtag");
        subTag.getSubTags().add(subSubTag);
        rootTag.getSubTags().add(subTag);

        passwordManager.setRootTag(rootTag);

        Entry entry = new Entry("Hello there", "asdf");
        entry.getTags().add(rootTag);
        entry.getTags().add(subSubTag);
        entry.setUsername("userSample");

        URL url = null;
        try {
            url = new URL("http://example.com");
        } catch (MalformedURLException e) {
        }

        entry.setUrl(url);
        entry.setValidUntil(LocalDate.MAX);

        passwordManager.getEntries().add(entry);

        entry = new Entry("General Kenobi", "urabold1");
        passwordManager.getEntries().add(entry);
    }
}
