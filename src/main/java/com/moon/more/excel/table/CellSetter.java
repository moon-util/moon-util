package com.moon.more.excel.table;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.annotation.format.DateTimePattern;
import com.moon.more.excel.annotation.format.NumberPattern;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;

import java.text.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Supplier;

import static org.joda.time.format.DateTimeFormat.forPattern;

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
            return tryNumberAndCache(numberFmt);
        }

        if (dateFmt != null) {
            CellSetter setter = tryDateOrNull(propertyType, dateFmt);
            if (setter != null) {
                return setter;
            }
        }

        return basicSetterMap.get(attr.getTransformOrDefault());
    }

    private final static Map<GetTransfer, BasicSetter> basicSetterMap = new EnumMap(TransferForGet.class);

    static {
        for (TransferForGet value : TransferForGet.values()) {
            basicSetterMap.put(value, new BasicSetter(value));
        }
    }

    /**
     * 设置单元格值
     *
     * @param factory
     * @param value
     */
    abstract void set(CellFactory factory, Object value);

    final GetTransfer getTransfer() { return transformer; }

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

            boolean grouping = pattern.grouping();
            if (grouping) {
                format.setGroupingUsed(true);
                format.setGroupingSize(pattern.groupingSize());
            }

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
                getTransfer().transfer(factory, format(value));
            }
        }
    }

    private final static Table<Class, Object, CellSetter> cached

        = TableImpl.newWeakHashTable();

    private static CellSetter get(Class propertyType, Object pattern) {
        return cached.get(propertyType, pattern);
    }

    private static synchronized void cache(Class propertyType, Object pattern, CellSetter setter) {
        cached.putIfAbsent(propertyType, pattern, setter);
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * number support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private final static CellSetter tryNumberAndCache(NumberPattern pattern) {
        CellSetter setter = get(Number.class, pattern);
        if (setter == null) {
            setter = new NumberSetter(pattern);
            cache(Number.class, pattern, setter);
        }
        return setter;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * date support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private final static CellSetter tryDateOrNull(Class propertyType, DateTimePattern pattern) {
        CellSetter setter = get(propertyType, pattern);
        if (setter == null) {
            setter = doParseAndCache(propertyType, pattern);
        }
        return setter;
    }

    private final static CellSetter doParseAndCache(Class propertyType, DateTimePattern pattern) {
        if (typeof(TemporalAccessor.class, propertyType)) {
            CellSetter setter = new TemporalAccessorSetter(pattern);
            cache(TemporalAccessor.class, pattern, setter);
            return setter;
        }
        if (Assert.isImportedJodaTime()) {
            CellSetter setter = tryJodaAndCache(propertyType, pattern);
            if (setter != null) { return setter; }
        }
        if (typeof(Calendar.class, propertyType)) {
            CellSetter setter = new CalendarSetter(pattern);
            cache(Calendar.class, pattern, setter);
            return setter;
        }
        if (typeof(Date.class, propertyType)) {
            CellSetter setter = new UtilDateSetter(pattern);
            cache(Date.class, pattern, setter);
            return setter;
        }
        return null;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * util date time support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private abstract static class DefaultDateSetter extends StringCellSetter {

        private final ThreadLocal<DateFormat> formatter;
        private final Supplier<DateFormat> creator;

        protected DefaultDateSetter(DateTimePattern pattern) {
            String format = pattern.value();
            Locale locale = pattern.locale().getLocale();
            this.formatter = new ThreadLocal<>();
            this.creator = () -> new SimpleDateFormat(format, locale);
        }

        final DateFormat getFormatter() {
            DateFormat format = formatter.get();
            if (format == null) {
                format = creator.get();
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
                getTransfer().transfer(factory, date);
            }
        }
    }

    private final static class CalendarSetter extends DefaultDateSetter {

        protected CalendarSetter(DateTimePattern pattern) { super(pattern); }

        @Override
        final Date toDate(Object value) { return ((Calendar) value).getTime(); }
    }

    private final static class UtilDateSetter extends DefaultDateSetter {

        protected UtilDateSetter(DateTimePattern pattern) { super(pattern); }

        @Override
        final Date toDate(Object value) { return (Date) value; }
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * joda datetime support
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static CellSetter tryJodaAndCache(Class propertyType, DateTimePattern pattern) {
        if (typeof(ReadableInstant.class, propertyType)) {
            CellSetter setter = new ReadableInstantSetter(pattern);
            cache(ReadableInstant.class, pattern, setter);
            return setter;
        }
        if (typeof(ReadablePartial.class, propertyType)) {
            CellSetter setter = new ReadablePartialSetter(pattern);
            cache(ReadableInstant.class, pattern, setter);
            return setter;
        }
        return null;
    }

    private abstract static class JodaDateSetter extends StringCellSetter {

        private final org.joda.time.format.DateTimeFormatter formatter;

        private JodaDateSetter(DateTimePattern pattern) {
            String format = pattern.value();
            Locale locale = pattern.locale().getLocale();
            this.formatter = forPattern(format).withLocale(locale);
        }

        final org.joda.time.format.DateTimeFormatter getFormatter() { return formatter; }

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
                getTransfer().transfer(factory, toFormat(value));
            }
        }
    }

    private final static class ReadableInstantSetter extends JodaDateSetter {

        private ReadableInstantSetter(DateTimePattern pattern) { super(pattern); }

        @Override
        String toFormat(Object value) {
            return getFormatter().print((ReadableInstant) value);
        }
    }

    private final static class ReadablePartialSetter extends JodaDateSetter {

        private ReadablePartialSetter(DateTimePattern pattern) { super(pattern); }

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

    private final static class TemporalAccessorSetter extends StringCellSetter {

        private final DateTimeFormatter formatter;

        protected TemporalAccessorSetter(DateTimePattern pattern) {
            Locale locale = pattern.locale().getLocale();
            this.formatter = DateTimeFormatter.ofPattern(pattern.value(), locale);
        }

        /**
         * 格式化日期
         *
         * @param value
         * @param formatter
         *
         * @return
         */
        final String toFormat(Object value, DateTimeFormatter formatter) {
            return formatter.format((TemporalAccessor) value);
        }

        @Override
        final void set(CellFactory factory, Object value) {
            if (factory != null) {
                String val = toFormat(value, formatter);
                getTransfer().transfer(factory, val);
            }
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
            getTransfer().transfer(factory, value);
        }
    }
}
