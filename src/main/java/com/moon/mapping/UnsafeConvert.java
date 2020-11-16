package com.moon.mapping;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

import static java.time.ZoneId.systemDefault;

/**
 * 数据转换器：此转始终使用系统默认格式，且不对任何数据进行空指针判断
 *
 * @author benshaoye
 * @see JodaUnsafeConvert
 */
public abstract class UnsafeConvert {

    private UnsafeConvert() { }

    public static Byte toByte(Enum<?> enumVal) { return (byte) enumVal.ordinal(); }

    public static Short toShort(Enum<?> enumVal) { return (short) enumVal.ordinal(); }

    public static Integer toInteger(Enum<?> enumVal) { return enumVal.ordinal(); }

    public static Long toLong(Enum<?> enumVal) { return (long) enumVal.ordinal(); }

    public static Float toFloat(Enum<?> enumVal) { return (float) enumVal.ordinal(); }

    public static Double toDouble(Enum<?> enumVal) { return (double) enumVal.ordinal(); }

    public static BigDecimal toBigDecimal(Double value) { return BigDecimal.valueOf(value); }

    public static BigDecimal toBigDecimal(Float value) { return BigDecimal.valueOf(value); }

    public static BigDecimal toBigDecimal(Number number) {
        if (number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue());
        } else {
            return BigDecimal.valueOf(number.longValue());
        }
    }

    public static BigDecimal toBigDecimal(String number) { return new BigDecimal(number); }

    public static BigDecimal toBigDecimal(String number, String pattern) {
        return toBigDecimal(toNumber(number, pattern));
    }

    public static BigDecimal toBigDecimal(BigInteger number) { return new BigDecimal(number); }

    public static BigInteger toBigInteger(Number number) { return BigInteger.valueOf(number.longValue()); }

    public static BigInteger toBigInteger(String number) { return new BigInteger(number); }

    public static BigInteger toBigInteger(String number, String pattern) {
        return toBigInteger(toNumber(number, pattern));
    }

    public static long toLongValue(Instant instant) { return instant.toEpochMilli(); }

    public static long toLongValue(OffsetDateTime date) { return toLongValue(date.toInstant()); }

    public static long toLongValue(ZonedDateTime date) { return toLongValue(date.toInstant()); }

    public static long toLongValue(LocalDateTime date) { return toLongValue(date.atZone(systemDefault())); }

    public static long toLongValue(LocalDate date) { return toLongValue(date.atStartOfDay()); }

    public static long toLongValue(Calendar calendar) { return calendar.getTimeInMillis(); }

    public static long toLongValue(Date calendar) { return calendar.getTime(); }

    public static double toDoubleValue(Instant instant) { return instant.toEpochMilli(); }

    public static double toDoubleValue(OffsetDateTime date) { return toDoubleValue(date.toInstant()); }

    public static double toDoubleValue(ZonedDateTime date) { return toDoubleValue(date.toInstant()); }

    public static double toDoubleValue(LocalDateTime date) { return toDoubleValue(date.atZone(systemDefault())); }

    public static double toDoubleValue(LocalDate date) { return toDoubleValue(date.atStartOfDay()); }

    public static double toDoubleValue(Calendar calendar) { return calendar.getTimeInMillis(); }

    public static double toDoubleValue(Date calendar) { return calendar.getTime(); }

    public static int toIntValue(Enum<?> enumValue) { return enumValue.ordinal(); }

    public static short toShortValue(Enum<?> enumValue) { return (short) enumValue.ordinal(); }

    public static byte toByteValue(Enum<?> enumValue) { return (byte) enumValue.ordinal(); }

    public static Calendar toCalendar(LocalDate date) { return toCalendar(date.atStartOfDay()); }

    public static Calendar toCalendar(LocalDateTime date) { return toCalendar(date.atZone(systemDefault())); }

    public static Calendar toCalendar(ZonedDateTime date) { return toCalendar(date.toInstant()); }

    public static Calendar toCalendar(OffsetDateTime date) { return toCalendar(date.toInstant()); }

    public static Calendar toCalendar(Instant instant) { return toCalendar(instant.toEpochMilli()); }

    public static Calendar toCalendar(Number number) { return toCalendar(number.longValue()); }

    public static Calendar toCalendar(double instant) { return toCalendar((long) instant); }

    public static Calendar toCalendar(String instant) { return toCalendar(toDate(instant)); }

    public static Calendar toCalendar(String instant, String pattern) { return toCalendar(toDate(instant, pattern)); }

    public static Calendar toCalendar(long instant) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(instant);
        return calendar;
    }

    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Timestamp toTimestamp(Calendar date) { return toTimestamp(date.getTimeInMillis()); }

    public static Timestamp toTimestamp(LocalDate date) { return toTimestamp(date.atStartOfDay()); }

    public static Timestamp toTimestamp(LocalDateTime date) { return toTimestamp(date.atZone(systemDefault())); }

    public static Timestamp toTimestamp(ZonedDateTime date) { return toTimestamp(date.toInstant()); }

    public static Timestamp toTimestamp(long instant) { return new Timestamp(instant); }

    public static Timestamp toTimestamp(double instant) { return new Timestamp((long) instant); }

    public static Timestamp toTimestamp(Date instant) { return new Timestamp(instant.getTime()); }

    public static Timestamp toTimestamp(String instant) { return new Timestamp(toDate(instant).getTime()); }

    public static Timestamp toTimestamp(String instant, String pattern) {
        return new Timestamp(toDate(instant, pattern).getTime());
    }

    public static Timestamp toTimestamp(Number number) { return new Timestamp(number.longValue()); }

    public static Timestamp toTimestamp(Instant instant) { return new Timestamp(toLongValue(instant)); }

    public static Timestamp toTimestamp(OffsetDateTime date) { return toTimestamp(date.toInstant()); }

    public static java.sql.Date toJavaSqlDate(Calendar date) { return toJavaSqlDate(date.getTimeInMillis()); }

    public static java.sql.Date toJavaSqlDate(LocalDate date) { return toJavaSqlDate(date.atStartOfDay()); }

    public static java.sql.Date toJavaSqlDate(LocalDateTime date) {
        return toJavaSqlDate(date.atZone(systemDefault()));
    }

    public static java.sql.Date toJavaSqlDate(ZonedDateTime date) { return toJavaSqlDate(date.toInstant()); }

    public static java.sql.Date toJavaSqlDate(OffsetDateTime date) { return toJavaSqlDate(date.toInstant()); }

    public static java.sql.Date toJavaSqlDate(Instant instant) { return new java.sql.Date(toLongValue(instant)); }

    public static java.sql.Date toJavaSqlDate(Date instant) { return new java.sql.Date(instant.getTime()); }

    public static java.sql.Date toJavaSqlDate(long instant) { return new java.sql.Date(instant); }

    public static java.sql.Date toJavaSqlDate(double instant) { return new java.sql.Date((long) instant); }

    public static java.sql.Date toJavaSqlDate(String instant) { return new java.sql.Date(toDate(instant).getTime()); }

    public static java.sql.Date toJavaSqlDate(String instant, String pattern) {
        return new java.sql.Date(toDate(instant, pattern).getTime());
    }

    public static java.sql.Date toJavaSqlDate(Number number) { return new java.sql.Date(number.longValue()); }

    public static Time toTime(Calendar date) { return toTime(date.getTimeInMillis()); }

    public static Time toTime(LocalDate date) { return toTime(date.atStartOfDay()); }

    public static Time toTime(LocalDateTime date) { return toTime(date.atZone(systemDefault())); }

    public static Time toTime(ZonedDateTime date) { return toTime(date.toInstant()); }

    public static Time toTime(OffsetDateTime date) { return toTime(date.toInstant()); }

    public static Time toTime(Instant instant) { return new Time(toLongValue(instant)); }

    public static Time toTime(Number number) { return new Time(number.longValue()); }

    public static Time toTime(Date number) { return new Time(number.getTime()); }

    public static Time toTime(long instant) { return new Time(instant); }

    public static Time toTime(double instant) { return new Time((long) instant); }

    public static Time toTime(String instant) { return new Time(toDate(instant).getTime()); }

    public static Time toTime(String instant, String pattern) { return new Time(toDate(instant, pattern).getTime()); }

    public static Date toDate(Calendar date) { return date.getTime(); }

    public static Date toDate(LocalDate date) { return toDate(date.atStartOfDay()); }

    public static Date toDate(LocalDateTime date) { return toDate(date.atZone(systemDefault())); }

    public static Date toDate(ZonedDateTime date) { return toDate(date.toInstant()); }

    public static Date toDate(OffsetDateTime date) { return toDate(date.toInstant()); }

    public static Date toDate(Instant instant) { return new Date(toLongValue(instant)); }

    public static Date toDate(Number number) { return new Date(number.longValue()); }

    public static Date toDate(String instant, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(instant);
        } catch (ParseException e) {
            throw new IllegalStateException("日期格式错误: " + instant + " -> " + pattern, e);
        }
    }

    public static Date toDate(String instant) {
        try {
            return new SimpleDateFormat().parse(instant);
        } catch (ParseException e) {
            throw new IllegalStateException("日期格式错误: " + instant, e);
        }
    }

    public static Date toDate(long instant) { return new Date(instant); }

    public static Date toDate(double instant) { return new Date((long) instant); }

    public static LocalDateTime toLocalDateTime(double instant) { return toLocalDateTime(toInstant(instant)); }

    public static LocalDateTime toLocalDateTime(long instant) { return toLocalDateTime(toInstant(instant)); }

    public static LocalDateTime toLocalDateTime(Date date) { return toLocalDateTime(date.getTime()); }

    public static LocalDateTime toLocalDateTime(Number instant) { return toLocalDateTime(instant.longValue()); }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, systemDefault());
    }

    public static LocalDateTime toLocalDateTime(Calendar calendar) { return toLocalDateTime(calendar.toInstant()); }

    public static LocalDateTime toLocalDateTime(ZonedDateTime time) { return toLocalDateTime(time.toInstant()); }

    public static LocalDateTime toLocalDateTime(OffsetDateTime time) { return toLocalDateTime(time.toInstant()); }

    public static LocalDateTime toLocalDateTime(LocalDate date) { return date.atStartOfDay(); }

    public static ZonedDateTime toZonedDateTime(double instant) { return toZonedDateTime(toInstant(instant)); }

    public static ZonedDateTime toZonedDateTime(long instant) { return toZonedDateTime(toInstant(instant)); }

    public static ZonedDateTime toZonedDateTime(Date date) { return toZonedDateTime(date.getTime()); }

    public static ZonedDateTime toZonedDateTime(Number instant) { return toZonedDateTime(instant.longValue()); }

    public static ZonedDateTime toZonedDateTime(Instant instant) {
        return ZonedDateTime.ofInstant(instant, systemDefault());
    }

    public static ZonedDateTime toZonedDateTime(Calendar calendar) { return toZonedDateTime(calendar.toInstant()); }

    public static ZonedDateTime toZonedDateTime(LocalDateTime time) { return time.atZone(systemDefault()); }

    public static ZonedDateTime toZonedDateTime(OffsetDateTime time) { return toZonedDateTime(time.toInstant()); }

    public static ZonedDateTime toZonedDateTime(LocalDate date) { return date.atStartOfDay(ZoneId.systemDefault()); }

    public static OffsetDateTime toOffsetDateTime(double instant) { return toOffsetDateTime(toInstant(instant)); }

    public static OffsetDateTime toOffsetDateTime(long instant) { return toOffsetDateTime(toInstant(instant)); }

    public static OffsetDateTime toOffsetDateTime(Date date) { return toOffsetDateTime(date.getTime()); }

    public static OffsetDateTime toOffsetDateTime(Number instant) { return toOffsetDateTime(instant.longValue()); }

    public static OffsetDateTime toOffsetDateTime(Instant instant) {
        return OffsetDateTime.ofInstant(instant, systemDefault());
    }

    public static OffsetDateTime toOffsetDateTime(Calendar calendar) { return toOffsetDateTime(calendar.toInstant()); }

    public static OffsetDateTime toOffsetDateTime(ZonedDateTime time) { return toOffsetDateTime(time.toInstant()); }

    public static OffsetDateTime toOffsetDateTime(LocalDateTime time) { return toOffsetDateTime(toLongValue(time)); }

    public static OffsetDateTime toOffsetDateTime(LocalDate date) { return toOffsetDateTime(date.atStartOfDay()); }

    public static LocalDate toLocalDate(double number) { return toLocalDateTime(number).toLocalDate(); }

    public static LocalDate toLocalDate(long number) { return toLocalDateTime(number).toLocalDate(); }

    public static LocalDate toLocalDate(Date date) { return toLocalDate(date.getTime()); }

    public static LocalDate toLocalDate(Calendar date) { return toLocalDate(date.getTimeInMillis()); }

    public static LocalDate toLocalDate(Number number) { return toLocalDateTime(number).toLocalDate(); }

    public static LocalDate toLocalDate(LocalDateTime time) { return time.toLocalDate(); }

    public static LocalDate toLocalDate(ZonedDateTime time) { return time.toLocalDate(); }

    public static LocalDate toLocalDate(OffsetDateTime time) { return time.toLocalDate(); }

    public static LocalDate toLocalDate(Instant time) { return toLocalDateTime(time).toLocalDate(); }

    public static LocalTime toLocalTime(double number) { return toLocalDateTime(number).toLocalTime(); }

    public static LocalTime toLocalTime(long number) { return toLocalDateTime(number).toLocalTime(); }

    public static LocalTime toLocalTime(Date date) { return toLocalTime(date.getTime()); }

    public static LocalTime toLocalTime(Calendar date) { return toLocalTime(date.getTimeInMillis()); }

    public static LocalTime toLocalTime(Number number) { return toLocalDateTime(number).toLocalTime(); }

    public static LocalTime toLocalTime(LocalDateTime time) { return time.toLocalTime(); }

    public static LocalTime toLocalTime(ZonedDateTime time) { return time.toLocalTime(); }

    public static LocalTime toLocalTime(OffsetDateTime time) { return time.toLocalTime(); }

    public static LocalTime toLocalTime(Instant time) { return toLocalDateTime(time).toLocalTime(); }

    public static Instant toInstant(double instant) { return Instant.ofEpochMilli((long) instant); }

    public static Instant toInstant(long instant) { return Instant.ofEpochMilli(instant); }

    public static Instant toInstant(Calendar calendar) { return calendar.toInstant(); }

    public static Instant toInstant(Date date) { return date.toInstant(); }

    public static Instant toInstant(LocalDate date) { return toInstant(date.atStartOfDay()); }

    public static Instant toInstant(LocalDateTime datetime) { return toInstant(datetime.atZone(systemDefault())); }

    public static Instant toInstant(ZonedDateTime time) { return time.toInstant(); }

    public static Instant toInstant(OffsetDateTime time) { return time.toInstant(); }

    public static Instant toInstant(Number time) { return Instant.ofEpochMilli(time.longValue()); }

    public static String toString(Date date, String pattern) { return new SimpleDateFormat(pattern).format(date); }

    public static String toString(Calendar date, String pattern) {
        return new SimpleDateFormat(pattern).format(date.getTime());
    }

    public static String toString(Number number, String pattern) { return new DecimalFormat(pattern).format(number); }

    public static String toString(double number, String pattern) { return new DecimalFormat(pattern).format(number); }

    public static String toString(long number, String pattern) { return new DecimalFormat(pattern).format(number); }

    public static double toDoubleValue(String val, String pattern) { return toNumber(val, pattern).doubleValue(); }

    public static Number toNumber(String number, String pattern) {
        try {
            return new DecimalFormat(pattern).parse(number);
        } catch (ParseException e) {
            throw new IllegalStateException("数字格式错误, 要求: " + pattern + ", 实际: " + number, e);
        }
    }

    public static Byte toByte(String value, String pattern) { return toNumber(value, pattern).byteValue(); }

    public static Short toShort(String value, String pattern) { return toNumber(value, pattern).shortValue(); }

    public static Integer toInteger(String value, String pattern) { return toNumber(value, pattern).intValue(); }

    public static Long toLong(String value, String pattern) { return toNumber(value, pattern).longValue(); }

    public static Float toFloat(String value, String pattern) { return toNumber(value, pattern).floatValue(); }

    public static Double toDouble(String value, String pattern) { return toNumber(value, pattern).doubleValue(); }
}
