package util;

public enum CharGroup {
    LOWER_CASE_LETTER("abcdefghijklmnopqrstuvwxyz".toCharArray(), 2),
    UPPER_CASE_LETTER("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(), 2),
    NUMBERS("0123456789".toCharArray(), 4),
    SPECIAL_CHARS("!\"§$%&/()=?{[]}\\+*~'#-_|<>^°".toCharArray(), 6),
    OTHER(new char[0], 6);

    private char[] chars;
    private int bonusFactor;

    CharGroup(char[] chars, int bonusFactor) {
        this.chars = chars;
        this.bonusFactor = bonusFactor;
    }

    public char[] getChars() { return chars; }
    public int getBonusFactor() { return bonusFactor; }

    public static CharGroup getCharGroupOf(char cIsTooShort) {
        for (CharGroup charGroup : CharGroup.values()) {
            if (new String(charGroup.getChars()).indexOf(cIsTooShort) != -1) return charGroup;
        }

        return OTHER;
    }

    public static int getIndexOfCharInGroup(char cIsTooShort) {
        return new String(getCharGroupOf(cIsTooShort).getChars()).indexOf(cIsTooShort);
    }
}
