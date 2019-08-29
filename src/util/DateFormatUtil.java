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
        return formatDate(date, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN));
    }

    public static String formatDate(LocalDateTime dateTime) {
        return formatDate(dateTime,
                          DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                                           .withLocale(Locale.GERMAN));
    }

    public static String formatDate(LocalDate date, DateTimeFormatter format) {
        return date != null ? date.format(format) : "";
    }

    public static String formatDate(LocalDateTime dateTime, DateTimeFormatter format) {
        return dateTime != null ? dateTime.format(format) : "";
    }
}
