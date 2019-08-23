package controller;

import model.Entry;
import model.SecurityQuestion;
import model.Tag;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
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
        
        if (root == null) {
            root = new Tag(path[0]);
            pmController.getPasswordManager().setRootTag(root);
            return root;
        }
        
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
     * Writes all entries stored in the PasswordManager into the given OutputStream
     *
     * @param os The OutputStream to write into
     */
    protected void writeEntriesToStream(OutputStream outputStream, List<Entry> entries) throws IOException {
        if (outputStream == null) return;
        
        Map<Tag, String> pathMap = pmController.getPasswordManager().getRootTag().createPathMap();

        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(outputStream), CSVFormat.DEFAULT);

        // Print Header
        printer.printRecord(EntryTableHeader.values());
        // Print Entries
        for (Entry entry : entries) {
            String paths = entry.getTags().stream().map(pathMap::get).collect(Collectors.joining(";"));
            printer.printRecord(
                    entry.getTitle(),
                    entry.getUsername(),
                    entry.getPassword(),
                    entry.getUrl(),
                    entry.getCreatedAt().format(DATE_FORMAT),
                    entry.getLastModified().format(DATE_FORMAT),
                    entry.getValidUntil().format(DATE_FORMAT),
                    entry.getNote(),
                    entry.getSecurityQuestion().getQuestion(),
                    entry.getSecurityQuestion().getAnswer(),
                    paths
            );
        }
    }

    /**
     * Parses CSVRecords into Entries. Entries are added to the password manager. Tags are added to the tag tree.
     *
     * @param csvEntries CSV records to parse.
     */
    protected void parseEntries(Iterable<CSVRecord> csvEntries) {
        List<Entry> entries = new ArrayList<>();
        Tag root = new Tag("build_root");
        
        for (CSVRecord record : csvEntries) {

            Entry entry = new Entry(record.get(EntryTableHeader.TITLE), record.get(EntryTableHeader.PASSWORD));

            entry.setUsername(record.get(EntryTableHeader.USERNAME));
            entry.setCreatedAt(LocalDateTime.parse(record.get(EntryTableHeader.CREATED_AT), DATE_FORMAT));
            entry.setLastModified(LocalDateTime.parse(record.get(EntryTableHeader.LAST_MODIFIED), DATE_FORMAT));
            entry.setValidUntil(LocalDateTime.parse(record.get(EntryTableHeader.VALID_UNTIL), DATE_FORMAT));
            entry.setNote(record.get(EntryTableHeader.NOTE));
            String question = record.get(EntryTableHeader.SECURITY_QUESTION);
            String answer = record.get(EntryTableHeader.SECURITY_QUESTION_ANSWER);
            SecurityQuestion securityQuestion = new SecurityQuestion(question, answer);
            entry.setSecurityQuestion(securityQuestion);
            


            String tagPaths = "build_root\\".concat(record.get(EntryTableHeader.TAG_PATHS));
            String[] paths = tagPaths.split(";");
            
            entry.getTags().addAll(
                    Arrays.stream(paths)
                            .map(path -> path.split("\\\\"))
                            .map(path -> createTagFromPath(root, path))
                            .collect(Collectors.toList())
            );

            entries.add(entry);
        }
    }
    
    private enum EntryTableHeader {
        TITLE, USERNAME, PASSWORD, CREATED_AT, LAST_MODIFIED, VALID_UNTIL, NOTE, SECURITY_QUESTION, SECURITY_QUESTION_ANSWER, TAG_PATHS;

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
