package controller;

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.util.Base64;

import model.PasswordManager;

/**
 * The PMController class provides access to all the
 * different Controller classes needed by View classes
 * as well as the controllers itself.
 * <p>
 * It also takes care of calculating a KDF to the
 * given master password and validates the given
 * master password as long as it is set.
 *
 * @author sopr010
 */
public class PMController {

    private TagController tagController;

    private PasswordManager passwordManager;

    private PasswordController passwordController;

    private LoadSaveController loadSaveController;

    private ImportExportController importExportController;

    private EntryController entryController;

    private Path savePath;

    /**
     * Set a new master password. The master password is encrypted via a KDF and
     * then this method will update the current PasswordManager instance.
     *
     * @param password A string containing the new master password.
     * @throws IllegalArgumentException When the given string is null or empty.
     */
    public void setMasterPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Expected String as password but got null!");
        } else if (password.equals("")) {
            throw new IllegalArgumentException("The master password may not be empty!");
        }
        String safePassword = Base64.getEncoder().encodeToString(password.getBytes());
        SecretKeyFactory skFactory;

        try {
            skFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");
        } catch (NoSuchAlgorithmException noSuchAlgorithm) {
            throw new RuntimeException("Internal error! Please check your Java installation (minimum Java 8)!");
        }

        KeySpec specs = new PBEKeySpec(safePassword.toCharArray());
        SecretKey derivedKey;

        try {
            derivedKey = skFactory.generateSecret(specs);
        } catch (InvalidKeySpecException invalidKeySpec) {
            throw new RuntimeException("Internal error! Please check your Java installation (minimum Java 8)!");
        }

        passwordManager.setMasterPasswordKey(derivedKey);
    }

    /**
     * Check whether a given master password is actually correct for the
     * currently selected database.
     *
     * @param password A string containing the password to check.
     * @return true if the password is the current database's master password;
     * else false.
     * @throws IllegalArgumentException When the given string is null or empty.
     * @throws IllegalStateException    When the password manager is not properly initialized and no secret key is set.
     */
    public boolean validateMasterPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Expected String as password but got null!");
        } else if (password.equals("")) {
            throw new IllegalArgumentException("The master password may not be empty!");
        }

        if (passwordManager.getMasterPasswordKey() == null)
            throw new IllegalStateException("The master password of passwordManager is not initialized!");

        String safePassword = Base64.getEncoder().encodeToString(password.getBytes());
        SecretKeyFactory skFactory;
        SecretKey derivedKey;

        try {
            skFactory = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES");

            KeySpec specs = new PBEKeySpec(safePassword.toCharArray());

            derivedKey = skFactory.generateSecret(specs);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException noSuchAlgorithm) {
            throw new RuntimeException("Internal error! Please check your Java installation (minimum Java 8)!");
        }

        return Arrays.equals(derivedKey.getEncoded(), passwordManager.getMasterPasswordKey().getEncoded());
    }

    public TagController getTagController() {
        return tagController;
    }

    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }

    public void setPasswordManager(PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
    }

    public PasswordController getPasswordController() {
        return passwordController;
    }

    public void setPasswordController(PasswordController passwordController) {
        this.passwordController = passwordController;
    }

    public LoadSaveController getLoadSaveController() {
        return loadSaveController;
    }

    public void setLoadSaveController(LoadSaveController loadSaveController) {
        this.loadSaveController = loadSaveController;
    }

    public ImportExportController getImportExportController() {
        return importExportController;
    }

    public void setImportExportController(ImportExportController importExportController) {
        this.importExportController = importExportController;
    }

    public EntryController getEntryController() {
        return entryController;
    }

    public void setEntryController(EntryController entryController) {
        this.entryController = entryController;
    }

    public Path getSavePath() {
        return savePath;
    }

    public void setSavePath(Path savePath) {
        this.savePath = savePath;
    }
}
