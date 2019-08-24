package controller;

import model.Entry;
import model.Tag;
import org.apache.commons.csv.CSVParser;
import org.junit.*;
import util.Tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SerializationControllerTest extends SerializationController {

    private static final String zeroEntries = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\n";
    private static final String oneEntry = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,root;root\\foo\n";
    private static final String twoEntries = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,root;root\\foo\ntwo,,two,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,root\n";

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

    /**
     * Tests writeEntriesToStream without passing any entries.
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
            assertTrue(result.equals(zeroEntries));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Tests writeEntriesToStream by passing a single entry.
     * Expects entries to be printed into the OutputStream correctly.
     */
    @Test
    public void testWriteEntitiesToStreamSingle() {
        Tag root = new Tag("root");
        Tag foo = new Tag("foo");

        root.getSubTags().add(foo);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Entry one = new Entry("one", "one");
        one.getTags().add(root);
        one.getTags().add(foo);
        // Set createdAt and lastModified to a constant
        one.setCreatedAt(LocalDateTime.MIN);
        one.setLastModified(LocalDateTime.MIN);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(one);

        try {
            writeEntriesToStream(out, entries, root);
            String result = out.toString();
            assertTrue(result.equals(oneEntry));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests writeEntriesToStream by passing multiple entries
     * Expects entries to be printed into the OutputStream correctly.
     */
    @Test
    public void testWriteEntitiesToStreamMultiple() {
        Tag root = new Tag("root");
        Tag foo = new Tag("foo");

        root.getSubTags().add(foo);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Entry one = new Entry("one", "one");
        one.getTags().add(root);
        one.getTags().add(foo);
        // Set createdAt and lastModified to a constant
        one.setCreatedAt(LocalDateTime.MIN);
        one.setLastModified(LocalDateTime.MIN);

        Entry two = new Entry("two", "two");
        two.getTags().add(root);
        two.setCreatedAt(LocalDateTime.MIN);
        two.setLastModified(LocalDateTime.MIN);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(one);
        entries.add(two);

        try {
            writeEntriesToStream(out, entries, root);
            String result = out.toString();
            assertTrue(result.equals(twoEntries));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests parseEntries if no records are passed
     * Expects a empty list of entries and a null (empty) tag tree
     */
    @Test
    public void testParseEntriesZeroEntries() {
        ByteArrayInputStream in = new ByteArrayInputStream(zeroEntries.getBytes());

        try {
            CSVParser parser = new CSVParser(new InputStreamReader(in), entryParseFormat);
            Tuple<List<Entry>, Tag> result = parseEntries(parser.getRecords());

            assertTrue(result.first().size() == 0);
            assertNull(result.second());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests parseEntries if a single record is passed
     * Expects a single Entry and two tags to exist
     */
    @Test
    public void testParseEntriesOneEntry() {
        ByteArrayInputStream in = new ByteArrayInputStream(oneEntry.getBytes());

        try {
            CSVParser parser = new CSVParser(new InputStreamReader(in), entryParseFormat);
            Tuple<List<Entry>, Tag> result = parseEntries(parser.getRecords());

            assertTrue(result.first().size() == 1);

            Entry parsedEntry = result.first().get(0);
            assertEquals("one", parsedEntry.getTitle());
            assertEquals("one", parsedEntry.getPassword());
            assertEquals(parsedEntry.getCreatedAt(), LocalDateTime.MIN);
            assertEquals(parsedEntry.getCreatedAt(), LocalDateTime.MIN);

            Tag root = result.second();
            assertNotNull(root);
            assertEquals(1, root.getSubTags().size());
            Tag foo = root.getSubTags().get(0);
            assertTrue(parsedEntry.getTags().contains(root));
            assertTrue(parsedEntry.getTags().contains(foo));
            assertEquals(2, parsedEntry.getTags().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests parseEntries if multiple records are passed
     * Expects two entries and two tags to exist
     */
    @Test
    public void testParseEntriesTwoEntries() {
        ByteArrayInputStream in = new ByteArrayInputStream(twoEntries.getBytes());

        try {
            CSVParser parser = new CSVParser(new InputStreamReader(in), entryParseFormat);
            Tuple<List<Entry>, Tag> result = parseEntries(parser.getRecords());

            assertTrue(result.first().size() == 2);

            Entry parsedEntryOne = result.first().get(0);
            assertEquals("one", parsedEntryOne.getTitle());
            assertEquals("one", parsedEntryOne.getPassword());
            assertEquals(parsedEntryOne.getCreatedAt(), LocalDateTime.MIN);
            assertEquals(parsedEntryOne.getCreatedAt(), LocalDateTime.MIN);

            Entry parsedEntryTwo = result.first().get(1);
            assertEquals("two", parsedEntryTwo.getTitle());
            assertEquals("two", parsedEntryTwo.getPassword());
            assertEquals(parsedEntryTwo.getCreatedAt(), LocalDateTime.MIN);
            assertEquals(parsedEntryTwo.getCreatedAt(), LocalDateTime.MIN);

            Tag root = result.second();
            assertNotNull(root);
            assertEquals(1, root.getSubTags().size());
            Tag foo = root.getSubTags().get(0);
            assertTrue(parsedEntryOne.getTags().contains(root));
            assertTrue(parsedEntryOne.getTags().contains(foo));
            assertEquals(2, parsedEntryOne.getTags().size());
            assertTrue(parsedEntryTwo.getTags().contains(root));
            assertEquals(1, parsedEntryTwo.getTags().size());
        } catch (IOException e) {
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
