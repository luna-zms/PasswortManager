package controller;

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
    public void save(String path) {

    }

}
