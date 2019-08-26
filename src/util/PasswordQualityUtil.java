package util;

import javax.sound.midi.SysexMessage;

/**
 * Utils for checking the quality of a password
 *
 * @author sopr011, sopr012
 */
public class PasswordQualityUtil {
    /**
     * <p>Get the internal score from 0-20 of a password by adding all partial scores together.</p>
     * <p>Partial scores exist for the following criteria:
     * <ul>
     *     <li>The length of a password</li>
     *     <li>The partitioning into different character groups of a password</li>
     *     <li>The amount of different character groups in a password</li>
     *     <li>The existence of commonly used words from a dictionary in a password</li>
     * </ul></p>
     *
     * @param pwd The password to check the quality of
     * @return The internal score (0-20)
     */
    public static int getScore(String pwd) {
        if (pwd.length() == 0) return 0;

        int score = 0;

        System.out.println("Password: " + pwd);
        score += lengthScore(pwd.length());         System.out.println("LengthScore: " + lengthScore(pwd.length()));
        score += charGroupsExistScore(pwd);         System.out.println("CharGroupsExistScore: " + charGroupsExistScore(pwd));
        score += repeatSameCharScore(pwd);          System.out.println("RepeatSameCharScore: " + repeatSameCharScore(pwd));
        score += consecutiveCharGroupScore(pwd);    System.out.println("ConsecutiveCharGroupScore: " + consecutiveCharGroupScore(pwd));
        score += sequentialCharsScore(pwd);         System.out.println("SequentialCharsScore: " + sequentialCharsScore(pwd));
        //score += nearOnKeyboardScore(pwd);
        //score += dictionaryExistenceScore(pwd);

        System.out.println("Score: " + (score <= 200 ? score : 200));
        System.out.println("=====");
        return score <= 200 ? score : 200;
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
     * Returns the internal score a password gets for its length (0-5)
     *
     * @param pwdLength The length of a password
     * @return The internal score (0-5)
     */
    private static int lengthScore(int pwdLength) {
        return pwdLength*4;
    }

    /**
     * Returns the internal score for the existence of char groups in a password (0-5)
     *
     * @param pwd
     * @return
     */
    private static int charGroupsExistScore(String pwd) {
        int score = 0;

        for (char c : pwd.toCharArray()) {
            score += CharGroup.getCharGroupOf(c).getBonusFactor();
        }

        return score;
    }

    private static int repeatSameCharScore(String pwd) {
        double score = 0.0;
        Character lastChar = null;
        int repeatCounter = 1;

        for (char c : pwd.toCharArray()) {
            if (lastChar != null && c == lastChar) {
                repeatCounter += 1;
            } else {
                if (repeatCounter >= 2) {
                    score += Math.pow((double)repeatCounter, 1.2);
                }
                repeatCounter = 1;
            }

            lastChar = c;
        }

        if (repeatCounter >= 2) score += Math.pow((double)repeatCounter, 1.2);

        return -(int)score;
    }

    private static int consecutiveCharGroupScore(String pwd) {
        int score = 0;
        CharGroup lastCharGroup = null;
        int sameCharGroupCounter = 1;

        for (char c : pwd.toCharArray()) {
            if (CharGroup.getCharGroupOf(c) == lastCharGroup) {
                sameCharGroupCounter += 1;
            } else {
                if (sameCharGroupCounter >= 2) score += sameCharGroupCounter*2;
                lastCharGroup = CharGroup.getCharGroupOf(c);
                sameCharGroupCounter = 1;
            }
        }

        if (sameCharGroupCounter >= 2) score += sameCharGroupCounter*2;

        return -score;
    }

    private static int sequentialCharsScore(String pwd) {
        int score = 0;
        Character lastChar = null;
        int sequenceCounter = 1;

        for (char c : pwd.toCharArray()) {
            if (lastChar != null &&
                    CharGroup.getCharGroupOf(c) == CharGroup.getCharGroupOf(lastChar) &&
                    CharGroup.getIndexOfCharInGroup(c) == CharGroup.getIndexOfCharInGroup(lastChar)-1) {
                sequenceCounter += 1;
            } else {
                if (sequenceCounter >= 3) score += sequenceCounter*3;
                sequenceCounter = 1;
            }

            lastChar = c;
        }

        if (sequenceCounter >= 3) score += sequenceCounter*3;

        return -score;
    }
}
