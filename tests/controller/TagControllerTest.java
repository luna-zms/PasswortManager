package controller;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import model.Entry;
import model.PasswordManager;
import model.Tag;

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
	@Test 
	public void nullPointerTestAddTag() {
		try{
			tagController.addTag(exampleTagOne, null);
			fail();
		}
		catch(NullPointerException e){
			
		}
	}
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
	@Test
	public void testAddTag() {
		tagController.addTag(exampleTagTwo, exampleTagThree);
		assertTrue(exampleTagTwo.getSubTags().contains(exampleTagThree));
		assertEquals(1, exampleTagTwo.getSubTags().size());
	}

	@Test
	public void testRemoveTag() {
		passwordManager.getRootTag().getSubTags().add(exampleTagOne);
		entryWithTags.getTags().add(exampleTagTwo);
		
		tagController.removeTag(passwordManager.getRootTag(), exampleTagOne);
		
		assertTrue(passwordManager.getRootTag().getSubTags().isEmpty());
		assertEquals(1, entryWithTags.getTags().size());
		assertEquals(exampleTagTwo, entryWithTags.getTags().get(0));
	}

	@Test
	public void testRenameTag() {
		tagController.renameTag(exampleTagThree, "four");
		
		assertEquals("four", exampleTagThree.getName());
	}

}
