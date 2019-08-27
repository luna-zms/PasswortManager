package controller;

import model.Entry;
import model.PasswordManager;
import model.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

import static junit.framework.TestCase.*;

public class LoadSaveControllerTest {
    private LoadSaveController toTest;
    private PasswordManager passwordManager;

    private Path tempDir;

    @Before
    public void setUp() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey key = keyGen.generateKey();

        passwordManager = new PasswordManager(key);
        PMController pmController = new PMController();

        passwordManager.setLastModified(LocalDateTime.now());
        passwordManager.setValidUntil(LocalDateTime.now());

        pmController.setPasswordManager(passwordManager);

        toTest = new LoadSaveController();
        toTest.pmController = pmController;

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

        roundTrip("roundTripEmptyTest");

        assertTrue(passwordManager.getEntries().isEmpty());
        assertEquals(passwordManager.getRootTag(), new Tag("roundTripEmptyTest"));
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

        roundTrip("roundTripOnlyRootTagAndOneEntry");

        assertEquals(passwordManager.getEntries().size(), 1);
        assertEquals(passwordManager.getEntries().get(0), entry);
        assertEquals(passwordManager.getRootTag(), new Tag("roundTripOnlyRootTagAndOneEntry"));
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

        roundTrip("roundTripOnlyManyTagsAndOneEntry");

        assertEquals(passwordManager.getEntries().size(), 1);
        assertEquals(passwordManager.getEntries().get(0), entry);
        assertEquals(passwordManager.getRootTag(), testTag);
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

        Path testFile = Paths.get(tempDir.toString(), "testLoadWithExisting");

        toTest.save(testFile);

        passwordManager.setRootTag(existing);
        passwordManager.getEntries().clear();

        toTest.load(testFile);

        assertEquals(passwordManager.getEntries().size(), 1);
        assertEquals(passwordManager.getEntries().get(0), entry);
        assertEquals(passwordManager.getRootTag(), testTag);
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

        Path testFile = Paths.get(tempDir.toString(), "testLoadOverride");

        toTest.save(testFile);

        passwordManager.setRootTag(existing);
        passwordManager.getEntries().clear();

        toTest.load(testFile);

        assertEquals(1, passwordManager.getEntries().size());
        assertEquals(entry, passwordManager.getEntries().get(0));
        assertEquals(testTag, passwordManager.getRootTag());
    }

    private void roundTrip(String test) throws IOException {
        Path testFile = Paths.get(tempDir.toString(), test);
        toTest.save(testFile);
        toTest.load(testFile);
    }
}
