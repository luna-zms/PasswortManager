package util;

public enum CharGroup {
    LOWER_CASE_LETTER("abcdefghijklmnopqrstuvwxyz".toCharArray(), 10),
    UPPER_CASE_LETTER("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(), 3),
    NUMBERS("0123456789".toCharArray(), 2),
    SPECIAL_CHARS("!\"§$%&/()=?{[]}\\+*~'#-_|<>^°".toCharArray(), 2),
    OTHER(new char[0], 0);

    private char[] chars;
    private int safeDistinctCount;

    CharGroup(char[] chars, int safeDistinctCount) {
        this.chars = chars;
        this.safeDistinctCount = safeDistinctCount;
    }

    public char[] getChars() { return chars; }
    public int getSafeDistinctCount() { return safeDistinctCount; }

    public static CharGroup getCharGroupOf(char c) {
        for (CharGroup charGroup : CharGroup.values()) {
            if (charGroup.getChars().toString().indexOf(c) != -1) return charGroup;
        }

        return OTHER;
    }
}
