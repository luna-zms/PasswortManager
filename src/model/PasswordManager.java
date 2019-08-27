package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.crypto.SecretKey;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class is used to save the database dependent application data, e.g. the hashed master password.
 *
 * @author sopr016
 */
public class PasswordManager {
    private SecretKey masterPasswordKey;

    private LocalDateTime lastModified;
    private LocalDateTime validUntil;

    private Tag rootTag;

    private ObservableList<Entry> entries;

    public PasswordManager() {
        this(null);
    }

    public PasswordManager(SecretKey masterPasswordKey) {
        this.masterPasswordKey = masterPasswordKey;
        entries = FXCollections.observableArrayList();

        lastModified = null;
        validUntil = null;
    }

    public void mergeWith(List<Entry> newEntries, Tag newRootTag) {
        entries.addAll(newEntries);
        rootTag.mergeWith(newRootTag);
    }

    public SecretKey getMasterPasswordKey() {
        return masterPasswordKey;
    }

    public void setMasterPasswordKey(SecretKey masterPasswordKey) {
        this.masterPasswordKey = masterPasswordKey;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Tag getRootTag() {
        return rootTag;
    }

    public void setRootTag(Tag tag) {
        this.rootTag = tag;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = FXCollections.observableList(entries);
    }

    public ObservableList<Entry> entriesObservable() {
        return entries;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PasswordManager that = (PasswordManager) obj;
        return masterPasswordKey.equals(that.masterPasswordKey) &&
                Objects.equals(lastModified, that.lastModified) &&
                Objects.equals(validUntil, that.validUntil) &&
                Objects.equals(rootTag, that.rootTag) &&
                entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(masterPasswordKey, lastModified, validUntil, rootTag, entries);
    }
}
