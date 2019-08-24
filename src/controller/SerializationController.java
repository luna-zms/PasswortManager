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

public abstract class SerializationController {

    protected static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;
    protected PMController pmController;

    protected CSVFormat entryWriteFormat = CSVFormat.DEFAULT.withRecordSeparator("\n").withHeader(EntryTableHeader.class);
    protected CSVFormat entryParseFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();

    protected CSVFormat tagWriteFormat = CSVFormat.DEFAULT.withRecordSeparator("\n").withHeader(TagTableHeader.class);
    protected CSVFormat tagParseFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();

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
     * @return Tag pointed to by path
     */
    protected Tag createTagFromPath(Tag root, String[] path) {
        assert (root != null);
        assert (path != null);

        Tag currentTag = root;

        for (String part : Arrays.copyOfRange(path, 1, path.length)) {
            if (currentTag.hasSubTag(part)) {
                currentTag = currentTag.getSubTags().stream().filter(tag -> tag.getName().equals(part)).findFirst().get();
            } else {
                Tag tag = new Tag(part);
                currentTag.getSubTags().add(tag);
                currentTag = tag;
            }
        }
        return currentTag;
    }

    /**
     * Writes all given entries into the given OutputStream
     *
     * @param outputStream The OutputStream to write into
     * @param entries      List of entries to write
     * @param root         Root the tag tree
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
                    entry.getSecurityQuestion().getQuestion(),
                    entry.getSecurityQuestion().getAnswer(),
                    paths
            );
        }
        printer.flush();
    }

    /**
     * Parses CSVRecords into Entries.
     *
     * @param csvEntries CSV records to parse.
     * @return Tuple of entry list and tag tree
     */
    protected Tuple<List<Entry>, Tag> parseEntries(Iterable<CSVRecord> csvEntries) throws CsvException, DateTimeParseException {
        assert (csvEntries != null);

        List<Entry> entries = new ArrayList<>();
        Tag build_root = new Tag("build_root");

        HashSet<String> seenRoots = new HashSet<>();

        for (CSVRecord record : csvEntries) {

            if (!record.isConsistent()) {
                throw new CsvException("Malformed CSV: Inconsistent number of records in row");
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
                    throw new CsvException("Malformed CSV: Invalid Date format");
                }
            }

            String lastModified = record.get(EntryTableHeader.lastModified);
            if (!createdAt.isEmpty()) {
                try {
                    entry.setLastModified(LocalDateTime.parse(lastModified, DATE_TIME_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new CsvException("Malformed CSV: Invalid Date format");
                }
            }

            String validUntil = record.get(EntryTableHeader.validUntil);
            if (!validUntil.isEmpty()) {
                try {
                    entry.setValidUntil(LocalDate.parse(validUntil, DATE_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new CsvException("Malformed CSV: Invalid Date format");
                }
            }

            String url = record.get(EntryTableHeader.url);
            if (!url.isEmpty()) {
                try {
                    entry.setUrl(new URL(url));
                } catch (MalformedURLException exc) {
                    throw new CsvException("Malformed CSV: Malformed URL");
                }
            }

            String tagPaths = record.get(EntryTableHeader.tagPaths);
            String[] paths = tagPaths.split(";");

            entry.getTags().addAll(
                    Arrays.stream(paths)
                            .map(path -> "build_root\\".concat(path))
                            .map(path -> path.split("\\\\"))
                            .peek(path -> {
                                if (path.length < 2) {
                                    throw new CsvException("Malformed CSV: Path of length 0");
                                }
                            })
                            .peek(path -> {
                                if (seenRoots.size() > 1) {
                                    throw new CsvException("Malformed CSV: Multiple roots in CSV");
                                } else {
                                    seenRoots.add(path[0]);
                                }
                            })
                            .map(path -> createTagFromPath(build_root, path))
                            .collect(Collectors.toList())
            );

            entries.add(entry);
        }

        Tag root = null;
        if (build_root.getSubTags().size() > 0) {
            root = build_root.getSubTags().get(0);
        }

        return new Tuple<>(entries, root);
    }

    protected enum EntryTableHeader {
        title, username, password, url, createdAt, lastModified, validUntil, note, securityQuestion, securityQuestionAnswer, tagPaths
    }

    protected enum TagTableHeader {
        name, path
    }
}
