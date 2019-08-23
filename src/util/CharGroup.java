package util;

public enum CharGroup {
    LOWER_CASE_LETTER("abcdefghijklmnopqrstuvwxyz".toCharArray()),
    UPPER_CASE_LETTER("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()),
    NUMBERS("0123456789".toCharArray()),
    SPECIAL_CHARS("!\"§$%&/()=?{[]}\\+*~'#-_|<>^°".toCharArray()),
    OTHER(new char[0]);

    private char[] chars;

    CharGroup(char[] chars) {
        this.chars = chars;
    }

    public char[] getChars() { return chars; }

    public static CharGroup getCharGroupOf(char c) {
        for (CharGroup charGroup : CharGroup.values()) {
            if (charGroup.getChars().toString().indexOf(c) != -1) return charGroup;
        }

        return OTHER;
    }
}
