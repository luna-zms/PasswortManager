package controller;

import java.util.List;
import java.util.function.Predicate;

import model.Entry;


/**
 * 
 * @author sopr015
 *
 */
public class EntryController {

    protected PMController pmController;
    
    public EntryController (PMController other){
    	pmController = other;
    }

    /**
     *Adds an Entry e to the entryList of the PasswordManeger
     *@param e
     */
    public void addEntry(Entry e) {

    }

    /**
     * Edits a Entry by removing the old one and adding the edited
     * @param old
     * @param edited
     */
    public void editEntry(Entry old, Entry edited) {
    	
    }

    /**
     *@param e
     */
    public void removeEntry(Entry e) {

    }

    /**
     * 
     * @param p
     * @return List<Entry>
     */
    public List<Entry> filter(Predicate<Entry> p) {
        return null;
    }

}
