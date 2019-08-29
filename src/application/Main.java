package application;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import controller.*;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.PasswordManager;
import model.Tag;
import util.BadPasswordException;
import util.CsvException;
import factory.WindowFactory;
import view.MainWindowViewController;
import view.SetMasterPasswordViewController;
import view.StartWindowViewController;


public class Main extends Application {
    private MainWindowViewController mainWindowViewController;
    private PMController pmController;

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
        pmController.setLoadSaveController(new LoadSaveController(pmController));
        pmController.setImportExportController(new ImportExportController(passwordManager));

        pmController.getTagController().setPMController(pmController);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Gatekeeper");
            mainWindowViewController = new MainWindowViewController();
            mainWindowViewController.setPmController(pmController);
            primaryStage.setScene(WindowFactory.createScene(mainWindowViewController));

            primaryStage.setOnCloseRequest(evt -> {
                if (pmController.isDirty()) {
                    Alert alert = WindowFactory.createAlert(Alert.AlertType.CONFIRMATION, "Sie haben ungespeicherte Änderungen. Alle ihre Änderungen gehen verloren!");
                    alert.setTitle("Beenden bestätigen");
                    alert.setHeaderText("Wollen Sie GateKeeper wirklich beenden?");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        primaryStage.close();
                        return;
                    }
                }

                evt.consume();
            });

            primaryStage.show();

            if (!showOpenDialog(null)) primaryStage.close();
        } catch (Exception e) {
            WindowFactory.showError("Kritischer Fehler",
                                    "Aufgrund eines kritischen Fehlers muss die Anwendung beendet werden.\n\nNähere Informationen:\n" + e
                                            .getMessage());
        }
    }

    private boolean showOpenDialog(Path initialPath) {
        boolean successfulInit;
        do {
            StartWindowViewController startWindowViewController = new StartWindowViewController();
            if (initialPath != null) startWindowViewController.setPath(initialPath);

            WindowFactory.showDialog("Datenbank öffnen", startWindowViewController, false);

            Optional<Boolean> tmp = initApplication(startWindowViewController);
            if (!tmp.isPresent()) {
                return false;
            } else {
                successfulInit = tmp.get();
            }
        } while (!successfulInit);

        return true;
    }

    private boolean initApplicationWithCreate(Path path) {
        pmController.setSavePath(path);

        SetMasterPasswordViewController setMasterPasswordViewController = new SetMasterPasswordViewController();
        setMasterPasswordViewController.setPmController(pmController);
        setMasterPasswordViewController.setMode(false);
        WindowFactory.showDialog("Master-Passwort setzen", setMasterPasswordViewController);

        boolean ret = true;

        // User pressed cancel or closed window => go back to start
        if (!setMasterPasswordViewController.getPasswordSet()) {
            ret = false;
        }
        // Root tag is null otherwise
        else {
            PasswordManager passwordManager = pmController.getPasswordManager();
            passwordManager.getEntries().clear();
            passwordManager.setRootTag(getRootTagFromPath(path));
            pmController.setPasswordManager(passwordManager);
            try {
                pmController.getLoadSaveController().save(path);
            } catch (IOException exc) {
                Alert alert = WindowFactory.createAlert(Alert.AlertType.WARNING,
                                                        "Konnte Datei nicht speichern. Bitte manuell speichern.\n\nNähere Informationen:\n" +
                                                        exc.getLocalizedMessage());

                alert.setHeaderText("Dateifehler");

                alert.showAndWait();
            }
        }

        return ret;
    }

    private boolean initApplicationWithOpen(Path path, String password) {
        pmController.setMasterPassword(password);
        pmController.setSavePath(path);

        boolean ret = true;

        try {
            // Hack to not have to copy `ret = false` into each catch
            ret = false;
            pmController.getLoadSaveController().load(path);
            ret = true;
        } catch (CsvException csvException) {
            WindowFactory.showError("Die Datei ist vermutlich beschädigt.",
                    "Aufgrund von internen Formatfehlern konnte die Datei nicht geladen werden.\n\nNähere Informationen:\n" + csvException
                            .getMessage());
        } catch (BadPasswordException badPasswordException) {
            WindowFactory.showError("Falsches Passwort",
                    "Die Datei kann nicht sinnvoll entschlüsselt werden. Geben Sie das Passwort erneut ein.");
        } catch (IOException ioException) {
            WindowFactory.showError("Datei konnte nicht geöffnet werden.",
                    "Aufgrund eines unspezifizierten Fehlers ist das Öffnen der Datei fehlgeschlagen.\n\nNähere Informationen:\n" + ioException
                            .getLocalizedMessage());
        } catch (RuntimeException runtimeException) {
            WindowFactory.showError("Interner Fehler beim Laden", "Die Datei konnte aufgrund eines unspezifizierten Fehlers nicht geladen werden:\n\n"
                    + runtimeException.getLocalizedMessage());
        }

        checkMasterPasswordExpiration();

        return ret;
    }

    private Optional<Boolean> initApplication(StartWindowViewController startWindowViewController) {
        // I felt dirty writing this tbh
        if (startWindowViewController.getPath() == null) return Optional.empty();

        boolean ret;
        Path path = startWindowViewController.getPath();
        if (startWindowViewController.create()) {
            ret = initApplicationWithCreate(path);
        } else {
            ret = initApplicationWithOpen(path, startWindowViewController.getPassword());
        }

        if (ret) mainWindowViewController.init(this::showOpenDialog);
        return Optional.of(ret);
    }

    private void checkMasterPasswordExpiration() {
        LocalDateTime validUntil = pmController.getPasswordManager().getValidUntil();
        if (validUntil == null || !validUntil.isBefore(LocalDateTime.now())) {
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
        String fileName = fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf(".gate"));

        return new Tag(fileName);
    }
}
