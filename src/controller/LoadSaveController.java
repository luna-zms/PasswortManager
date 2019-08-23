package controller;

import model.PasswordManager;
import model.Tag;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
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

    /**
     * Loads the password/tag database stored at the given file into {@link SerializationController#pmController}
     * <p>
     * If the current {@link PMController} already contains entries and tags, the loaded data will be merged with
     * the existing data
     *
     * @param path The file to load from
     */
    public void load(String path) {

    }

    /**
     * Saves the password/tag database from {@link SerializationController#pmController} to the given file
     *
     * @param path The file to save to
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void save(String path) {
        PasswordManager passwordManager = pmController.getPasswordManager();

        try (FileOutputStream fos = new FileOutputStream(path); ZipOutputStream zos = new ZipOutputStream(fos)) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, passwordManager.getMasterPasswordKey());

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "MAGIC"); PrintWriter writer = new PrintWriter(cos)) {
                writer.println("siroD");
                writer.println(passwordManager.getLastModified().format(SerializationController.DATE_FORMAT));
                writer.println(passwordManager.getValidUntil().format(SerializationController.DATE_FORMAT));
            } catch (IOException e) {
                // Error creating/writing new ZipEntry for metadata/magic header
                e.printStackTrace();
                return;
            }

            Tag rootTag = passwordManager.getRootTag();

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "ENTRIES")) {
                writeEntriesToStream(cos, passwordManager.getEntries(), rootTag);
            } catch (IOException e) {
                // Error creating/writing new ZipEntry for entries section
                e.printStackTrace();
                return;
            }

            try (CipherOutputStream cos = createEncryptedZipEntry(zos, cipher, "TAGS"); PrintWriter writer = new PrintWriter(cos)) {
                if (rootTag != null)
                    rootTag.createPathMap().values().forEach(writer::println);
            } catch (IOException e) {
                // Error creating/writing new ZipEntry for tags section
                e.printStackTrace();
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
        }
    }

    private CipherOutputStream createEncryptedZipEntry(ZipOutputStream zos, Cipher cipher, String entryName) throws IOException {
        ZipEntry zipEntry = new ZipEntry(entryName);
        zos.putNextEntry(zipEntry);

        return new CipherOutputStream(zos, cipher);
    }

}
