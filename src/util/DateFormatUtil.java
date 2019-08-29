package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateFormatUtil {
    private DateFormatUtil() {
    }

    public static String formatDate(LocalDate date) {
        return date != null ?
               date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN)) :
               "";
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ?
               dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(Locale.GERMAN)) :
               "";
    }
}
