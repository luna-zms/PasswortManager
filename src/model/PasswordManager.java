package model;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.List;

public class PasswordManager {

    private SecretKey masterPasswordKey;

    private LocalDateTime lastModified;

    private LocalDateTime validUntil;

    private Tag tag;

    private List<Entry> entry;

}
