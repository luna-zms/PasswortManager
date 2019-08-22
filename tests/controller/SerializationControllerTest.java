package controller;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.PasswordManager;
import model.Tag;

public class SerializationControllerTest extends SerializationController {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		pmController = new PMController();
		PasswordManager pma = new PasswordManager();
		pmController.setPasswordManager(pma);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testPathLengthOne() {
		Tag one = this.createTagFromPath(new String[]{"foo"});
		
		assertEquals(pmController.getPasswordManager().getRootTag(), one);
		assertEquals(pmController.getPasswordManager().getRootTag().getSubTags().size(), 0);
	}

	@Test
	public void testValidPaths() {
		Tag one = this.createTagFromPath(new String[]{"foo", "bar", "blub"});

		Tag root = pmController.getPasswordManager().getRootTag();
		assertNotNull(root);
		Tag bar = root.getSubTagByName("bar");
		assertNotNull(bar);
		Tag blub = bar.getSubTagByName("blub");
		assertNotNull(blub);
		
		Tag two = this.createTagFromPath(new String[]{"foo", "bar", "baz"});
		
		Tag baz = bar.getSubTagByName("baz");
		assertNotNull(baz);
		
		assertEquals(one, blub);
		assertEquals(two, baz);;
		
		assertEquals(root.getSubTags().size(), 1);
		assertEquals(bar.getSubTags().size(), 2);
		assertEquals(blub.getSubTags().size(), 0);
		assertEquals(baz.getSubTags().size(), 0);
	}
	
	@Test
	public void testValidPath() {
		Tag one = this.createTagFromPath(new String[]{"foo", "bar", "blub"});

		Tag root = pmController.getPasswordManager().getRootTag();
		assertNotNull(root);
		Tag bar = root.getSubTagByName("bar");
		assertNotNull(bar);
		Tag blub = bar.getSubTagByName("blub");
		assertNotNull(blub);
		
		assertEquals(one, blub);
		assertEquals(root.getSubTags().size(), 1);
		assertEquals(bar.getSubTags().size(), 1);
		assertEquals(blub.getSubTags().size(), 0);
	}
	
	@Test
	public void testNullPath() {
		Tag one = this.createTagFromPath(null);
		
		assertEquals(one, null);
	}
	
	@Test
	public void testWriteEntriesToStreamNull() {
		try {
			writeEntriesToStream(null);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWriteEntriesToStreamNoEntries() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			writeEntriesToStream(out);
			String result = out.toString();
			assertEquals(
					result,
					"Title, Username, Password, CreatedAt, LastModified, ValidUntil, Note, SecurityQuestion, SecurityQuestionAnswer, TagPaths"
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void load(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(String path) {
		// TODO Auto-generated method stub
		
	}

}
