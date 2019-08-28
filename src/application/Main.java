package application;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import controller.*;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.PasswordManager;
import model.Tag;
import util.CsvException;
import util.WindowFactory;
import view.MainWindowViewController;
import view.SetMasterPasswordViewController;
import view.StartWindowViewController;


public class Main extends Application {
    private MainWindowViewController mainWindowViewController;
    private PMController pmController;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        pmController = new PMController();

        PasswordManager passwordManager = new PasswordManager();
        pmController.setPasswordManager(passwordManager);

        pmController.setPasswordController(new PasswordController());
        pmController.setEntryController(new EntryController(pmController));
        pmController.setTagController(new TagController());
        pmController.setLoadSaveController(new LoadSaveController(passwordManager));
        pmController.setImportExportController(new ImportExportController(passwordManager));

        pmController.getTagController().setPMController(pmController);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            mainWindowViewController = new MainWindowViewController();
            mainWindowViewController.setPmController(pmController);
            primaryStage.setScene(WindowFactory.createScene(mainWindowViewController));
            primaryStage.show();

            showOpenDialog(null);
        } catch (Exception e) {
            WindowFactory.showError("Kritischer Fehler",
                                    "Aufgrund eines kritischen Fehlers muss die Anwendung beendet werden.\n\nNähere Informationen:\n" + e
                                            .getMessage());
        }
    }

    public void showOpenDialog(Path initialPath) {
        boolean successfulInit;
        do {
            StartWindowViewController startWindowViewController = new StartWindowViewController();
            if (initialPath != null) startWindowViewController.setPath(initialPath);

            WindowFactory.showDialog("Datenbank öffnen", startWindowViewController);

            Optional<Boolean> tmp = initApplication(startWindowViewController);
            if (!tmp.isPresent()) {
                primaryStage.close();
                return;
            } else {
                successfulInit = tmp.get();
            }
        } while (!successfulInit);
    }

    private Optional<Boolean> initApplication(StartWindowViewController startWindowViewController) {
        // I felt dirty writing this tbh
        if (startWindowViewController.getPath() == null) return Optional.empty();

        boolean ret = true;
        Path path = startWindowViewController.getPath();
        if (startWindowViewController.create()) {
            pmController.setSavePath(path);

            SetMasterPasswordViewController setMasterPasswordViewController = new SetMasterPasswordViewController();
            setMasterPasswordViewController.setPmController(pmController);
            setMasterPasswordViewController.setMode(false);
            WindowFactory.showDialog("Master-Passwort setzen", setMasterPasswordViewController);

            // User pressed cancel or closed window => go back to start
            if (!setMasterPasswordViewController.getPasswordSet()) {
                ret = false;
            }
            // Root tag is null otherwise
            else {
                pmController.getPasswordManager().setRootTag(getRootTagFromPath(path));
            }
        } else {
            pmController.setMasterPassword(startWindowViewController.getPassword());
            pmController.setSavePath(path);

            try {
                // Hack to not have to copy `ret = false` into each catch
                ret = false;
                pmController.getLoadSaveController().load(path);
                ret = true;
            } catch (CsvException e) {
                WindowFactory.showError("Die Datei ist vermutlich beschädigt.",
                                        "Aufgrund von internen Formatfehlern konnte die Datei nicht geladen werden.\n\nNähere Informationen:\n" + e
                                                .getMessage());
            } catch (IOException e) {
                WindowFactory.showError("Datei konnte nicht geöffnet werden.",
                                        "Aufgrund eines unspezifizierten Fehlers ist das Öffnen der Datei fehlgeschlagen.\n\nNähere Informationen:\n" + e
                                                .getLocalizedMessage());
            }

            checkMasterPasswordExpiration();
        }

        if (ret) mainWindowViewController.init(this::showOpenDialog);
        return Optional.of(ret);
    }

    private void checkMasterPasswordExpiration() {
        if (!pmController.getPasswordManager().getValidUntil().isBefore(LocalDateTime.now())) {
            return;
        }

        Alert alert = WindowFactory.createAlert(Alert.AlertType.INFORMATION,
                                                "Das Master-Passwort für diese Datenbank ist abgelaufen.");
        alert.setHeaderText("Master-Passwort abgelaufen");
        alert.showAndWait();

        SetMasterPasswordViewController setMasterPasswordViewController = new SetMasterPasswordViewController();
        setMasterPasswordViewController.setPmController(pmController);
        setMasterPasswordViewController.setMode(true);
        WindowFactory.showDialog("Master-Passwort ändern", setMasterPasswordViewController);
    }

    private Tag getRootTagFromPath(Path path) {
        String fileNameWithExt = path.getFileName().toString();
        String fileName = fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf(".pwds"));

        return new Tag(fileName);
    }
}
