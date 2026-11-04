package com.bankz.common;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Conversions between COBOL PIC fields and Java values. */
public final class LegacyFormat {

    private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HHMMSS = DateTimeFormatter.ofPattern("HHmmss");

    private LegacyFormat() {
    }

    /** PIC 9(n): zero-padded number. */
    public static String zeroPad(long value, int length) {
        return String.format("%0" + length + "d", value);
    }

    /** PIC X(n): right-padded with blanks, truncated to n. */
    public static String pad(String value, int length) {
        String v = value == null ? "" : value;
        return v.length() >= length ? v.substring(0, length) : v + " ".repeat(length - v.length());
    }

    /** DB2 CHAR values come back blank-padded. */
    public static String trim(String value) {
        return value == null ? "" : value.stripTrailing();
    }

    public static boolean isBlank(String value) {
        return value == null || value.isEmpty() || value.charAt(0) == ' ';
    }

    public static int yyyymmdd(LocalDate date) {
        return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }

    public static LocalDate fromYyyymmdd(int value) {
        return LocalDate.of(value / 10000, (value / 100) % 100, value % 100);
    }

    public static String ddmmyyyy(LocalDate date) {
        return date == null ? "00000000" : date.format(DDMMYYYY);
    }

    public static String ddSlashMmSlashYyyy(int yyyymmdd) {
        return String.format("%02d/%02d/%04d", yyyymmdd % 100, (yyyymmdd / 100) % 100, yyyymmdd / 10000);
    }

    public static String ddSlashMmSlashYyyy(LocalDate date) {
        return date.format(DD_MM_YYYY);
    }

    public static String hhmmss(LocalTime time) {
        return time.format(HHMMSS);
    }

    /** "first last" built the COBOL way: STRING ... DELIMITED BY '  '. */
    public static String fullName(String first, String last) {
        return trim(first) + " " + trim(last);
    }
}
