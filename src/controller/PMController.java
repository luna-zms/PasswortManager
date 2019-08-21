package controller;

import model.PasswordManager;

public class PMController {

	private SerializationController serializationController;

	private TagController tagController;

	private PasswordManager passwordManager;

	protected PasswordController pmController;

	private LoadSaveController loadSaveController;

	private ImportExportController importExportController;

	private EntryController entryController;

	/**
	 *  
	 */
	public String setMasterPassword(String s) {
		return null;
	}

	public boolean validateMasterPassword(String pwd) {
		return false;
	}

}
