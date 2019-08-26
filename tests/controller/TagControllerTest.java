package controller;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import model.Entry;
import model.PasswordManager;
import model.Tag;
/**
 * @author sopr017
 */
public class TagControllerTest {

	private TagController tagController;
	private PasswordManager passwordManager;
	private PMController pmController; 
	private Entry entryWithTags;
	private Entry entryWithoutTags;
	private Tag nullTag;
	private Tag exampleTagOne;
	private Tag exampleTagTwo;
	private Tag exampleTagThree;
	
	@Before
	public void setUp(){
		tagController = new TagController();
		passwordManager = new PasswordManager(null);
		pmController = new PMController();
		entryWithTags = new Entry("Muster-Seite", "musterpasswort");
		entryWithoutTags = new Entry("example page", "samplepassword");
		nullTag = null;
		exampleTagOne = new Tag("One");
		exampleTagTwo = new Tag ("Two");
		exampleTagThree = new Tag ("Three");
		
		ArrayList<Entry> exampleList = new ArrayList<Entry>();
		exampleList.add(entryWithTags);
		exampleList.add(entryWithoutTags);
		
		tagController.setPMController(pmController);
		pmController.setPasswordManager(passwordManager);
		passwordManager.setEntries(exampleList);
		passwordManager.setRootTag(new Tag("rootTag"));
		entryWithTags.getTags().add(exampleTagOne);
		entryWithTags.getTags().add(exampleTagTwo);
		
		
	}
	/**
	 * Tests whether the addTag method throws a NullPointerException when you give null to the method or not.
	 */
	@Test 
	public void nullPointerTestAddTag() {
		try{
			tagController.addTag(exampleTagOne, null);
			fail();
		}
		catch(NullPointerException e){
			
		}
	}
	/**
	 * Tests whether the removeTag method throws a NullPointerExceptionwhen you give null to the method or not.
	 */
	@Test
	public void nullPointerTestRemoveTag() {
		try{
			passwordManager.getRootTag().getSubTags().add(exampleTagOne);
			entryWithTags.getTags().add(exampleTagTwo);
			
			tagController.removeTag(passwordManager.getRootTag(), null);
			fail();
		}
		catch(NullPointerException e){
			
		}
	}
	/**
	 * Tests whether the renameTag method throws a NullPointerException when you give null to the method or not.
	 */
	@Test 
	public void nullPointerTestRenameTag() {
		try{
			tagController.renameTag(exampleTagOne, null);
			fail();
		}
		catch(NullPointerException e){
			
		}
		try{
			tagController.renameTag(null, "test");
			fail();
		}
		catch(NullPointerException e){
			
		}
	}
	/**
	 * Tries if the added tag is in the subTagList of the parent tag and tries if it is the only one.
	 */
	@Test
	public void testAddTag() {
		tagController.addTag(exampleTagTwo, exampleTagThree);
		assertTrue(exampleTagTwo.getSubTags().contains(exampleTagThree));
		assertEquals(1, exampleTagTwo.getSubTags().size());
	}
	/**
	 * Tries if the tag is removed properly from the parent tag and from the entries.
	 */
	@Test
	public void testRemoveTag() {
		passwordManager.getRootTag().getSubTags().add(exampleTagOne);
		entryWithTags.getTags().add(exampleTagTwo);
		
		tagController.removeTag(passwordManager.getRootTag(), exampleTagOne);
		
		assertTrue(passwordManager.getRootTag().getSubTags().isEmpty());
		assertEquals(1, entryWithTags.getTags().size());
		assertEquals(exampleTagTwo, entryWithTags.getTags().get(0));
	}
	/**
	 * Tries if the tag is renamed properly.
	 */
	@Test
	public void testRenameTag() {
		tagController.renameTag(exampleTagThree, "four");
		
		assertEquals("four", exampleTagThree.getName());
	}

}
