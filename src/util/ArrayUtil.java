package util;

public class ArrayUtil {
    public static <T>boolean contains(char[] arr, char c1) {
        for (char c2 : arr) {
            if (c1 == c2) return true;
        }

        return false;
    }
}
