package controller;

import model.PasswordManager;

public class PMController {

    private TagController tagController;

    private PasswordManager passwordManager;

    protected PasswordController passwordController;

    private LoadSaveController loadSaveController;

    private ImportExportController importExportController;

    private EntryController entryController;

    /**
     *
     */
    public void setMasterPassword(String s) {

    }

    public boolean validateMasterPassword(String pwd) {
        return false;
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }
}
