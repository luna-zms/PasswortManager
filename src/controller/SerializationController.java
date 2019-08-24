package controller;

import model.Entry;
import model.SecurityQuestion;
import model.Tag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import util.Tuple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SerializationController {

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    protected PMController pmController;

    protected CSVFormat entryWriteFormat = CSVFormat.DEFAULT.withHeader(EntryTableHeader.class);
    protected CSVFormat entryParseFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();

    protected CSVFormat tagWriteFormat = CSVFormat.DEFAULT.withHeader(TagTableHeader.class);
    protected CSVFormat tagParseFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();

    public abstract void load(String path);

    /**
     *
     */
    public abstract void save(String path);

    /**
     * Creates the tag pointed to by path, constructing parent tags if needed
     *
     * @param path Path in the Tag tree, as an array of tag names
     * @param root Root of tree to operate on
     * @return Tag pointed to by path
     */
    protected Tag createTagFromPath(Tag root, String[] path) {
        if (path == null) return null;
        if (root == null) return null;

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
     * @param os      The OutputStream to write into
     * @param entries List of entries to write
     * @param root    Root the tag tree
     */
    protected void writeEntriesToStream(OutputStream outputStream, List<Entry> entries, Tag root) throws IOException {
        if (outputStream == null) return;
        if (entries == null) return;
        if (root == null) return;

        Map<Tag, String> pathMap = root.createPathMap();

        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT);

        // Print Header
        printer.printRecord(EntryTableHeader.values());
        // Print Entries
        for (Entry entry : entries) {
            String paths = entry.getTags().stream().map(pathMap::get).collect(Collectors.joining(";"));
            // NOTE: null values are written as empty strings. We do not need to special case null values here
            printer.printRecord(
                    entry.getTitle(),
                    entry.getUsername(),
                    entry.getPassword(),
                    entry.getUrl(),
                    entry.getCreatedAt().format(DATE_FORMAT),
                    entry.getLastModified().format(DATE_FORMAT),
                    entry.getValidUntil(),
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
    protected Tuple<List<Entry>, Tag> parseEntries(Iterable<CSVRecord> csvEntries) throws RuntimeException, DateTimeParseException {
        List<Entry> entries = new ArrayList<>();
        Tag build_root = new Tag("build_root");

        HashSet<String> seenRoots = new HashSet<>();

        for (CSVRecord record : csvEntries) {

            if (!record.isConsistent()) {
                throw new RuntimeException("Malformed CSV: Inconsistent number of records in row");
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
                    entry.setCreatedAt(LocalDateTime.parse(createdAt, DATE_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new RuntimeException("Malformed CSV: Invalid Date format");
                }
            }

            String lastModified = record.get(EntryTableHeader.lastModified);
            if (!createdAt.isEmpty()) {
                try {
                    entry.setLastModified(LocalDateTime.parse(lastModified, DATE_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new RuntimeException("Malformed CSV: Invalid Date format");
                }
            }

            String validUntil = record.get(EntryTableHeader.validUntil);
            if (!validUntil.isEmpty()) {
                try {
                    entry.setValidUntil(LocalDateTime.parse(validUntil, DATE_FORMAT));
                } catch (DateTimeParseException exc) {
                    throw new RuntimeException("Malformed CSV: Invalid Date format");
                }
            }

            String url = record.get(EntryTableHeader.url);
            if (!url.isEmpty()) {
                try {
                    entry.setUrl(new URL(url));
                } catch (MalformedURLException exc) {
                    throw new RuntimeException("Malformed CSV: Malformed URL");
                }
            }

            String tagPaths = "build_root\\".concat(record.get(EntryTableHeader.tagPaths));
            String[] paths = tagPaths.split(";");

            entry.getTags().addAll(
                    Arrays.stream(paths)
                            .map(path -> path.split("\\\\"))
                            .peek(path -> {
                                if (path[0] == null) {
                                    throw new RuntimeException("Malformed CSV: Path of length 0");
                                }
                            })
                            .peek(path -> {
                                if (seenRoots.contains(path[0])) {
                                    throw new RuntimeException("Malformed CSV: Multiple roots in CSV");
                                } else {
                                    seenRoots.add(path[0]);
                                }
                            })
                            .map(path -> createTagFromPath(build_root, path))
                            .collect(Collectors.toList())
            );

            entries.add(entry);
        }

        Tag root = build_root.getSubTags().get(0);

        return new Tuple<List<Entry>, Tag>(entries, root);
    }

    protected enum EntryTableHeader {
        title, username, password, url, createdAt, lastModified, validUntil, note, securityQuestion, securityQuestionAnswer, tagPaths;
    }

    protected enum TagTableHeader {
        name, path;
    }
}
