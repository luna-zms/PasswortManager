package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.crypto.SecretKey;

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

    private List<Entry> entries;

    public PasswordManager(SecretKey masterPasswordKey) {
        this.masterPasswordKey = masterPasswordKey;

        entries = new ArrayList<>();

        lastModified = null;
        validUntil = null;
    }

    public void mergeWith(List<Entry> newEntries, Tag newRootTag){
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
        this.entries = entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordManager that = (PasswordManager) o;
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
