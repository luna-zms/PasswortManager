package controller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImportExportController extends SerializationController {

    /**
     * Reads a file for importing and passes the data to parseEntries for further use.
     *
     * @author sopr011
     *
     * @param  path The file path to read from
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

        parseEntries(csvParser);
    }

    /**
     * Writes an output stream to a file for exporting using writeEntriesToStream to fill the stream.
     *
     * @author sopr011
     *
     * @param  path The file path to write to
     */
    @Override
    public void save(String path) {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(path))) {
            writeEntriesToStream(outputStream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

}
