package com.moon.mapper.convert;

import org.joda.time.ReadableInstant;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Joda 日期转换器：此转始终使用系统默认格式，且不对任何数据进行空指针判断
 *
 * @author benshaoye
 * @see Convert
 */
public abstract class JodaConvert {

    private JodaConvert() { }

    public static long toLongValue(ReadableInstant instant) {
        return instant.getMillis();
    }

    public static long toLongValue(org.joda.time.LocalDateTime instant) {
        return instant.toDate().getTime();
    }

    public static long toLongValue(org.joda.time.LocalDate instant) {
        return instant.toDate().getTime();
    }

    public static java.sql.Date toJavaSqlDate(ReadableInstant instant) {
        return new java.sql.Date(instant.getMillis());
    }

    public static java.sql.Date toJavaSqlDate(org.joda.time.LocalDateTime instant) {
        return Convert.toJavaSqlDate(instant.toDate());
    }

    public static java.sql.Date toJavaSqlDate(org.joda.time.LocalDate instant) {
        return Convert.toJavaSqlDate(instant.toDate());
    }

    public static Timestamp toTimestamp(ReadableInstant instant) { return new Timestamp(instant.getMillis()); }

    public static Timestamp toTimestamp(org.joda.time.LocalDateTime instant) {
        return Convert.toTimestamp(instant.toDate());
    }

    public static Timestamp toTimestamp(org.joda.time.LocalDate instant) {
        return Convert.toTimestamp(instant.toDate());
    }

    public static Time toTime(ReadableInstant instant) { return new Time(instant.getMillis()); }

    public static Time toTime(org.joda.time.LocalDateTime instant) {
        return Convert.toTime(instant.toDate());
    }

    public static Time toTime(org.joda.time.LocalDate instant) {
        return Convert.toTime(instant.toDate());
    }

    public static Date toDate(ReadableInstant instant) { return new Date(instant.getMillis()); }

    public static Date toDate(org.joda.time.LocalDateTime instant) { return instant.toDate(); }

    public static Date toDate(org.joda.time.LocalDate instant) { return instant.toDate(); }

    public static OffsetDateTime toOffsetDateTime(ReadableInstant instant) {
        return Convert.toOffsetDateTime(instant.getMillis());
    }

    public static OffsetDateTime toOffsetDateTime(org.joda.time.LocalDateTime instant) {
        return Convert.toOffsetDateTime(instant.toDate());
    }

    public static OffsetDateTime toOffsetDateTime(org.joda.time.LocalDate instant) {
        return Convert.toOffsetDateTime(instant.toDate());
    }

    public static ZonedDateTime toZonedDateTime(ReadableInstant instant) {
        return Convert.toZonedDateTime(instant.getMillis());
    }

    public static ZonedDateTime toZonedDateTime(org.joda.time.LocalDateTime instant) {
        return Convert.toZonedDateTime(instant.toDate());
    }

    public static ZonedDateTime toZonedDateTime(org.joda.time.LocalDate instant) {
        return Convert.toZonedDateTime(instant.toDate());
    }

    public static LocalDateTime toLocalDateTime(ReadableInstant instant) {
        return Convert.toLocalDateTime(instant.getMillis());
    }

    public static LocalDateTime toLocalDateTime(org.joda.time.LocalDateTime instant) {
        return Convert.toLocalDateTime(instant.toDate());
    }

    public static LocalDateTime toLocalDateTime(org.joda.time.LocalDate instant) {
        return Convert.toLocalDateTime(instant.toDate());
    }

    public static LocalDate toLocalDate(ReadableInstant instant) {
        return Convert.toLocalDate(instant.getMillis());
    }

    public static LocalDate toLocalDate(org.joda.time.LocalDateTime instant) {
        return Convert.toLocalDate(instant.toDate());
    }

    public static LocalDate toLocalDate(org.joda.time.LocalDate instant) {
        return Convert.toLocalDate(instant.toDate());
    }

    public static LocalTime toLocalTime(ReadableInstant instant) {
        return Convert.toLocalTime(instant.getMillis());
    }

    public static LocalTime toLocalTime(org.joda.time.LocalDateTime instant) {
        return Convert.toLocalTime(instant.toDate());
    }

    public static LocalTime toLocalTime(org.joda.time.LocalDate instant) {
        return Convert.toLocalTime(instant.toDate());
    }

    public static Calendar toCalendar(ReadableInstant instant) {
        return Convert.toCalendar(instant.getMillis());
    }

    public static Calendar toCalendar(org.joda.time.LocalDateTime instant) {
        return Convert.toCalendar(instant.toDate());
    }

    public static Calendar toCalendar(org.joda.time.LocalDate instant) {
        return Convert.toCalendar(instant.toDate());
    }
}
