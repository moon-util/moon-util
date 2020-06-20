package com.moon.more.excel.table;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.annotation.format.DateTimePattern;
import com.moon.more.excel.annotation.format.NumberPattern;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;

import java.text.*;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.WeakHashMap;

/**
 * @author benshaoye
 */
abstract class CellSetter {

    private final GetTransfer transformer;

    private static boolean typeof(Class expected, Class actual) {
        return expected.isAssignableFrom(actual);
    }

    protected CellSetter(GetTransfer transformer) {
        this.transformer = transformer;
    }

    static CellSetter of(Attribute attr) {
        Class propertyType = attr.getPropertyType();

        DateTimePattern dateFmt = attr.getAnnotation(DateTimePattern.class);
        NumberPattern numberFmt = attr.getAnnotation(NumberPattern.class);
        if (numberFmt != null && Assert.isNumberType(propertyType)) {
            return tryNumber(propertyType, numberFmt);
        }

        if (dateFmt != null) {
            CellSetter setter = tryDateOrNull(propertyType, dateFmt.value());
            if (setter != null) {
                return setter;
            }
        }

        return new BasicSetter(attr.getTransformOrDefault());
    }

    /**
     * 设置单元格值
     *
     * @param factory
     * @param value
     */
    abstract void set(CellFactory factory, Object value);

    final GetTransfer getTransformer() { return transformer; }

    private static abstract class StringCellSetter extends CellSetter {

        StringCellSetter() { super(TransferForGet.STRING); }
    }

    private final static class NumberSetter extends StringCellSetter {

        private final NumberFormat formatter;

        private NumberSetter(NumberPattern pattern) {
            Locale locale = pattern.locale().getLocale();
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            DecimalFormat format = new DecimalFormat(pattern.value(), symbols);
            format.setRoundingMode(pattern.roundingMode());
            format.setGroupingUsed(pattern.grouped());

            int count = pattern.maxIntDigit();
            if (count >= 0) {
                format.setMaximumIntegerDigits(count);
            }
            count = pattern.minIntDigit();
            if (count >= 0) {
                format.setMinimumIntegerDigits(count);
            }
            count = pattern.maxFractionDigit();
            if (count >= 0) {
                format.setMaximumFractionDigits(count);
            }
            count = pattern.minFractionDigit();
            if (count >= 0) {
                format.setMinimumFractionDigits(count);
            }

            this.formatter = format;
        }

        String format(Object value) { return formatter.format(value); }

        @Override
        void set(CellFactory factory, Object value) {
            if (value != null) {
                getTransformer().transfer(factory, format(value));
            }
        }
    }

    private final static Table<Class, Object, CellSetter> cached

        = new TableImpl<>(() -> new WeakHashMap(), c -> new WeakHashMap());

    final static CellSetter get(Class propertyType, Object pattern) {
        return cached.get(propertyType, pattern);
    }

    final static synchronized void cache(Class propertyType, Object pattern, CellSetter setter) {
        cached.put(propertyType, pattern, setter);
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * number support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private final static CellSetter tryNumber(Class propertyType, NumberPattern pattern) {
        CellSetter setter = get(propertyType, pattern);
        if (setter == null) {
            setter = new NumberSetter(pattern);
            cache(propertyType, pattern, setter);
        }
        return setter;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * date support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private final static CellSetter tryDateOrNull(Class propertyType, String pattern) {
        return tryOrParse4Date(propertyType, pattern);
    }

    private final static CellSetter tryOrParse4Date(Class propertyType, String pattern) {
        CellSetter setter = get(propertyType, pattern);
        if (setter == null) {
            setter = doParseDateSetter(propertyType, pattern);
            cache(propertyType, pattern, setter);
        }
        return setter;
    }

    private final static CellSetter doParseDateSetter(Class propertyType, String pattern) {
        if (typeof(ChronoLocalDateTime.class, propertyType)) {
            return new J8LocalDateTimeSetter(pattern);
        }
        if (typeof(ChronoLocalDate.class, propertyType)) {
            return new J8LocalDateSetter(pattern);
        }
        if (typeof(LocalTime.class, propertyType)) {
            return new J8LocalTimeSetter(pattern);
        }
        if (Assert.isImportedJodaTime()) {
            CellSetter setter = tryOfJoda(propertyType, pattern);
            if (setter != null) { return setter; }
        }
        if (typeof(Calendar.class, propertyType)) {
            return new CalendarSetter(pattern);
        }
        if (typeof(Date.class, propertyType)) {
            return new UtilDateSetter(pattern);
        }
        return null;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * util date time support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private abstract static class DefaultDateSetter extends StringCellSetter {

        private final String pattern;
        private final ThreadLocal<DateFormat> formatter;

        protected DefaultDateSetter(String pattern) {
            this.formatter = new ThreadLocal<>();
            this.pattern = pattern;
        }

        final DateFormat getFormatter() {
            DateFormat format = formatter.get();
            if (format == null) {
                format = new SimpleDateFormat(pattern);
                formatter.set(format);
            }
            return format;
        }

        /**
         * 转换问 util 日期
         *
         * @param value
         *
         * @return
         */
        abstract Date toDate(Object value);

        @Override
        void set(CellFactory factory, Object value) {
            if (value != null) {
                String date = getFormatter().format(toDate(value));
                getTransformer().transfer(factory, date);
            }
        }
    }

    private final static class CalendarSetter extends DefaultDateSetter {

        protected CalendarSetter(String pattern) { super(pattern); }

        @Override
        final Date toDate(Object value) { return ((Calendar) value).getTime(); }
    }

    private final static class UtilDateSetter extends DefaultDateSetter {

        protected UtilDateSetter(String pattern) { super(pattern); }

        @Override
        final Date toDate(Object value) { return (Date) value; }
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * joda datetime support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static CellSetter tryOfJoda(Class propertyType, String pattern) {
        if (typeof(ReadableInstant.class, propertyType)) {
            return new ReadableInstantSetter(pattern);
        }
        if (typeof(ReadablePartial.class, propertyType)) {
            return new ReadablePartialSetter(pattern);
        }
        return null;
    }

    private abstract static class JodaDateSetter extends StringCellSetter {

        private final org.joda.time.format.DateTimeFormatter formatter;

        private JodaDateSetter(String pattern) {
            this.formatter = DateTimeFormat.forPattern(pattern);
        }

        final org.joda.time.format.DateTimeFormatter getFormatter() {
            return formatter;
        }

        /**
         * 格式化日期
         *
         * @param value
         *
         * @return
         */
        abstract String toFormat(Object value);

        @Override
        final void set(CellFactory factory, Object value) {
            if (value != null) {
                getTransformer().transfer(factory, toFormat(value));
            }
        }
    }

    private final static class ReadableInstantSetter extends JodaDateSetter {

        private ReadableInstantSetter(String pattern) { super(pattern); }

        @Override
        String toFormat(Object value) {
            return getFormatter().print((ReadableInstant) value);
        }
    }

    private final static class ReadablePartialSetter extends JodaDateSetter {

        private ReadablePartialSetter(String pattern) { super(pattern); }

        @Override
        String toFormat(Object value) {
            return getFormatter().print((ReadablePartial) value);
        }
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * Java 8 datetime support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private abstract static class Java8DateSetter extends StringCellSetter {

        private final DateTimeFormatter formatter;

        protected Java8DateSetter(String pattern) {
            this.formatter = DateTimeFormatter.ofPattern(pattern);
        }

        /**
         * 格式化日期
         *
         * @param value
         * @param formatter
         *
         * @return
         */
        abstract String toFormat(Object value, DateTimeFormatter formatter);

        @Override
        final void set(CellFactory factory, Object value) {
            if (factory != null) {
                String val = toFormat(value, formatter);
                getTransformer().transfer(factory, val);
            }
        }
    }

    /**
     * LocalDate
     */
    private final static class J8LocalDateSetter extends Java8DateSetter {

        protected J8LocalDateSetter(String pattern) { super(pattern); }

        @Override
        String toFormat(Object value, DateTimeFormatter formatter) {
            return ((ChronoLocalDate) value).format(formatter);
        }
    }

    /**
     * LocalDateTime
     */
    private final static class J8LocalDateTimeSetter extends Java8DateSetter {

        protected J8LocalDateTimeSetter(String pattern) { super(pattern); }

        @Override
        String toFormat(Object value, DateTimeFormatter formatter) {
            return ((ChronoLocalDateTime) value).format(formatter);
        }
    }

    /**
     * LocalTime
     */
    private final static class J8LocalTimeSetter extends Java8DateSetter {

        protected J8LocalTimeSetter(String pattern) { super(pattern); }

        @Override
        String toFormat(Object value, DateTimeFormatter formatter) {
            return ((LocalTime) value).format(formatter);
        }
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * default support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private final static class BasicSetter extends CellSetter {

        BasicSetter(GetTransfer transformer) { super(transformer); }

        @Override
        void set(CellFactory factory, Object value) {
            getTransformer().transfer(factory, value);
        }
    }
}
