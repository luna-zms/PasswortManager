package controller;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import model.Entry;
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

    }

    @After
    public void tearDown() throws Exception {
    }
    
    /**
     * Tests createTagFromPath with a path of length one.
     * Expects no new tags to be created
     */
    @Test
    public void testPathLengthOne() {
        Tag root = new Tag("root");
        Tag one = createTagFromPath(root, new String[]{"root"});
        
        assertEquals("createTagFromPath returns wrong tag", root, one);
        assertEquals("createTagFromPath creates too many tags", root.getSubTags().size(), 0);
    }

    /**
     * Tests createTagFromPath with multiple overlapping paths.
     * Expects createTagFromPath to construct each tag mentioned in a path exactly once and to create no additional tags
     */
    @Test
    public void testValidPaths() {
        Tag root = new Tag("root");
        
        Tag one = createTagFromPath(root, new String[]{"root", "foo", "bar"});
        Tag two = createTagFromPath(root, new String[]{"root", "foo", "baz"});
        
        Tag foo = root.getSubTagByName("foo");
        Tag bar = foo.getSubTagByName("bar");
        Tag baz = foo.getSubTagByName("baz");
        
        assertNotNull(foo);
        assertNotNull(bar);
        assertNotNull(baz);
        assertEquals(one, bar);
        assertEquals(two, baz);
        assertEquals(root.getSubTags().size(), 1);
        assertEquals(foo.getSubTags().size(), 2);
        assertEquals(bar.getSubTags().size(), 0);
        assertEquals(baz.getSubTags().size(), 0);
    }
    
    /**
     * Tests createTagFromPath with a single path.
     * Expects createTagFromPath to create exactly the tags mentioned in the path.
     */
    @Test
    public void testValidPath() {
        Tag root = new Tag("root");
        Tag one = createTagFromPath(root, new String[]{"root", "bar", "baz"});

        Tag bar = root.getSubTagByName("bar");

        Tag baz = bar.getSubTagByName("baz");
        assertNotNull("createTagFromPath fails to create tag", bar);
        assertNotNull("createTagFromPath fails to create tag", baz);
        assertEquals("createTagFromPath returns wrong rag", one, baz);
        assertEquals("createTagFromPath creates too many tags", root.getSubTags().size(), 1);
        assertEquals("createTagFromPath creates too many tags", bar.getSubTags().size(), 1);
        assertEquals("createTagFromPath creates too many tags", baz.getSubTags().size(), 0);
    }
    
    /** Tests writeEntriesToStream without passing any entries.
     * Expects writeEntriesToStream to print the CSV header to the stream, but no records.
     */
    @Test
    public void testWriteEntriesToStreamNoEntries() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Tag root = new Tag("root");
        try {
            writeEntriesToStream(out, new ArrayList<Entry>(), root);
            String result = out.toString();
            System.out.println(result);
            assertTrue(result.equals("Title,Username,Password,Url,CreatedAt,LastModified,ValidUntil,Note,SecurityQuestion,SecurityQuestionAnswer,TagPaths\r\n"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Tests writeEntriesToStream by passing entries and a tag tree.
     * Expects entries to be printed into the OutputStream correctly.
     */
    @Test
    public void testWriteEntitiesToStreamFull() {
        Tag root = new Tag("root");
        Tag foo = new Tag("foo");
        Tag bar = new Tag("bar");
        Tag baz = new Tag("baz");
        
        root.getSubTags().add(foo);
        foo.getSubTags().add(bar);
        foo.getSubTags().add(baz);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Entry one = new Entry("one", "one");
        Entry two = new Entry("two", "two");
        Entry three = new Entry ("three", "three");
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(one);
        entries.add(two);
        entries.add(three);
        
        try {
            System.out.println("Blub");
            writeEntriesToStream(out, entries, root);
            String result = out.toString();
            
            System.out.println(result);
            
            assertTrue(result.equals(""));
        }
        catch (IOException e) {
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
