package controller;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import model.Entry;
import model.PasswordManager;

/**
 * This class is for managing the entries from PasswordManager
 * 
 * @author sopr015
 *
 */
public class EntryController {

	protected PMController pmController;

	public EntryController(PMController other) {
		pmController = other;
	}

	/**
	 * Adds an entry to the entries of the PasswordManeger
	 *
	 * @param entry
	 *            Entry e will be added
	 */
	public void addEntry(Entry entry) {
		
		if(entry == null) throw new IllegalArgumentException();
		PasswordManager pm = pmController.getPasswordManager();
		pm.getEntries().add(entry);

	}

	/**
	 * Edits a Entry by removing the old one and adding the edited
	 * 
	 * @param old
	 * @param edited
	 */
	public void editEntry(Entry old, Entry edited) {
		this.removeEntry(old);
		this.addEntry(edited);
	}

	/**
	 * Removes entry from entries of the PasswordManager
	 *
	 * @param entry
	 */
	public void removeEntry(Entry entry) {
		
		if(entry == null) throw new IllegalArgumentException();
		PasswordManager pm = pmController.getPasswordManager();
		pm.getEntries().remove(entry);

	}

	/**
	 * Filters entries from PasswordManager via Predicate
	 * 
	 * @param predicate
	 * @return List<Entry> filtered version of entries from PasswordManager
	 */
	public List<Entry> filter(Predicate<Entry> predicate) {
		return pmController.getPasswordManager().getEntries().stream().filter(predicate).collect(Collectors.toList());

	}

}
