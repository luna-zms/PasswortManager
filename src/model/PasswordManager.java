package model;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.List;

public class PasswordManager {

    private SecretKey masterPasswordKey;

    private LocalDateTime lastModified;

    private LocalDateTime validUntil;

    private Tag rootTag;

    private List<Entry> entries;

    public SecretKey getMasterPasswordKey() {
        return masterPasswordKey;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public Tag getRootTag() {
        return rootTag;
    }

    public void setMasterPasswordKey(SecretKey masterPasswordKey) {
        this.masterPasswordKey = masterPasswordKey;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public void setRootTag(Tag rootTag) {
        this.rootTag = rootTag;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
