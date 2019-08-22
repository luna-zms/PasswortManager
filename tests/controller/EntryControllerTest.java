package controller;

import static org.junit.Assert.*;
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
	public void removeEntry() {
		entryController.addEntry(one);
		entryController.addEntry(two);
		entryController.removeEntry(one);
		assertEquals(1, pm.getEntries().size());
		assertTrue(pm.getEntries().contains(two));
		entryController.removeEntry(two);
		assertEquals(0, pm.getEntries().size());
	}
	
	@Test
	public void editEntry() {
		fail("Not yet implemented");
	}
	
	@Test
	public void filter() {
		fail("Not yet implemented");
	}
	
}
