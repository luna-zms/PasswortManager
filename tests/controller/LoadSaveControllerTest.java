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
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static junit.framework.TestCase.*;

public class LoadSaveControllerTest {
    private LoadSaveController toTest;
    private PMController pmController;
    private PasswordManager passwordManager;

    private Path tempDir;

    @Before
    public void setUp() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey key = keyGen.generateKey();

        passwordManager = new PasswordManager(key);
        pmController = new PMController();

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
        passwordManager.setRootTag(new Tag("TestTag"));

        roundTrip(Paths.get(tempDir.toString(), "roundTripEmptyTest"));

        System.out.println(passwordManager.getEntries().get(0));

        assertTrue(passwordManager.getEntries().isEmpty());
        assertEquals(passwordManager.getRootTag(), new Tag("TestTag"));
    }

    @Test
    public void roundTripOnlyRootTagAndOneEntry() throws IOException {
        Tag testTag = new Tag("TestTag");
        Entry entry = new Entry("Test", "Test");

        entry.getTags().add(testTag);

        passwordManager.setRootTag(testTag);
        passwordManager.getEntries().add(entry);

        roundTrip(Paths.get(tempDir.toString(), "roundTripOnlyRootTagAndOneEntry"));

        assertEquals(passwordManager.getEntries().size(), 1);
        assertEquals(passwordManager.getEntries().get(0), entry);
        assertEquals(passwordManager.getRootTag(), new Tag("TestTag"));
    }

    private void roundTrip(Path testFile) throws IOException {
        toTest.save(testFile);

        passwordManager.setRootTag(null);
        passwordManager.getEntries().clear();

        toTest.load(testFile);
    }
}
