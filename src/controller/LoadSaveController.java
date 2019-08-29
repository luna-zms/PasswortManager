package controller;

import model.Entry;
import model.Tag;
import org.apache.commons.csv.CSVParser;
import util.BadPasswordException;
import util.Tuple;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Controller for loading and saving the program data in a save file
 * <p>
 * The save format is a `.zip` file with 3 entries:
 * 1. A Metadata/Magic word entry. Handles password validating by checking if the magic word matches after decryption
 * and contains the master password metadata
 * 2. A Password database entry. Stores the list of managed passwords in the same CSV format
 * as {@link controller.ImportExportController}
 * 3. A Tag database entry. Store a list of all tags, even those that aren't associated with any entries.
 * Each tag is stored on its own line and in the same format as in the same list per entry.
 * <p>
 * De-/Encryption is done using standard AES. Each entry of the `.zip` file will be encrypted individually, the file as
 * a whole will not be encrypted
 */
public class LoadSaveController extends SerializationController {
    private final PMController pmController;

    private static final String MAGIC_WORD = "siroD";

    public LoadSaveController(PMController pmController) {
        super(pmController.getPasswordManager());

        this.pmController = pmController;
    }

    /**
     * Loads the first zip entry containing the magic number as well as administrative functionality,
     * namely the lastModified and the validUntil date.
     *
     * @param cipher Contains the given cipher to create a cipher input stream with.
     * @param zipInputStream Contains the overall zip input stream from where to read the first entry from.
     *
     * @return A tuple containing the lastModified date and the validUntil date (last one may be null), respectively.
     * @throws IOException When something with the streams goes badly wrong.
     * @throws BadPasswordException When the zip entry can not be decrypted.
     */
    private Tuple<LocalDateTime, LocalDateTime> loadMagicZipEntry(Cipher cipher, ZipInputStream zipInputStream)
            throws IOException, BadPasswordException {
        LocalDateTime lastModified, validUntil = null;
        try (CipherInputStream cis = readEncryptedZipEntry(zipInputStream, cipher, "MAGIC");
             InputStreamReader isr = new InputStreamReader(cis);
             BufferedReader bur = new BufferedReader(isr)) {

            String magicLine;
            try {
                magicLine = bur.readLine();
            } catch (IOException ioc) {
                ioc.printStackTrace();
                throw new BadPasswordException(ioc.getMessage());
            }

            if (!MAGIC_WORD.equals(magicLine))
                throw new BadPasswordException("Mismatch in Magic Bytes, is the password correct? Read: " + magicLine);

            lastModified = LocalDateTime.parse(bur.readLine(), SerializationController.DATE_TIME_FORMAT);
            String validUntilLine = bur.readLine();
            if (validUntilLine != null)
                validUntil = LocalDateTime.parse(validUntilLine, SerializationController.DATE_TIME_FORMAT);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return new Tuple<>(lastModified, validUntil);
    }

    /**
     * Load the saved tag tree from the last zip entry.
     *
     * @param cipher Contains the given cipher to create a cipher input stream with.
     * @param zipInputStream Contains the overall zip input stream from where to read the last entry from.
     * @param readRootTag Contains the later root tag to save all read tags in.
     *
     * @throws IOException When something goes wrong when handling with the input streams.
     */
    private void loadTagTreeZipEntry(Cipher cipher, ZipInputStream zipInputStream, Tag readRootTag)
            throws IOException {
        try (CipherInputStream cis = readEncryptedZipEntry(zipInputStream, cipher, "TAGS");
             InputStreamReader isr = new InputStreamReader(cis);
             BufferedReader bur = new BufferedReader(isr)) {

            String nextLine;

            while ((nextLine = bur.readLine()) != null) {
                if (nextLine.isEmpty())
                    throw new IOException("Invalid Tag read from save file.");

                if (readRootTag == null)
                    readRootTag = new Tag(nextLine.split("\\\\")[0]);

                createTagFromPath(readRootTag, nextLine.split("\\\\"));
            }
        }
    }

    /**
     * Loads the password/tag database stored at the given file into {@link SerializationController#passwordManager}
     * <p>
     * If the current {@link PMController} already contains entries and tags, the loaded data will be merged with
     * the existing data
     *
     * @param path The file to load from
     */
    public void load(Path path) throws IOException, BadPasswordException {
        try (InputStream fis = Files.newInputStream(path);
             ZipInputStream zis = new ZipInputStream(fis)) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, passwordManager.getMasterPasswordKey());

            List<Entry> entries;
            Tag readRootTag;

            Tuple<LocalDateTime, LocalDateTime> gotDates = loadMagicZipEntry(cipher, zis);
            LocalDateTime lastModified = gotDates.first(), validUntil = gotDates.second();

            try (CipherInputStream cis = readEncryptedZipEntry(zis, cipher, "ENTRIES");
                 InputStreamReader isr = new InputStreamReader(cis)) {
                Tuple<List<Entry>, Tag> tup = parseEntries(new CSVParser(isr, entryParseFormat));

                entries = tup.first();
                readRootTag = tup.second();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }

            loadTagTreeZipEntry(cipher, zis, readRootTag);

            String fileName = path.getFileName().toString();

            readRootTag.setName(fileName.substring(0, fileName.lastIndexOf(".gate")));

            passwordManager.setRootTag(readRootTag);
            passwordManager.setEntries(entries);
            passwordManager.setLastModified(lastModified);
            passwordManager.setValidUntil(validUntil);

            // If we got here, loading worked
            pmController.setDirty(false);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static class UnclosableInputStream extends InputStream {
        ZipInputStream zipInputStream;
        UnclosableInputStream(ZipInputStream zipInputStream) {
            super();
            this.zipInputStream = zipInputStream;
        }

        @Override
        public int read(byte[] bytes) throws IOException {
            return zipInputStream.read(bytes);
        }

        @Override
        public int read(byte[] bytes, int off, int len) throws IOException {
            return zipInputStream.read(bytes, off, len);
        }

        @Override
        public long skip(long longInteger) throws IOException {
            return zipInputStream.skip(longInteger);
        }

        @Override
        public int available() throws IOException {
            return zipInputStream.available();
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public synchronized void mark(int readlimit) {
            zipInputStream.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            zipInputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return zipInputStream.markSupported();
        }

        @Override
        public int read() throws IOException {
            return zipInputStream.read();
        }
    };

    private CipherInputStream readEncryptedZipEntry(ZipInputStream zis, Cipher cipher, String expectedName) throws IOException {
        ZipEntry entry = zis.getNextEntry();

        if (entry == null)
            throw new IOException("Unexpected end of zip file");

        if (!expectedName.equals(entry.getName()))
            throw new IOException("Unexpected zip entry. Expected '" + expectedName + "', got '" + entry.getName() + "'");

        // CipherInputStream does the same bullshit CipherOutputStream does (see createEncryptedZipEntry())

        InputStream unclosableStream = new UnclosableInputStream(zis);

        return new CipherInputStream(unclosableStream, cipher);
    }

    /**
     * Saves the password/tag database from {@link SerializationController#passwordManager} to the given file
     *
     * @param path The file to save to
     */
    public void save(Path path) throws IOException {
        try (OutputStream fos = Files.newOutputStream(path); ZipOutputStream zos = new ZipOutputStream(fos)) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, passwordManager.getMasterPasswordKey());

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "MAGIC"); PrintWriter writer = new PrintWriter(cos)) {
                writer.println("siroD");
                writer.println(passwordManager.getLastModified().format(SerializationController.DATE_TIME_FORMAT));
                if (passwordManager.getValidUntil() != null)
                    writer.println(passwordManager.getValidUntil().format(SerializationController.DATE_TIME_FORMAT));
            }

            Tag rootTag = passwordManager.getRootTag();

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "ENTRIES")) {
                writeEntriesToStream(cos, passwordManager.getEntries(), rootTag);
            }

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "TAGS"); PrintWriter writer = new PrintWriter(cos)) {
                rootTag.createPathMap().values().forEach(writer::println);
            }

            // If we got here, the file got save so we can safely reset the dirty flag
            pmController.setDirty(false);
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException exception) {
            // The java installation does not support AES encryption for some reason
            throw new RuntimeException(exception);
        }
    }

    private static class UnclosableOutputStream extends OutputStream {
        ZipOutputStream zipOutputStream;

        UnclosableOutputStream(ZipOutputStream zipOutputStream) {
            this.zipOutputStream = zipOutputStream;
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            zipOutputStream.write(bytes);
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            zipOutputStream.write(bytes, off, len);
        }

        @Override
        public void flush() throws IOException {
            zipOutputStream.flush();
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public void write(int integer) throws IOException {
            zipOutputStream.write(integer);
        }
    };

    private CipherOutputStream createEncryptedZipEntry(ZipOutputStream zos, Cipher cipher, String entryName) throws IOException {
        ZipEntry zipEntry = new ZipEntry(entryName);
        zos.putNextEntry(zipEntry);

        // Following problem: The CipherOutputStream only finalizes encryption in its .close() method.
        // This means we need to close the CipherOutputStream every time we finish writing an ZipEntry before moving
        // on to the next.
        // HOWEVER, closing the CipherOutputStream also closes the underlying stream, which is the ZipOutputStream
        // in our case. That means that we could only write a single encrypted ZipEntry. So we either have to
        // re-open our ZipOutputStream for every item (which is a stupid solution)
        // or we wrap out ZipOutputStream into a OutputStream that ignores the CipherOutputStream closing it.
        // Which is what is done below

        OutputStream unclosableStream = new UnclosableOutputStream(zos);

        return new CipherOutputStream(unclosableStream, cipher);
    }
}
