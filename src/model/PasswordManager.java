package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    private LocalDateTime lastModified = LocalDateTime.now();
    private LocalDateTime validUntil = null;

    private Tag rootTag;

    private ObservableList<Entry> entries;

    public PasswordManager() {
        this(null);
    }

    /**
     * constructor sets the minimal required attributes masterPasswordKey
     * and lastModified or validUntil
     * @param masterPasswordKey
     */
    public PasswordManager(SecretKey masterPasswordKey) {
        this.masterPasswordKey = masterPasswordKey;
        entries = FXCollections.observableArrayList();
    }
    /**
     * this method will merge this tag newrootTag with the list newEntries
     * @param newEntries
     * @param newRootTag
     */
    public void mergeWith(List<Entry> newEntries, Tag newRootTag) {
        entries.addAll(newEntries);
        Map<Tag, Tag> unify = rootTag.mergeWith(newRootTag);
        unify.put(newRootTag, rootTag);

        for (Entry entry : entries) {
        	ListIterator<Tag> iterator = entry.getTags().listIterator();
        	while (iterator.hasNext()) {
        		Tag tag = iterator.next();
        		iterator.set(unify.getOrDefault(tag, tag));
        	}
        }
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
