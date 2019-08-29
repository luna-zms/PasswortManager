package model;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import util.DateFormatUtil;


/**
 * this class is used to set Entry
 * @author sopr016
 *
 */
public class Entry {
    public static final Callback<Entry, Observable[]> OBSERVABLE_PROPS = entry -> new Observable[]{
            entry.tagsObservable()
    };

    private String title, username, password, note;
    private URL url;
    private LocalDateTime createdAt, lastModified;
    private LocalDate validUntil;
    private SecurityQuestion securityQuestion;
    private ObservableList<Tag> tags = FXCollections.observableArrayList(Tag.OBSERVABLE_PROPS);


    /**
     * Constructor sets the minimal required attributes title and password
     * and the cratedAt or lastModifiedAt
     * @param title
     * @param password
     */
    public Entry(String title, String password) {
        this.title = title;
        this.password = password;

        createdAt = LocalDateTime.now();
        lastModified = createdAt.plus(Duration.ofSeconds(1)); // We have to add a second, otherwise it looks like
        // Java has floating point problems, so the lastModified value can potential be lesser than createdAt.

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
        return DateFormatUtil.formatDate(createdAt);
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedString() {
        return DateFormatUtil.formatDate(lastModified);
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
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

    public ObservableList<Tag> tagsObservable() {
        return tags;
    }

    public boolean isExpired() {
        return validUntil != null && validUntil.isBefore(LocalDate.now().plusDays(1));
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

    @Override
    public String toString() {
        return "Entry{" +
                "title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", note='" + note + '\'' +
                ", url=" + url +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                ", validUntil=" + validUntil +
                ", securityQuestion=" + securityQuestion +
                ", tags=" + tags +
                '}';
    }
}
