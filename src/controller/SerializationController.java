package controller;

import java.io.OutputStream;
import java.util.List;

import model.Entry;
import model.Tag;
import org.apache.commons.csv.CSVRecord;

public abstract class SerializationController {

    protected PMController pmController;

    public abstract void load(String path);

    /**
     *
     */
    public abstract void save(String path);

    /**
     * @param path
     * @return
     */
    protected Tag createTagFromPath(String[] path) {
        return null;
    }

    /**
     *
     */
    protected void writeEntriesToStream(OutputStream os) {

    }

    /**
     *
     */
    protected List<Entry> parseEntries(Iterable<CSVRecord> csvEntries) {
        return null;
    }

}
