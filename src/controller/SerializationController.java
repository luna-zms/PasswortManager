package controller;

import model.Tag;

public abstract class SerializationController {

	protected PMController pmController;

	public abstract void load(String path);

	/**
	 *  
	 */
	public abstract void save(String path);

	protected Tag createTagFromPath(String[ ] path) {
		return null;
	}

	/**
	 *  
	 */
	protected void writeEntriesToStream(OutputStream os) {

	}

}
