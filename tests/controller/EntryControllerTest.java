package controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import model.Entry;
import model.PasswordManager;
import org.junit.Before;
import org.junit.Test;



public class EntryControllerTest {

	private Entry nullEntry;
	private Entry one;
	private Entry two;
	private PasswordManager pm;
	private PMController pmController;
	private List<Entry> entryListBefore;
	private List<Entry> entryListAfter;
	private EntryController entryController;
	
	@Before
	public void setUp(){
		pm = new PasswordManager();
		nullEntry = null;
		one = new Entry("one", "123Kevin");
		two = new Entry("two", "123Kevin");
		entryListBefore = pm.getEntries();
		pmController.setPasswordManager(pm);
		entryController = new EntryController(pmController);
		System.gc();
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullPointerException() {
	    entryController.addEntry(null);
	    entryController.removeEntry(null);
	    entryController.editEntry(null, null);
	    entryController.removeEntry(one);
	}
	
	@Test
	public void addEntryTest() {
		entryController.addEntry(one);
		assertTrue(pm.getEntries().contains(one) );
		assertFalse(pm.getEntries().contains(two) );
	}
	
	@Test
	public void removeEntryTest() {
		entryController.addEntry(one);
		entryController.addEntry(two);
		entryController.removeEntry(one);
		assertEquals(1, pm.getEntries().size());
		assertTrue(pm.getEntries().contains(two));
		entryController.removeEntry(two);
		assertEquals(0, pm.getEntries().size());
	}
	
	@Test
	public void editEntryTest() {
		entryController.addEntry(one);
		entryController.addEntry(two);
		entryController.editEntry(one, new Entry("three", "Kevin"));
		assertFalse(pm.getEntries().contains(one));
		assertTrue(pm.getEntries().stream().anyMatch(entry -> {
			return (entry.getTitle() == "three");
		}));
	}
	
	@Test
	public void filterTest() {
		entryController.addEntry(one);
		entryController.addEntry(two);
		entryListAfter = entryController.filter( entry -> {
			return (entry.getPassword() == "123Kevin");
		});
		assertTrue(entryListAfter.equals(pm.getEntries()));
		entryListAfter = entryController.filter( entry -> {
			return (entry.getTitle() == "one");
		});
		ArrayList<Entry> test = new ArrayList<Entry>();
		test.add(one);
		assertTrue(test.equals(entryListAfter));
		
	}
	
}
