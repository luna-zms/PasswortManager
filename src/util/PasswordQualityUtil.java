package util;

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
        int score = 0;

        score += lengthScore(pwd.length());
        score += partitionScore(pwd);
        score += charGroupsExistScore(pwd);
        // score -= dictionaryMalusScore(pwd);

        return score;
    }

    /**
     * Get the normalized score from 0-1 of a password
     *
     * @param pwd The password to check the quality of
     * @return The normalized score (0-1)
     */
    public static double getNormalizedScore(String pwd) {
        return (double)getScore(pwd) / 20.0;
    }

    /**
     * Returns the internal score a password gets for its length (0-5)
     *
     * @param pwdLength The length of a password
     * @return The internal score (0-5)
     */
    private static int lengthScore(int pwdLength) {
        pwdLength = Math.min(25, pwdLength);

        return pwdLength/5;
    }

    /**
     * Returns the internal score for the partitioning of a password into character groups (0-10)
     *
     * @param pwd The password to get the partitioning score for
     * @return The internal score (0-10)
     */
    private static int partitionScore(String pwd) {
        int partitions = 1;
        char[] pwdCharArray = pwd.toCharArray();
        char lastChar = pwdCharArray[0];

        for (int i = 1; i < pwdCharArray.length; i++) {
            char currentChar = pwdCharArray[i];

            if (CharGroup.getCharGroupOf(currentChar) != CharGroup.getCharGroupOf(lastChar)) {
                partitions += 1;
                lastChar = currentChar;
            }
        }

        return (int) (((double)partitions / (double)pwd.length()) * 10.0); // Normalize partition count
    }

    /**
     * Returns the internal score for the existence of char groups in a password (0-5)
     *
     * @param pwd
     * @return
     */
    private static int charGroupsExistScore(String pwd) {
        int score = 0;

        for (CharGroup charGroup : CharGroup.values()) {
            for (char c : charGroup.getChars()) {
                if (pwd.indexOf(c) != -1) {
                    if (charGroup == CharGroup.SPECIAL_CHARS) score += 2;
                    else if (charGroup != CharGroup.OTHER) score += 1;

                    break;
                }
            }
        }

        return score;
    }
}
