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
import java.nio.file.Paths;
import java.util.List;

public class ImportExportController extends SerializationController {

    /**
     * Reads a file for importing and passes the data to parseEntries for further use.
     *
     * @param path The file path to read from
     * @author sopr011
     */
    @Override
    public void load(String path) {
        CSVParser csvParser = null;

        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(path))) {
            csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }

        Tuple<List<Entry>, Tag> result = parseEntries(csvParser);
        pmController.getPasswordManager().setEntries(result.first());
        pmController.getPasswordManager().setRootTag(result.second());
    }

    /**
     * Writes an output stream to a file for exporting using writeEntriesToStream to fill the stream.
     *
     * @param path The file path to write to
     * @author sopr011
     */
    @Override
    public void save(String path) {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(path))) {
            PasswordManager passwordManager = pmController.getPasswordManager();
            writeEntriesToStream(outputStream, passwordManager.getEntries(), passwordManager.getRootTag());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

}
