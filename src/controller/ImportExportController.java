package controller;

import model.Entry;
import model.PasswordManager;
import model.Tag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import util.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportExportController extends SerializationController {
    private PMController pmController;

    public ImportExportController(PMController pmController) {
        super(pmController.getPasswordManager());
        this.pmController = pmController;
    }

    /**
     * Reads a file for importing and passes the data to parseEntries for further use.
     *
     * @param path The file path to read from
     * @author sopr011
     */
    @Override
    public void load(Path path) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            CSVParser csvParser = new CSVParser(bufferedReader, entryParseFormat);

            Tuple<List<Entry>, Tag> result = parseEntries(csvParser);
            passwordManager.mergeWith(result.first(), result.second());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        pmController.setDirty(true);
    }

    /**
     * Writes an output stream to a file for exporting using writeEntriesToStream to fill the stream.
     *
     * @param path The file path to write to
     * @author sopr011
     */
    @Override
    public void save(Path path) {
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            writeEntriesToStream(outputStream, passwordManager.getEntries(), passwordManager.getRootTag());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

}
