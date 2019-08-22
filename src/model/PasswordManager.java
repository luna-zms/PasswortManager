package model;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

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

    public PasswordManager() {
        entries = new ArrayList<>();
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
}
