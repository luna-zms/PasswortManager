package controller;

import model.Entry;
import model.SecurityQuestion;
import model.Tag;
import util.Tuple;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class SerializationController {

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    protected PMController pmController;

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
     * @param os The OutputStream to write into
     * @param entries List of entries to write
     * @param root Root the tag tree 

     */
    protected void writeEntriesToStream(OutputStream outputStream, List<Entry> entries, Tag root) throws IOException {
        if (outputStream == null) return;
        if (entries == null) return;
        if (root == null) return;
        
        Map<Tag, String> pathMap = root.createPathMap();

        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT);

        // Print Header
        printer.printRecord(EntryTableHeader.values());
        printer.println();
        // Print Entries
        for (Entry entry : entries) {
            String paths = entry.getTags().stream().map(pathMap::get).collect(Collectors.joining(";"));
            // NOTE: Url and validUntil can be null in entries. All other fields are initialized.
            printer.printRecord(
                    entry.getTitle(),
                    entry.getUsername(),
                    entry.getPassword(),
                    entry.getUrl() == null ? "" : entry.getUrl(),
                    entry.getCreatedAt().format(DATE_FORMAT),
                    entry.getLastModified().format(DATE_FORMAT),
                    entry.getValidUntil() == null ? "" : entry.getValidUntil().format(DATE_FORMAT),
                    entry.getNote(),
                    entry.getSecurityQuestion().getQuestion(),
                    entry.getSecurityQuestion().getAnswer(),
                    paths
            );
            printer.println();
        }
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

            Entry entry = new Entry(record.get(EntryTableHeader.TITLE), record.get(EntryTableHeader.PASSWORD));

            entry.setUsername(record.get(EntryTableHeader.USERNAME));
            entry.setCreatedAt(LocalDateTime.parse(record.get(EntryTableHeader.CREATED_AT), DATE_FORMAT));
            entry.setLastModified(LocalDateTime.parse(record.get(EntryTableHeader.LAST_MODIFIED), DATE_FORMAT));
            entry.setNote(record.get(EntryTableHeader.NOTE));
            String question = record.get(EntryTableHeader.SECURITY_QUESTION);
            String answer = record.get(EntryTableHeader.SECURITY_QUESTION_ANSWER);
            SecurityQuestion securityQuestion = new SecurityQuestion(question, answer);
            entry.setSecurityQuestion(securityQuestion);
            
            // NOTE: validUntil and Url can both be null.
            // All other entry properties are always initialized.
            String validUntil = record.get(EntryTableHeader.VALID_UNTIL);
            if (validUntil != null) {
                entry.setValidUntil(LocalDateTime.parse(validUntil, DATE_FORMAT));
            }
            
            String url = record.get(EntryTableHeader.URL);
            if (validUntil != null) {
                try {
                    entry.setUrl(new URL(url));
                }
                catch (MalformedURLException e) {
                    throw new RuntimeException("Malformed CSV: Malformed URL");
                }
            }
            


            String tagPaths = "build_root\\".concat(record.get(EntryTableHeader.TAG_PATHS));
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
    
    private enum EntryTableHeader {
        TITLE, USERNAME, PASSWORD, URL, CREATED_AT, LAST_MODIFIED, VALID_UNTIL, NOTE, SECURITY_QUESTION, SECURITY_QUESTION_ANSWER, TAG_PATHS;

        @Override
        @SuppressWarnings("PMD.CyclomaticComplexity")
        public String toString() {
            switch (this) {
                case TITLE:
                    return "Title";
                case USERNAME:
                    return "Username";
                case PASSWORD:
                    return "Password";
                case CREATED_AT:
                    return "CreatedAt";
                case URL:
                    return "Url";
                case LAST_MODIFIED:
                    return "LastModified";
                case VALID_UNTIL:
                    return "ValidUntil";
                case NOTE:
                    return "Note";
                case SECURITY_QUESTION:
                    return "SecurityQuestion";
                case SECURITY_QUESTION_ANSWER:
                    return "SecurityQuestionAnswer";
                case TAG_PATHS:
                    return "TagPaths";
                default:
                    return ""; // Note: Never used. This switch is exhaustive
            }
        }
    }
}
