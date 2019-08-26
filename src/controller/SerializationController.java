package controller;

import model.Entry;
import model.SecurityQuestion;
import model.Tag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import util.CsvException;
import util.Tuple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract base class for serialization controllers. Provides constants, enums, and utility
 * functions for reading password databases from CSV and writing password databases to CSV.
 */
public abstract class SerializationController {

    protected static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;
    protected PMController pmController;

    protected CSVFormat entryWriteFormat = CSVFormat.DEFAULT.withRecordSeparator("\n").withHeader(EntryTableHeader.class);
    protected CSVFormat entryParseFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();

    public abstract void load(Path path) throws IOException;

    /**
     *
     */
    public abstract void save(Path path) throws IOException;

    /**
     * Creates the tag pointed to by path, constructing parent tags if needed
     *
     * @param path Path in the Tag tree, as an array of tag names
     * @param root Root of tree to operate on
     * @return Tag Tag pointed to by path
     */
    protected Tag createTagFromPath(Tag root, String[] path) {
        assert (root != null);
        assert (path != null);

        Tag currentTag = root;

        for (String part : Arrays.copyOfRange(path, 1, path.length)) {
            if (currentTag.hasSubTag(part)) {
                currentTag = currentTag.getSubTagByName(part);
            } else {
                Tag tag = new Tag(part);
                currentTag.getSubTags().add(tag);
                currentTag = tag;
            }
        }
        return currentTag;
    }

    /**
     * Writes all given entries into the given OutputStream.
     *
     * @param outputStream The OutputStream to write into
     * @param entries      List of entries to write
     * @param root         Root of the tag tree, used to map entry tags to paths in the csv records
     * @throws IOException if an I/O Exception occurs
     */
    protected void writeEntriesToStream(OutputStream outputStream, List<Entry> entries, Tag root) throws IOException {
        assert (outputStream != null);
        assert (entries != null);
        assert (root != null);

        Map<Tag, String> pathMap = root.createPathMap();

        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(outputStream), entryWriteFormat);

        // Print Entries
        for (Entry entry : entries) {
            String paths = entry.getTags().stream().map(pathMap::get).collect(Collectors.joining(";"));
            // NOTE: null values are written as empty strings.
            printer.printRecord(
                    entry.getTitle(),
                    entry.getUsername(),
                    entry.getPassword(),
                    entry.getUrl(),
                    (entry.getCreatedAt() != null) ? entry.getCreatedAt().format(DATE_TIME_FORMAT) : "",
                    (entry.getCreatedAt() != null) ? entry.getLastModified().format(DATE_TIME_FORMAT) : "",
                    (entry.getValidUntil() != null) ? entry.getValidUntil().format(DATE_FORMAT) : "",
                    entry.getNote(),
                    (entry.getSecurityQuestion() != null) ? entry.getSecurityQuestion().getQuestion() : "",
                    (entry.getSecurityQuestion() != null) ? entry.getSecurityQuestion().getAnswer() : "",
                    paths
            );
        }
        printer.flush();
    }

    /**
     * Parses CSVRecords into a list of Entries and a tag tree.
     *
     * @param csvEntries CSV records to parse.
     * @return Tuple of entry list and tag tree
     * @throws CsvException Throws a CsvException if a csv record is malformed or contains invalid data
     */
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.CyclomaticComplexity"})
    protected Tuple<List<Entry>, Tag> parseEntries(Iterable<CSVRecord> csvEntries) throws CsvException, DateTimeParseException {
        assert (csvEntries != null);

        List<Entry> entries = new ArrayList<>();
        Tag buildRoot = new Tag("build_root");


        for (CSVRecord record : csvEntries) {

            if (!record.isConsistent()) {
                throw new CsvException("Ungültiges CSV: Inkonsistente Anzahl Felder in Zeile");
            }

            // null values are written and read as empty Strings

            // String attributes
            // Since entries are initiated with empty strings, we can just write the read Strings back
            Entry entry = new Entry(record.get(EntryTableHeader.title), record.get(EntryTableHeader.password));
            entry.setUsername(record.get(EntryTableHeader.username));
            entry.setNote(record.get(EntryTableHeader.note));

            String question = record.get(EntryTableHeader.securityQuestion);
            String answer = record.get(EntryTableHeader.securityQuestionAnswer);
            SecurityQuestion securityQuestion = new SecurityQuestion(question, answer);
            entry.setSecurityQuestion(securityQuestion);

            // Non String attributes
            // After initialization these attributes have a non empty string representation.
            // Such, if we read an empty string we keep the default value, otherwise we parse the string into
            // an object

            String createdAt = record.get(EntryTableHeader.createdAt);
            if (!createdAt.isEmpty()) {
                try {
                    entry.setCreatedAt(LocalDateTime.parse(createdAt, DATE_TIME_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new CsvException("Ungültiges CSV: Ungültiges Datumsformat");
                }
            }

            String lastModified = record.get(EntryTableHeader.lastModified);
            if (!createdAt.isEmpty()) {
                try {
                    entry.setLastModified(LocalDateTime.parse(lastModified, DATE_TIME_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new CsvException("Ungültiges CSV: Ungültiges Datumsformat");
                }
            }

            String validUntil = record.get(EntryTableHeader.validUntil);
            if (!validUntil.isEmpty()) {
                try {
                    entry.setValidUntil(LocalDate.parse(validUntil, DATE_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new CsvException("Ungültiges CSV: Ungültiges Datumsformat");
                }
            }

            String url = record.get(EntryTableHeader.url);
            if (!url.isEmpty()) {
                try {
                    entry.setUrl(new URL(url));
                } catch (MalformedURLException exc) {
                    throw new CsvException("Ungültiges CSV: Ungültige URL");
                }
            }

            String tagPaths = record.get(EntryTableHeader.tagPaths);
            String[] paths = tagPaths.split(";", -42);
            HashSet<String> seenRoots = new HashSet<>();
            
            final int minimumPathLength = 2;
            final int maximumRoots = 1;

            entry.getTags().addAll(
                    Arrays.stream(paths)
                            .map(path -> "build_root\\".concat(path))
                            .map(path -> path.split("\\\\"))
                            .peek(path -> {
                                if (path.length < minimumPathLength) {
                                    throw new CsvException("Ungültiges CSV: Tag Pfad der Länge Null");
                                }
                            })
                            .peek(path -> seenRoots.add(path[1]))
                            .map(path -> createTagFromPath(buildRoot, path))
                            .collect(Collectors.toList())
            );

            if (seenRoots.size() > maximumRoots) {
                throw new CsvException("Ungültiges CSV: Mehrere Wurzeln in Tag Baum");
            }

            entries.add(entry);
        }

        Tag root = null;
        if (buildRoot.getSubTags().size() > 0) {
            root = buildRoot.getSubTags().get(0);
        }

        return new Tuple<>(entries, root);
    }

    protected enum EntryTableHeader {
        title, username, password, url, createdAt, lastModified, validUntil, note, securityQuestion, securityQuestionAnswer, tagPaths
    }
}
