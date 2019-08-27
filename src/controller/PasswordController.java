package controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import util.CharGroup;
import util.PasswordGeneratorSettings;
import util.PasswordQualityUtil;

public class PasswordController {
    /**
     * Generate a password from a given set of generator settings,
     * using a total of {@code pgs.getLength() * pgs.getCharGroups().size()} random numbers
     * generated by an instance of {@link java.security.SecureRandom}.
     *
     * @param pgs Generator settings containing the length and the character groups to use
     *            when generating the password. Must not be null.
     * @return A string of the given length,
     * guaranteed to consist of only the characters from the given char groups.
     * @author sopr018
     */
    public String generatePassword(PasswordGeneratorSettings pgs) {
        StringBuilder builder = new StringBuilder();
        SecureRandom rng = new SecureRandom();
        List<CharGroup> charGroups = new ArrayList<>(pgs.getCharGroups());
        int countCharGroups = charGroups.size();

        for (int i = 0; i < pgs.getLength(); i++) {
            int charGroupIndex = rng.nextInt(countCharGroups);
            char[] chars = charGroups.get(charGroupIndex).getChars();
            int charIndex = rng.nextInt(chars.length);
            builder.append(chars[charIndex]);
        }

        return builder.toString();
    }
}
