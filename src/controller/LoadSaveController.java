package controller;

import model.Entry;
import model.PasswordManager;
import model.Tag;
import org.apache.commons.csv.CSVParser;
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
@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.CyclomaticComplexity"})
public class LoadSaveController extends SerializationController {

    /**
     * Loads the password/tag database stored at the given file into {@link SerializationController#pmController}
     * <p>
     * If the current {@link PMController} already contains entries and tags, the loaded data will be merged with
     * the existing data
     *
     * @param path The file to load from
     */
    public void load(Path path) throws IOException {
        PasswordManager wtf = pmController.getPasswordManager();

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, wtf.getMasterPasswordKey());

            try (InputStream fis = Files.newInputStream(path);
                 ZipInputStream zis = new ZipInputStream(fis)) {
                LocalDateTime lastModified;
                LocalDateTime validUntil;

                List<Entry> entries;
                Tag rootTag;

                try (CipherInputStream cis = readEncryptedZipEntry(zis, cipher, "MAGIC");
                     InputStreamReader isr = new InputStreamReader(cis);
                     BufferedReader bur = new BufferedReader(isr)) {

                    String magicLine = bur.readLine();

                    if (!"siroD".equals(magicLine))
                        throw new IOException("Mismatch in Magic Bytes, is the password correct? Read: " + magicLine);

                    lastModified = LocalDateTime.parse(bur.readLine(), SerializationController.DATE_TIME_FORMAT);
                    validUntil = LocalDateTime.parse(bur.readLine(), SerializationController.DATE_TIME_FORMAT);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }

                try (CipherInputStream cis = readEncryptedZipEntry(zis, cipher, "ENTRIES");
                     InputStreamReader isr = new InputStreamReader(cis)) {
                    Tuple<List<Entry>, Tag> tup = parseEntries(new CSVParser(isr, entryParseFormat));

                    entries = tup.first();
                    rootTag = tup.second();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }

                try (CipherInputStream cis = readEncryptedZipEntry(zis, cipher, "TAGS");
                     InputStreamReader isr = new InputStreamReader(cis);
                     BufferedReader bur = new BufferedReader(isr)) {

                    bur.lines().forEach(line -> createTagFromPath(rootTag, line.split("\\\\")));

                    wtf.setEntries(entries);

                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }

                wtf.setRootTag(rootTag);
                wtf.setLastModified(lastModified);
                wtf.setValidUntil(validUntil);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private CipherInputStream readEncryptedZipEntry(ZipInputStream zis, Cipher cipher, String expectedName) throws IOException {
        ZipEntry entry = zis.getNextEntry();

        if (entry == null)
            throw new IOException("Unexpected end of zip file");

        if (!expectedName.equals(entry.getName()))
            throw new IOException("Unexpected zip entry. Expected '" + expectedName + "', got '" + entry.getName() + "'");

        // CipherInputStream does the same bullshit CipherOutputStream does (see createEncryptedZipEntry())

        InputStream unclosableStream = new InputStream() {
            @Override
            public int read(byte[] b) throws IOException {
                return zis.read(b);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return zis.read(b, off, len);
            }

            @Override
            public long skip(long n) throws IOException {
                return zis.skip(n);
            }

            @Override
            public int available() throws IOException {
                return zis.available();
            }

            @Override
            public void close() throws IOException {
            }

            @Override
            public synchronized void mark(int readlimit) {
                zis.mark(readlimit);
            }

            @Override
            public synchronized void reset() throws IOException {
                zis.reset();
            }

            @Override
            public boolean markSupported() {
                return zis.markSupported();
            }

            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        return new CipherInputStream(unclosableStream, cipher);
    }

    /**
     * Saves the password/tag database from {@link SerializationController#pmController} to the given file
     *
     * @param path The file to save to
     */
    public void save(Path path) throws IOException {
        PasswordManager passwordManager = pmController.getPasswordManager();

        try (OutputStream fos = Files.newOutputStream(path); ZipOutputStream zos = new ZipOutputStream(fos)) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, passwordManager.getMasterPasswordKey());

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "MAGIC"); PrintWriter writer = new PrintWriter(cos)) {
                writer.println("siroD");
                writer.println(passwordManager.getLastModified().format(SerializationController.DATE_TIME_FORMAT));
                writer.println(passwordManager.getValidUntil().format(SerializationController.DATE_TIME_FORMAT));
            } catch (IOException e) {
                // Error creating/writing new ZipEntry for metadata/magic header
                e.printStackTrace();
                throw e;
            }

            Tag rootTag = passwordManager.getRootTag();

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "ENTRIES")) {
                writeEntriesToStream(cos, passwordManager.getEntries(), rootTag);
            } catch (IOException e) {
                // Error creating/writing new ZipEntry for entries section
                e.printStackTrace();
                throw e;
            }

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "TAGS"); PrintWriter writer = new PrintWriter(cos)) {
                if (rootTag != null)
                    rootTag.createPathMap().values().forEach(writer::println);
            } catch (IOException e) {
                // Error creating/writing new ZipEntry for tags section
                e.printStackTrace();
                throw e;
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            // The java installation does not support AES encryption for some reason
            e.printStackTrace();
            System.exit(-1);
        } catch (InvalidKeyException e) {
            // Internal error: The master password key was invalid, meaning there was an error in the cdf!
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            // Error while accessing the file (path to file didn't exist, missing write permissions, etc)
            e.printStackTrace();
            throw e;
        }
    }

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

        OutputStream unclosableStream = new OutputStream() {
            @Override
            public void write(byte[] b) throws IOException {
                zos.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                zos.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                zos.flush();
            }

            @Override
            public void close() throws IOException {
            }

            @Override
            public void write(int i) throws IOException {
                zos.write(i);
            }
        };

        return new CipherOutputStream(unclosableStream, cipher);
    }

}
