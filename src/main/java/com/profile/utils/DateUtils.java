package com.profile.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtils {
    public static String formatYearMonth(String dateStr) {
        if (dateStr == null || !dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "Unknown";
        }

        String[] parts = dateStr.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        LocalDate date = LocalDate.of(year, month, 1);
        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return monthName + " " + year;
    }

    public static String getLastYearMonth() {
        LocalDate now = LocalDate.now().minusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return now.format(formatter);
    }
}