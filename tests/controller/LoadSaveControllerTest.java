package controller;

import model.Entry;
import model.PasswordManager;
import model.Tag;
import util.ListUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LoadSaveControllerTest {
    private LoadSaveController toTest;
    private PasswordManager passwordManager;

    private Path tempDir;

    @Before
    public void setUp() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey key = keyGen.generateKey();

        passwordManager = new PasswordManager(key);

        passwordManager.setLastModified(LocalDateTime.now());
        passwordManager.setValidUntil(LocalDateTime.now());

        PMController pmController = new PMController();
        pmController.setPasswordManager(passwordManager);
        toTest = new LoadSaveController(pmController);

        tempDir = Files.createTempDirectory("PMLoadSaveControllerTest");
    }

    @After
    public void tearDown() throws Exception {
        Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Tests saving and then loading a completely empty instance of the password manager
     */
    @Test
    public void roundTripEmptyTest() throws IOException {
        passwordManager.setRootTag(new Tag("roundTripEmptyTest"));

        roundTrip("roundTripEmptyTest.gate");

        assertTrue(passwordManager.getEntries().isEmpty());
        assertTrue(ListUtil.tagEquals(new Tag("roundTripEmptyTest"), passwordManager.getRootTag()));
    }

    /**
     * Tests saving and then loading an instance of the password manager with only the root tag set and only one entry
     */
    @Test
    public void roundTripOnlyRootTagAndOneEntry() throws IOException {
        Tag testTag = new Tag("roundTripOnlyRootTagAndOneEntry");
        Entry entry = new Entry("Test", "Test");

        entry.getTags().add(testTag);

        passwordManager.setRootTag(testTag);
        passwordManager.getEntries().add(entry);

        roundTrip("roundTripOnlyRootTagAndOneEntry.gate");

        assertEquals(passwordManager.getEntries().size(), 1);
        assertTrue("Left: " + entry + "\nRight: " + passwordManager.getEntries().get(0), entryEquals(entry, passwordManager.getEntries().get(0)));
        assertTrue(ListUtil.tagEquals(new Tag("roundTripOnlyRootTagAndOneEntry"), passwordManager.getRootTag()));
    }

    /**
     * Tests saving and then loading an instance of the password manager with multiple tags and one entry (which has a tag)
     */
    @Test
    public void roundTripManyTagsAndOneEntry() throws IOException {
        Tag testTag = new Tag("roundTripOnlyManyTagsAndOneEntry");
        testTag.getSubTags().add(new Tag("Subtag1"));
        testTag.getSubTags().add(new Tag("Subtag2"));
        Entry entry = new Entry("Test", "Test");

        entry.getTags().add(testTag);

        passwordManager.setRootTag(testTag);
        passwordManager.getEntries().add(entry);

        roundTrip("roundTripOnlyManyTagsAndOneEntry.gate");

        assertEquals(passwordManager.getEntries().size(), 1);
        assertTrue("Left: " + entry + "\nRight: " + passwordManager.getEntries().get(0), entryEquals(entry, passwordManager.getEntries().get(0)));
        assertTrue(ListUtil.tagEquals(testTag, passwordManager.getRootTag()));
    }

    /**
     * Tests loading some data into the password manager while it already contains some of the data
     */
    @Test
    public void testLoadWithExisting() throws IOException {
        Tag existing = new Tag("testLoadWithExisting");

        Tag testTag = new Tag("testLoadWithExisting");
        testTag.getSubTags().add(new Tag("Subtag1"));
        testTag.getSubTags().add(new Tag("Subtag2"));
        Entry entry = new Entry("Test", "Test");

        entry.getTags().add(testTag);

        passwordManager.setRootTag(testTag);
        passwordManager.getEntries().add(entry);

        Path testFile = Paths.get(tempDir.toString(), "testLoadWithExisting.gate");

        toTest.save(testFile);

        passwordManager.setRootTag(existing);
        passwordManager.getEntries().clear();

        toTest.load(testFile);

        assertEquals(passwordManager.getEntries().size(), 1);
        assertTrue("Left: " + entry + "\nRight: " + passwordManager.getEntries().get(0), entryEquals(entry, passwordManager.getEntries().get(0)));
        assertTrue(ListUtil.tagEquals(testTag, passwordManager.getRootTag()));
    }

    /**
     * Tests loading some data into the password manager while it already contains some other data, which should be overridden
     */
    @Test
    public void testLoadOverride() throws IOException {
        Tag existing = new Tag("testLoadOverride");
        existing.getSubTags().add(new Tag("Subtag3"));

        Tag testTag = new Tag("testLoadOverride");
        testTag.getSubTags().add(new Tag("Subtag1"));
        testTag.getSubTags().add(new Tag("Subtag2"));
        Entry entry = new Entry("Test", "Test");

        entry.getTags().add(testTag);

        passwordManager.setRootTag(testTag);
        passwordManager.getEntries().add(entry);

        Path testFile = Paths.get(tempDir.toString(), "testLoadOverride.gate");

        toTest.save(testFile);

        passwordManager.setRootTag(existing);
        passwordManager.getEntries().clear();

        toTest.load(testFile);

        assertEquals(1, passwordManager.getEntries().size());
        assertTrue("Left: " + entry + "\nRight: " + passwordManager.getEntries().get(0), entryEquals(entry, passwordManager.getEntries().get(0)));
        assertTrue(ListUtil.tagEquals(testTag, passwordManager.getRootTag()));
    }

    private void roundTrip(String test) throws IOException {
        Path testFile = Paths.get(tempDir.toString(), test);
        toTest.save(testFile);
        toTest.load(testFile);
    }

    public boolean entryEquals(Entry e1, Entry e2) {
        if (e1 == e2) return true;
        return e1.getTitle().equals(e2.getTitle()) &&
               e1.getUsername().equals(e2.getUsername() ) &&
               e1.getPassword().equals(e2.getPassword()) &&
               e1.getNote().equals(e2.getNote()) &&
               Objects.equals(e1.getUrl(), e2.getUrl()) &&
               e1.getCreatedAt().equals(e2.getCreatedAt()) &&
               e1.getLastModified().equals(e2.getLastModified()) &&
               Objects.equals(e1.getValidUntil(), e2.getValidUntil()) &&
               Objects.equals(e1.getSecurityQuestion(), e2.getSecurityQuestion()) &&
               ListUtil.tagsEquals(e1.getTags(), e2.getTags());
    }
}
