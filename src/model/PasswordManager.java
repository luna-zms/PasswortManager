package model;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.crypto.SecretKey;

public class PasswordManager {

	private SecretKey masterPasswordKey;

	private LocalDateTime lastModified;

	private LocalDateTime validUntil;

	private Tag tag;

	private Collection<Entry> entry;

}
