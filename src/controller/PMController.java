package controller;

import model.PasswordManager;

public class PMController {

    private TagController tagController;

    private PasswordManager passwordManager;

    private PasswordController passwordController;

    private LoadSaveController loadSaveController;

    private ImportExportController importExportController;

    private EntryController entryController;

    /**
     * Set a new master password. The master password is encrypted via a KDF and
     * then this method will update the current PasswordManager instance.
     * 
     * @param password
     *            A string containing the new master password.
     */
    public void setMasterPassword(String password) {

    }

    /**
     * Check whether a given master password is actually correct for the
     * currently selected database.
     * 
     * @param password
     *            A string containing the password to check.
     * @return true if the password is the current database's master password;
     *         else false.
     */
    public boolean validateMasterPassword(String password) {
        return false;
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

}
