package controller;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import model.Entry;
import model.PasswordManager;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests for EntryController
 * @author sopr015
 *
 */
public class EntryControllerTest {

	private Entry nullEntry;
	private Entry one;
	private Entry two;
	private PasswordManager pm;
	private PMController pmController;
	private List<Entry> entryListBefore;
	private List<Entry> entryListAfter;
	private EntryController entryController;
	
	/**
	 * 
	 * Resets attributes
	 * 
	 */
	@Before
	public void setUp(){
		pm = new PasswordManager(null);
		nullEntry = null;
		one = new Entry("one", "123Kevin");
		two = new Entry("two", "123Kevin");
		entryListBefore = pm.getEntries();
		pmController = new PMController();
		pmController.setPasswordManager(pm);
		entryController = new EntryController(pmController);
	}
	
	/**
	 * 
	 * tests addEntry() with null as parameter
	 * 
	 */
	public void testAddIllegalArgumentException() {
		try{
			entryController.addEntry(null);
			fail("testAddNullPointerException failed");
		}catch(Exception e){
			
		}
		
	}
	/**
	 * 
	 * tests removeEntry() with null as parameter
	 * 
	 */
	public void testRemoveIllegalArgumentException() {
		try{
			entryController.removeEntry(null);
			fail("testRemoveNullPointerException failed");
		}catch(Exception e){
			
		}
		
	}
	/**
	 * 
	 * tests editEntry() with null as parameter
	 * 
	 */
	public void testEditIllegalArgumentException() {
		try{
			entryController.editEntry(null, null);
			fail("IllegalArgumentException() failed");
		}catch(Exception e){
			
		}
		
	}
	

	
	/**
	 * Tests addEntry() 
	 * adds two entries and checks if they are adds to the PasswordManager
	 */
	@Test
	public void addEntryTest() {
		entryController.addEntry(one);
		assertTrue(pm.getEntries().contains(one) );
		assertFalse(pm.getEntries().contains(two) );
	}
	
	/**
	 * Tests if a entry can be removed from PasswordManager
	 * removes two different Entries
	 */
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
	
	/**
	 * Tests if a Entry one is changed to entry three
	 * in a PasswordManager with two Entries
	 */
	@Test
	public void editEntryTest() {
		entryController.addEntry(one);
		entryController.addEntry(two);
		entryController.editEntry(one, new Entry("three", "Kevin"));
		assertFalse(pm.getEntries().contains(one));
		assertTrue(pm.getEntries().stream().anyMatch(entry -> {
			return (entry.getTitle().equals("three"));
		}));
	}
	
	/**
	 * Tests if two different filters 
	 * one filters by Password and the return should be the same list
	 * the other filters by name and the return should be a list with a single specified Element
	 */
	@Test
	public void filterTest() {
		entryController.addEntry(one);
		entryController.addEntry(two);
		entryListAfter = entryController.filter( entry -> {
			return (entry.getPassword().equals("123Kevin"));
		});
		assertTrue(entryListAfter.equals(pm.getEntries()));
		entryListAfter = entryController.filter( entry -> {
			return (entry.getTitle().equals("one"));
		});
		ArrayList<Entry> test = new ArrayList<Entry>();
		test.add(one);
		assertTrue(test.equals(entryListAfter));
		
	}
	
}
