package util;

import factory.WindowFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Utils for checking the quality of a password
 *
 * @author sopr011, sopr012
 */
public class PasswordQualityUtil {
    public static final int MAX_SCORE = 200;
    public static final int TWO = 2;
    public static final int THREE = 3;
    private static PasswordQualityUtil passwordQualityUtil;

    private ArrayList<String> dict;

    /**
     * Loads the dictionary file when initialized by getInstance() as a singleton
     */
    private PasswordQualityUtil() {
        boolean dictLoaded = true;
        InputStream dictFile = PasswordQualityUtil.class.getResourceAsStream("resources/dict.txt");

        if (dictFile == null) dictLoaded = false;

        dict = new ArrayList<String>();

        if (dictLoaded) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(dictFile))) {
                while (reader.ready()) dict.add(reader.readLine());
            } catch (IOException ioe) {
                dictLoaded = false;
            }
        }

        if (!dictLoaded) WindowFactory.showError("Dateifehler", "Konnte Wörterbuch für Passwortqualitätsüberprüfung nicht laden.");
    }

    /**
     * Get a singleton instance of PasswordQualityUtil
     *
     * @return PasswordQualityUtil Singleton instance
     */
    public static PasswordQualityUtil getInstance() {
        if (PasswordQualityUtil.passwordQualityUtil == null) {
            PasswordQualityUtil.passwordQualityUtil = new PasswordQualityUtil();
        }

        return PasswordQualityUtil.passwordQualityUtil;
    }

    public ArrayList<String> getDict() { return dict; }

    /**
     * <p>Get the internal score from 0-20 of a password by adding all partial scores together.</p>
     * <p>Partial scores exist for the following criteria:
     * <ul>
     *     <li>The length of a password</li>
     *     <li>The amount of different character groups in a password</li>
     *     <li>If characters are repeated multiple times in a row</li>
     *     <li>If char groups are repeated multiple times in a row</li>
     *     <li>If sequences of characters (abc, 123, etc.) exist</li>
     *     <li>The existence of commonly used words from a dictionary in a password</li>
     * </ul></p>
     *
     * @param pwd The password to check the quality of
     * @return The internal score (0-20)
     */
    public static int getScore(String pwd) {
        if (pwd.length() == 0) return 0;

        int score = 0;

        score += lengthScore(pwd.length());
        score += charGroupsExistScore(pwd);
        score += repeatSameCharScore(pwd);
        score += consecutiveCharGroupScore(pwd);
        score += sequentialCharsScore(pwd);
        //score += nearOnKeyboardScore(pwd);
        score += dictionaryExistenceScore(pwd);

        // The score cannot be less than 0 or more than 200
        if (score < 0) return 0;
        if (score > MAX_SCORE) return MAX_SCORE;
        else return score;
    }

    /**
     * Get the normalized score from 0-1 of a password
     *
     * @param pwd The password to check the quality of
     * @return The normalized score (0-1)
     */
    public static double getNormalizedScore(String pwd) {
        return (double)getScore(pwd) / 200.0;
    }

    /**
     * <br>Returns the internal score a password gets for its length n:
     * <br>n*4
     *
     * @param pwdLength The length of a password
     * @return The internal score
     */
    private static int lengthScore(int pwdLength) {
        return pwdLength*4;
    }

    /**
     * <br>Returns the internal score for the existence of char groups in a password:
     * <br>n*bonusFactor
     *
     * @param pwd The password to check the quality of
     * @return The internal score
     */
    private static int charGroupsExistScore(String pwd) {
        int score = 0;

        for (char c : pwd.toCharArray()) {
            score += CharGroup.getCharGroupOf(c).getBonusFactor();
        }

        return score;
    }

    /**
     * <br>Returns the internal score for the repetition of the same char multiple times in a row:
     * <br>n*repeated^1.2
     *
     * @param pwd The password to check the quality of
     * @return The internal score
     */
    private static int repeatSameCharScore(String pwd) {
        double score = 0.0;
        Character lastChar = null;
        int repeatCounter = 1;

        for (char iChar : pwd.toCharArray()) {
            if (lastChar != null && iChar == lastChar) {
                repeatCounter += 1;
            } else {
                if (repeatCounter >= TWO) {
                    score += CharGroup.getCharGroupOf(lastChar).getBonusFactor()*(repeatCounter-1);
                }
                repeatCounter = 1;
            }

            lastChar = iChar;
        }

        if (repeatCounter >= TWO) score += CharGroup.getCharGroupOf(lastChar).getBonusFactor()*(repeatCounter-1);

        return -(int)score;
    }

    /**
     * <br>Returns the internal score for the existence of consecutive char groups:
     * <br>n*2
     *
     * @param pwd The password to check the quality of
     * @return The internal score
     */
    private static int consecutiveCharGroupScore(String pwd) {
        int score = 0;
        CharGroup lastCharGroup = null;
        int sameCharGroupCounter = 1;

        for (char iChar : pwd.toCharArray()) {
            if (CharGroup.getCharGroupOf(iChar) == lastCharGroup) {
                sameCharGroupCounter += 1;
            } else {
                if (sameCharGroupCounter >= TWO) score += sameCharGroupCounter*2;
                lastCharGroup = CharGroup.getCharGroupOf(iChar);
                sameCharGroupCounter = 1;
            }
        }

        if (sameCharGroupCounter >= TWO) score += sameCharGroupCounter*2;

        return -score;
    }

    /**
     * <br>Returns the internal score for the existence of sequences of chars:
     * <br>n*3
     *
     * @param pwd The password to check the quality of
     * @return The internal score
     */
    private static int sequentialCharsScore(String pwd) {
        int score = 0;
        Character lastChar = null;
        int sequenceCounter = 1;

        for (char iChar : pwd.toCharArray()) {
            if (lastChar != null &&
                    CharGroup.getCharGroupOf(iChar) == CharGroup.getCharGroupOf(lastChar) &&
                    CharGroup.getIndexOfCharInGroup(iChar) == CharGroup.getIndexOfCharInGroup(lastChar)+1) {
                sequenceCounter += 1;
            } else {
                if (sequenceCounter >= THREE) score += sequenceCounter*3;
                sequenceCounter = 1;
            }

            lastChar = iChar;
        }

        if (sequenceCounter >= THREE) score += sequenceCounter*3;

        return -score;
    }

    /**
     * <br>Returns the internal score for the existence of words from a dictionary of commonly used words:
     * <br>n*4
     *
     * @param pwd The password to check the quality of
     * @return The internal score
     */
    private static int dictionaryExistenceScore(String pwd) {
        for (String word : getInstance().getDict()) {
            // Only pwd needs to be lower case because all words in dictionary are lower case already
            if (pwd.toLowerCase().contains(word)) return -word.length()*4;
        }

        return 0;
    }
}
