package util;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipboardUtil {
    private ClipboardUtil() {
    }

    public static void copyToClipboard(String str) {
        ClipboardContent content = new ClipboardContent();
        content.putString(str);

        Clipboard.getSystemClipboard().setContent(content);
    }

    public static <T> void copyToClipboard(ObservableValue<T> observableValue, Function<T, String> getter) {
        T value = observableValue.getValue();
        if (value != null) copyToClipboard(getter.apply(value));
    }
}
