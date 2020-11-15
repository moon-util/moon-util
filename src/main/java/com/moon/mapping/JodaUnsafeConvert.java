package com.moon.mapping;

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
 * @see UnsafeConvert
 */
public abstract class JodaUnsafeConvert {

    private JodaUnsafeConvert() { }

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
        return UnsafeConvert.toJavaSqlDate(instant.toDate());
    }

    public static java.sql.Date toJavaSqlDate(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toJavaSqlDate(instant.toDate());
    }

    public static Timestamp toTimestamp(ReadableInstant instant) { return new Timestamp(instant.getMillis()); }

    public static Timestamp toTimestamp(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toTimestamp(instant.toDate());
    }

    public static Timestamp toTimestamp(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toTimestamp(instant.toDate());
    }

    public static Time toTime(ReadableInstant instant) { return new Time(instant.getMillis()); }

    public static Time toTime(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toTime(instant.toDate());
    }

    public static Time toTime(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toTime(instant.toDate());
    }

    public static Date toDate(ReadableInstant instant) { return new Date(instant.getMillis()); }

    public static Date toDate(org.joda.time.LocalDateTime instant) { return instant.toDate(); }

    public static Date toDate(org.joda.time.LocalDate instant) { return instant.toDate(); }

    public static OffsetDateTime toOffsetDateTime(ReadableInstant instant) {
        return UnsafeConvert.toOffsetDateTime(instant.getMillis());
    }

    public static OffsetDateTime toOffsetDateTime(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toOffsetDateTime(instant.toDate());
    }

    public static OffsetDateTime toOffsetDateTime(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toOffsetDateTime(instant.toDate());
    }

    public static ZonedDateTime toZonedDateTime(ReadableInstant instant) {
        return UnsafeConvert.toZonedDateTime(instant.getMillis());
    }

    public static ZonedDateTime toZonedDateTime(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toZonedDateTime(instant.toDate());
    }

    public static ZonedDateTime toZonedDateTime(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toZonedDateTime(instant.toDate());
    }

    public static LocalDateTime toLocalDateTime(ReadableInstant instant) {
        return UnsafeConvert.toLocalDateTime(instant.getMillis());
    }

    public static LocalDateTime toLocalDateTime(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toLocalDateTime(instant.toDate());
    }

    public static LocalDateTime toLocalDateTime(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toLocalDateTime(instant.toDate());
    }

    public static LocalDate toLocalDate(ReadableInstant instant) {
        return UnsafeConvert.toLocalDate(instant.getMillis());
    }

    public static LocalDate toLocalDate(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toLocalDate(instant.toDate());
    }

    public static LocalDate toLocalDate(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toLocalDate(instant.toDate());
    }

    public static LocalTime toLocalTime(ReadableInstant instant) {
        return UnsafeConvert.toLocalTime(instant.getMillis());
    }

    public static LocalTime toLocalTime(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toLocalTime(instant.toDate());
    }

    public static LocalTime toLocalTime(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toLocalTime(instant.toDate());
    }

    public static Calendar toCalendar(ReadableInstant instant) {
        return UnsafeConvert.toCalendar(instant.getMillis());
    }

    public static Calendar toCalendar(org.joda.time.LocalDateTime instant) {
        return UnsafeConvert.toCalendar(instant.toDate());
    }

    public static Calendar toCalendar(org.joda.time.LocalDate instant) {
        return UnsafeConvert.toCalendar(instant.toDate());
    }
}
