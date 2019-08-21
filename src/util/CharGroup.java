package util;

public enum CharGroup {
    LOWER_CASE_LETTER("abcdefghijklmnopqrstuvwxyz".toCharArray(), 10), UPPER_CASE_LETTER(
	    "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(), 3), NUMBERS("0123456789".toCharArray(),
		    2), SPECIAL_CHARS("!\"§$%&/()=?{[]}\\+*~'#-_|<>^°".toCharArray(), 2);

    private char[] chars;

    private int safeDistinctCount;

    private CharGroup(char[] chars, int safeDistinctCount) {
	this.chars = chars;
	this.safeDistinctCount = safeDistinctCount;
    }

    public char[] getChars() {
	return chars;
    }

    public int getSafeDistinctCount() {
	return safeDistinctCount;
    }
}
