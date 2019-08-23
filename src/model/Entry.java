package model;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Entry {
    private String title, username, password, note;
    private URL url;
    private LocalDateTime createdAt, lastModified, validUntil;
    private SecurityQuestion securityQuestion;
    private List<Tag> tags;

    public Entry(String title, String password) {
        this.title = title;
        this.password = password;

        createdAt = LocalDateTime.now();
        lastModified = createdAt;
        tags = new ArrayList<>();

        username = "";
        note = "";
        securityQuestion = new SecurityQuestion("", "");

        url = null;
        validUntil = null;
    }

    private static String stringFromDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)) : "";
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getUrlString() {
        return url != null ? url.toString() : "";
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtString() {
        return stringFromDateTime(createdAt);
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedString() {
        return stringFromDateTime(lastModified);
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public String getValidUntilString() {
        return stringFromDateTime(validUntil);
    }

    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entry entry = (Entry) obj;
        return title.equals(entry.title) &&
                username.equals(entry.username) &&
                password.equals(entry.password) &&
                note.equals(entry.note) &&
                Objects.equals(url, entry.url) &&
                createdAt.equals(entry.createdAt) &&
                lastModified.equals(entry.lastModified) &&
                Objects.equals(validUntil, entry.validUntil) &&
                Objects.equals(securityQuestion, entry.securityQuestion) &&
                tags.equals(entry.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, username, password, note, url, createdAt, lastModified, validUntil, securityQuestion, tags);
    }
}
