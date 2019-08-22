package util;

/**
 * Utils for checking the quality of a password
 *
 * @author Daniel Ginter
 * @author Alexander Korn
 */
public class PasswordQualityUtil {
    /**
     * Get the internal score from 0-20 of a password
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
        return (double)getScore(pwd)/20.0;
    }

    private static int lengthScore(int pwdLength) {
        pwdLength = Math.min(25, pwdLength);

        return pwdLength/5;
    }

    private static int partitionScore(String pwd) {
        int partitions = 1;
        char[] pwdCharArray = pwd.toCharArray();
        char lastChar = pwdCharArray[0];

        for (int i = 1; i < pwdCharArray.length; i++) {
            char currentChar = pwdCharArray[i];

            if (getGroupOfChar(currentChar) != getGroupOfChar(lastChar)) {
                partitions += 1;
                lastChar = currentChar;
            }
        }

        return (int) (((double) partitions / (double) pwd.length()) * 10.0); // Normalize partition count
    }

    private static int charGroupsExistScore(String pwd) {
        int score = 0;

        for (CharGroup charGroup : CharGroup.values()) {
            for (char c : charGroup.getChars()) {
                if (pwd.indexOf(c) != -1) {
                    if (charGroup == CharGroup.SPECIAL_CHARS) score += 2;
                    else score += 1;

                    break;
                }
            }
        }

        return score;
    }

    private static CharGroup getGroupOfChar(char c) {
        for (CharGroup charGroup : CharGroup.values()) {
            if (contains(charGroup.getChars(), c)) return charGroup;
        }

        throw new IllegalArgumentException("Char not in any group: " + c);
    }

    private static boolean contains(char[] chars, char cc) {
        for (char c : chars) {
            if (c == cc) return true;
        }

        return false;
    }
}
