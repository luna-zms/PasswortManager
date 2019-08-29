package controller;

import model.Entry;
import model.PasswordManager;
import model.SecurityQuestion;
import model.Tag;
import org.apache.commons.csv.CSVParser;
import org.junit.Test;
import util.CsvException;
import util.Tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class SerializationControllerTest extends SerializationController {

    private static final String zeroEntries = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\n";
    private static final String oneEntry = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,foo\n";
    private static final String twoEntries = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,foo\ntwo,,two,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,\n";
    private static final String fullyInitialized = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\ntitle,username,password,https://localhost,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,-999999999-01-01,note,question,answer,\n";
    private static final String emptyPath = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,";
    private static final String twoEmptyPaths = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,;";
    private static final String inconsistentRecord = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,foo\n";
    private static final String invalidCreatedAt = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,blub,-999999999-01-01T00:00:00,,,,,foo\n";
    private static final String invalidLastModified = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,blub,,,,,foo\n";
    private static final String invalidValidUntil = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,blub,,,,foo\n";
    private static final String invalidURL = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,blub,-999999999-01-01T00:00:00,-999999999-01-01T00:00:00,,,,,foo\n";
    private static final String emptyCreatedAt = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,,-999999999-01-01T00:00:00,,,,,foo\n";
    private static final String emptyLastModified = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\none,,one,,-999999999-01-01T00:00:00,,,,,,foo\n";
    
    // Fuzzer data
    private static final int FUZZER_RUNS = 100;
    private static final int MAX_STRING_LENGTH = 100;
    private static final int MAX_RECORDS = 100;
    private static final int MAX_DATE_LOOKAHEAD = 100;
    private static final char[] SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefhijklmnopqrstuvwxyz1234567890".toCharArray();
    private static final String HEADER = "title,username,password,url,createdAt,lastModified,validUntil,note,securityQuestion,securityQuestionAnswer,tagPaths\n";
    private static final String[] PATH_COMPONENTS = new String[]{"foo", "bar", "baz", "blub", "test", "test2"};
    private static final int MAX_PATH_LENGTH = 25;
    private static final int MAX_PATH_LIST_LENGTH = 100;

    public SerializationControllerTest() {
        super(null);
    }
    
    private String randomString(int length) {
    	StringBuilder builder = new StringBuilder();
    	for (int i = 0; i < length; i++) {
    		builder.append(SYMBOLS[ThreadLocalRandom.current().nextInt(SYMBOLS.length)]);
    	}
    	return builder.toString();
    }
    
    private LocalDate randomLocalDate() {
    	return LocalDate.now()
    			.plusDays(ThreadLocalRandom.current().nextInt(MAX_DATE_LOOKAHEAD))
    			.plusMonths(ThreadLocalRandom.current().nextInt(MAX_DATE_LOOKAHEAD))
    			.plusYears(ThreadLocalRandom.current().nextInt(MAX_DATE_LOOKAHEAD));
    }
    
    private LocalDateTime randomLocalDateTime() {
    	return LocalDateTime.now()
    			.plusDays(ThreadLocalRandom.current().nextInt(MAX_DATE_LOOKAHEAD))
    			.plusMonths(ThreadLocalRandom.current().nextInt(MAX_DATE_LOOKAHEAD))
    			.plusYears(ThreadLocalRandom.current().nextInt(MAX_DATE_LOOKAHEAD));
    	
    }
    
    private String randomPath(int length) {
    	StringBuilder result = new StringBuilder();
    	for (int i = 0; i < length; i++) {
    		result.append(PATH_COMPONENTS[ThreadLocalRandom.current().nextInt(PATH_COMPONENTS.length)]);
    		result.append("\\");
    	}
    	return result.toString();
    }
    
    private String randomPathList(int amount) {
    	StringBuilder result = new StringBuilder();
    	for (int i = 0; i < amount; i++) {
    		result.append(randomPath(ThreadLocalRandom.current().nextInt(MAX_PATH_LENGTH)));
    		result.append(";");
    	}
    	return result.toString();
    }
    
    private String generateRandomCSV() {
    	StringBuilder result = new StringBuilder();
    	ThreadLocalRandom random = ThreadLocalRandom.current();
    	int numRecords = random.nextInt(MAX_RECORDS);
    	result.append(HEADER);
    	
    	for (int i = 0; i < numRecords; i++) {
    		// title
    		result.append(randomString(MAX_STRING_LENGTH));
    		result.append(",");
    		// username
    		result.append(randomString(MAX_STRING_LENGTH ));
    		result.append(",");
    		// password
    		result.append(randomString(MAX_STRING_LENGTH));
    		result.append(",");
    		// url
    		result.append("https://localhost");
    		result.append(",");
    		// createdAt
    		result.append(randomLocalDateTime().format(DATE_TIME_FORMAT));
    		result.append(",");
    		// lastModified
    		result.append(randomLocalDateTime().format(DATE_TIME_FORMAT));
    		result.append(",");
    		// validUntil
    		result.append(randomLocalDate().format(DATE_FORMAT));
    		result.append(",");
    		// note
    		result.append(randomString(MAX_STRING_LENGTH));
    		result.append(",");
    		// sec Quest.
    		result.append(randomString(MAX_STRING_LENGTH));
    		result.append(",");
    		// sec answer
    		result.append(randomString(MAX_STRING_LENGTH));
    		result.append(",");
    		// taglist
    		result.append(randomPathList(random.nextInt(MAX_PATH_LIST_LENGTH)));
    		result.append("\n");
    	}
    	return result.toString();
    }
    
    

    private Tuple<List<Entry>, Tag> parseCSVString(String str) {
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());

        try {
            CSVParser parser = new CSVParser(new InputStreamReader(in), entryParseFormat);
            return parseEntries(parser.getRecords());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Generates random CSV files and parses them. Parsing should either fail with a CSV error, or succeed.
     */
    @Test
    public void parseEntriesFuzzer() {
    	for (int i = 0; i < FUZZER_RUNS; i ++) {
    		System.out.print("Fuzzing Run: " + (i + 1));
    		String csv = generateRandomCSV();
    		try {
    			parseCSVString(csv);
    			System.out.println("    Successful");
    		}
    		catch (CsvException exp) {
    			System.out.println("    " + exp.getMessage());
    		}
    		catch (Exception exp) {
    			System.out.println("Fuzzing failed. Got Exception:");
    			System.out.println(exp.getMessage());
    			System.out.println("CSV file is:");
    			System.out.println(csv);
    			fail();
    		}
    	}
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
            writeEntriesToStream(out, new ArrayList<>(), root);
            String result = out.toString();
            assertEquals("writeEntriesToStream creates phantom entries", result, zeroEntries);
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
    public void testWriteEntriesToStreamSingle() {
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
            assertEquals("writeEntriesToStream produces wrong output", result, oneEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests writeEntriesToStream by passing multiple entries.
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
            assertEquals("writeEntriesToStream produces wrong output", result, twoEntries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests writeEntriesToStream with a fully initialized Entry.
     */
    @Test
    public void testWriteEntriesToStreamFullyInitialized() {
        Tag root = new Tag("root");

        Entry entry = new Entry("title", "password");
        entry.setUsername("username");
        entry.setNote("note");
        try {
            entry.setUrl(new URL("https://localhost"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        entry.setCreatedAt(LocalDateTime.MIN);
        entry.setLastModified(LocalDateTime.MIN);
        entry.setValidUntil(LocalDate.MIN);
        SecurityQuestion question = new SecurityQuestion("question", "answer");
        entry.setSecurityQuestion(question);
        entry.getTags().add(root);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(entry);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            writeEntriesToStream(out, entries, root);
            String result = out.toString();
            assertEquals("writeEntriesToStream produces wrong output", result, fullyInitialized);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests writeEntriesToStream with an entry initialized to null in every field.
     * Parsing should succeed.
     */
    @Test
    public void testWriteEntriesToStreamNullEntry() {
        Entry entry = new Entry(null, null);
        entry.setUsername(null);
        entry.setNote(null);
        entry.setUrl(null);
        entry.setCreatedAt(null);
        entry.setLastModified(null);
        entry.setValidUntil(null);
        entry.setSecurityQuestion(null);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(entry);

        Tag root = new Tag("root");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            writeEntriesToStream(out, entries, root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Tests parseEntries if a single record is passed.
     * Expects a single Entry and two tags to exist.
     */
    @Test
    public void testParseEntriesOneEntry() {
        Tuple<List<Entry>, Tag> result = parseCSVString(oneEntry);
        assertEquals(1, result.first().size());

        Entry parsedEntry = result.first().get(0);
        assertEquals("parseEntries reads wrong title", "one", parsedEntry.getTitle());
        assertEquals("parseEntries reads wrong password", "one", parsedEntry.getPassword());
        assertEquals("parseEntries parses dates incorrectly", parsedEntry.getCreatedAt(), LocalDateTime.MIN);
        assertEquals("parseEntries parses dates incorrectly", parsedEntry.getCreatedAt(), LocalDateTime.MIN);

        Tag root = result.second();
        assertNotNull("parseEntries fails to create tag tree", root);
        assertEquals("parseEntries produces phantom tags", 1, root.getSubTags().size());
        Tag foo = root.getSubTags().get(0);
        assertTrue("parseEntries constructs tag tree incorrectly", parsedEntry.getTags().contains(root));
        assertTrue("parseEntries assigns tags to entries incorrectly", parsedEntry.getTags().contains(foo));
        assertEquals("parseEntries assigns incorrect amount of tags to entry", 2, parsedEntry.getTags().size());
    }

    /**
     * Tests parseEntries if multiple records are passed.
     * Expects two entries and two tags to exist.
     */
    @Test
    public void testParseEntriesTwoEntries() {
        Tuple<List<Entry>, Tag> result = parseCSVString(twoEntries);
        assertEquals(2, result.first().size());

        Entry parsedEntryOne = result.first().get(0);
        assertEquals("parseEntries reads wrong title", "one", parsedEntryOne.getTitle());
        assertEquals("parseEntries reads wrong password", "one", parsedEntryOne.getPassword());
        assertEquals("parseEntries parses dates incorrectly", parsedEntryOne.getCreatedAt(), LocalDateTime.MIN);
        assertEquals("parseEntries parses dates incorrectly", parsedEntryOne.getCreatedAt(), LocalDateTime.MIN);

        Entry parsedEntryTwo = result.first().get(1);
        assertEquals("parseEntries reads wrong title", "two", parsedEntryTwo.getTitle());
        assertEquals("parseEntries reads wrong password", "two", parsedEntryTwo.getPassword());
        assertEquals("parseEntries parses dates incorrectly", parsedEntryTwo.getCreatedAt(), LocalDateTime.MIN);
        assertEquals("parseEntries parses dates incorrectly", parsedEntryTwo.getCreatedAt(), LocalDateTime.MIN);

        Tag root = result.second();
        assertNotNull("parseEntries fails to construct tag tree", root);
        assertEquals("parseEntries produces phantom tags", 1, root.getSubTags().size());
        Tag foo = root.getSubTags().get(0);
        assertTrue("parseEntries assigns tags to entries incorrectly", parsedEntryOne.getTags().contains(root));
        assertTrue("parseEntries assigns tags to entries incorrectly", parsedEntryOne.getTags().contains(foo));
        assertEquals("parseEntries assigns incorrect amount of tags to entry", 2, parsedEntryOne.getTags().size());
        assertTrue("parseEntries assigns tags to entries incorrectly", parsedEntryTwo.getTags().contains(root));
        assertEquals("parseEntries assigns incorrect amount of tags to entry", 1, parsedEntryTwo.getTags().size());
    }

    /**
     * Tests parseEntries with a fully initialized entry.
     */
    @Test
    public void testParseEntriesFullyInitialized() {
        Tuple<List<Entry>, Tag> result = parseCSVString(fullyInitialized);
        assertEquals(1, result.first().size());

        Entry entry = result.first().get(0);
        assertEquals("title", entry.getTitle());
        assertEquals("username", entry.getUsername());
        assertEquals("password", entry.getPassword());
        assertEquals("note", entry.getNote());
        try {
            assertEquals(new URL("https://localhost"), entry.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertEquals(LocalDateTime.MIN, entry.getCreatedAt());
        assertEquals(LocalDateTime.MIN, entry.getLastModified());
        assertEquals(LocalDate.MIN, entry.getValidUntil());
        assertEquals("question", entry.getSecurityQuestion().getQuestion());
        assertEquals("answer", entry.getSecurityQuestion().getAnswer());
        assertEquals(1, entry.getTags().size());
    }

    /**
     * Tries parsing a cvs recording containing no tag paths.
     * Parsing should fail with a CvsException.
     */
    @Test
    public void testParseEntriesWithEmptyTagPaths() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(emptyPath);
        } catch (CsvException exc) {
            fail("parseEntries does not accept cvs file with empty tag path in record");
        }
    }

    /**
     * Tries parsing a cvs record containing a tag path with two empty paths.
     * Parsing should fail with a CvsException.
     */
    @Test
    public void testParseEntriesWithTwoEmptyPaths() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(twoEmptyPaths);
        } catch (CsvException exc) {
            fail("parseEntries does not accept cvs file with two empty paths in tagPaths");
        }
    }

    /**
     * Tries parsing a cvs record with less fields than specified in the header.
     * Parsing should fail with a CvsException.
     */
    @Test
    public void testParseEntriesWithInconsistentRecord() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(inconsistentRecord);
            fail("parseEntries accepts cvs file with inconsistent number of fields");
        } catch (CsvException exc) {

        }
    }

    /**
     * Tries parsing a cvs record with an invalid createdAt field.
     * Parsing should fail with a CvsException.
     */
    @Test
    public void testParseEntriesWithInvalidCreatedAt() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(invalidCreatedAt);
            fail("parseEntries accepts cvs file with invalid createdAt field");
        } catch (CsvException exc) {

        }
    }

    /**
     * Tries parsing a cvs record with an invalid lastModified field.
     * Parsing should fail with a CvsException.
     */
    @Test
    public void testParseEntriesWithInvalidLastModified() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(invalidLastModified);
            fail("parseEntries accepts cvs file with invalid lastModified field");
        } catch (CsvException exc) {

        }
    }

    /**
     * Tries parsing a cvs record with an invalid validUntil field.
     * Parsing should fail with a CvsException.
     */
    @Test
    public void testParseEntriesWithInvalidValidUntil() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(invalidValidUntil);
            fail("parseEntries accepts cvs file with invalid validUntil field");
        } catch (CsvException exc) {

        }
    }

    /**
     * Tries parsing a cvs record with an invalid url field.
     * Parsing should fail with a CvsException.
     */
    @Test
    public void testParseEntriesWithInvalidUrl() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(invalidURL);
            fail("parseEntries accepts cvs file with invalid url field");
        } catch (CsvException exc) {

        }
    }

    /**
     * Tries parsing a cvs record with an empty createdAt field.
     * Parsing should succeed, with a default value in createdAt.
     */
    @Test
    public void testParseEntriesWithEmptyCreatedAt() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(emptyCreatedAt);
            assertNotNull(result.first().get(0).getCreatedAt());
        } catch (CsvException exc) {

        }
    }

    /**
     * Tries parsing a cvs record with an empty lastModified field.
     * Parsing should succeed, with a default value in lastModified.
     */
    @Test
    public void testParseEntriesWithEmptyLastModified() {
        try {
            Tuple<List<Entry>, Tag> result = parseCSVString(emptyLastModified);
            assertNotNull(result.first().get(0).getCreatedAt());
        } catch (CsvException exc) {

        }
    }

    @Override
    public void load(Path path) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(Path path) {
        // TODO Auto-generated method stub

    }
}
