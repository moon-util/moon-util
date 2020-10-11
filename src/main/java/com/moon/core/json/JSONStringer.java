package com.moon.core.json;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.enums.Const;
import com.moon.core.lang.StringJoiner;
import com.moon.core.lang.ref.BooleanAccessor;
import com.moon.core.lang.ref.DoubleAccessor;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.ref.LongAccessor;
import com.moon.core.time.DateTimeUtil;
import com.moon.core.time.DateUtil;
import com.moon.core.time.DateTime;
import com.moon.core.util.SetUtil;
import com.moon.core.util.function.TableConsumer;
import com.moon.core.util.interfaces.Stringify;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import sun.util.BuddhistCalendar;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static com.moon.core.time.CalendarUtil.isImportedJodaTime;

/**
 * @author moonsky
 */
class JSONStringer implements Stringify {

    private final static Map<Class, Stringifier> STRINGIFIER_MAP = new HashMap<>();

    final static JSONStringer STRINGER = new JSONStringer();

    static {
        for (DefaultStringifiers value : DefaultStringifiers.values()) {
            Set<Class> classes = value.getSupportsCls();
            for (Class cls : classes) {
                STRINGIFIER_MAP.put(cls, value);
            }
        }

        if (isImportedJodaTime()) {
            importJodaTime();
        }
    }

    @Override
    public String stringify(Object obj) {
        return stringify(obj, StringifySettings.EMPTY);
    }

    public String stringify(Object data, StringifySettings settings) {
        return stringify(new StringBuilder(), data, settings).toString();
    }

    public StringBuilder stringify(StringBuilder container, Object data, StringifySettings settings) {
        settings.initialize();
        doStringify(container, data, settings);
        settings.destroy();
        return container;
    }

    private JSONStringer() { }

    private static StringBuilder doStringify(StringBuilder builder, Object obj) {
        return doStringify(builder, obj, StringifySettings.EMPTY);
    }

    private static StringBuilder doStringify(StringBuilder builder, Object obj, StringifySettings settings) {
        if (obj == null) {
            return addUnwrapped(builder, settings, "null");
        } else {
            // 用 Map 的方式比逐个 instanceof 效率快
            Stringifier stringifier = STRINGIFIER_MAP.get(obj.getClass());
            if (stringifier == null) {
                if (obj instanceof Map) {
                    stringifyMap(builder, (Map) obj, settings);
                } else if (obj instanceof Iterable) {
                    stringifyCollect(builder, (Iterable) obj, settings);
                } else if (obj instanceof Object[]) {
                    stringifyObjects(builder, (Object[]) obj, settings);
                } else if (obj instanceof Iterator) {
                    stringifyIterator(builder, (Iterator) obj, settings);
                } else {
                    stringifyOfJavaBean(builder, obj, settings);
                }
            } else {
                stringifier.accept(builder, obj, settings);
            }
        }
        return builder;
    }

    private static void stringifyOfJavaBean(StringBuilder stringer, Object obj, StringifySettings settings) {
        doAppendAroundObject(stringer, obj, settings, (builder, setting, data) -> {
            BeanInfoUtil.getFieldDescriptorsMap(data.getClass()).forEach((name, desc) -> {
                beforeAppend(builder, settings);
                builder.append('"').append(name).append('"').append(':');
                doStringify(builder, desc.getValue(data)).append(',');
                afterAppended(builder, settings);
            });
        });
    }

    private static void stringifyIterator(
        StringBuilder builder, Iterator iterator, StringifySettings stringifySettings
    ) {
        doAppendAroundArray(builder, iterator, stringifySettings, (str, settings, values) -> {
            while (values.hasNext()) {
                beforeAppend(str, settings);
                doStringify(builder, values.next(), settings).append(',');
                afterAppended(str, settings);
            }
        });
    }

    private static void stringifyCollect(
        StringBuilder builder, Iterable iterable, StringifySettings stringifySettings
    ) {
        doAppendAroundArray(builder, iterable, stringifySettings, (str, settings, values) -> {
            for (Object value : values) {
                beforeAppend(str, settings);
                doStringify(str, value, settings).append(',');
                afterAppended(str, settings);
            }
        });
    }

    private static void stringifyMap(StringBuilder builder, Map inputMap, StringifySettings stringifySettings) {
        doAppendAroundObject(builder, inputMap, stringifySettings, (str, settings, values) -> {
            for (Object o : values.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                beforeAppend(str, settings);
                builder.append('"').append(entry.getKey()).append('"').append(':');
                doStringify(builder, entry.getValue(), settings).append(',');
                afterAppended(str, settings);
            }
        });
    }

    private static void stringifyObjects(StringBuilder builder, Object[] arr, StringifySettings stringifySettings) {
        doAppendAroundArray(builder, arr, stringifySettings, (str, settings, values) -> {
            for (Object v : values) {
                if (v == null) {
                    beforeAppend(str, settings);
                    str.append("null").append(',');
                    afterAppended(str, settings);
                } else {
                    doStringify(str, v, settings).append(',');
                }
            }
        });
    }

    interface Stringifier extends TableConsumer<StringBuilder, Object, StringifySettings>, Predicate {

        @Override
        void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings);

        Set<Class> getSupportsCls();

        @Override
        default boolean test(Object o) {
            if (o == null) {
                return false;
            }
            Set<Class> classes = getSupportsCls();
            if (o instanceof Class) {
                return classes.contains(o);
            }
            return classes.contains(o.getClass());
        }
    }

    @SuppressWarnings("all")
    enum DefaultStringifiers implements Stringifier {
        OPTIONAL_AT_UTIL {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                Object value = ((Optional) o).get();
                beforeAppend(builder, stringifySettings);
                doStringify(builder, value, stringifySettings);
                afterAppended(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(Optional.class); }
        },
        OPTIONAL_DOUBLE {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                double value = ((OptionalDouble) o).getAsDouble();
                beforeAppend(builder, stringifySettings);
                builder.append(value);
                afterAppended(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OptionalDouble.class); }
        },
        OPTIONAL_LONG {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                long value = ((OptionalLong) o).getAsLong();
                beforeAppend(builder, stringifySettings);
                builder.append(value);
                afterAppended(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OptionalLong.class); }
        },
        OPTIONAL_INT {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                int value = ((OptionalInt) o).getAsInt();
                beforeAppend(builder, stringifySettings);
                builder.append(value);
                afterAppended(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OptionalInt.class); }
        },
        CALENDAR {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                String value = DateUtil.format((Calendar) o);
                addString(builder, stringifySettings, value);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(Calendar.class, GregorianCalendar.class, BuddhistCalendar.class);
            }
        },
        DATE {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                String value = DateUtil.format((Date) o);
                addString(builder, stringifySettings, value);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(Date.class, Time.class, java.sql.Date.class, Timestamp.class, DateTime.class);
            }
        },
        JDK8_DATE {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                String value = DateTimeUtil.format((LocalDate) o);
                addString(stringBuilder, stringifySettings, value);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(LocalDate.class);
            }
        },
        JDK8_TIME {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                String value = DateTimeUtil.format((LocalTime) o);
                addString(stringBuilder, stringifySettings, value);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(LocalTime.class);
            }
        },
        JDK8_DATE_TIME {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                String value = DateTimeUtil.format((LocalDateTime) o);
                addString(stringBuilder, stringifySettings, value);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(LocalDateTime.class);
            }
        },
        JDK8_DATE_OFFSET {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                String value = DateTimeUtil.format(((OffsetDateTime) o).toLocalDateTime());
                addString(stringBuilder, stringifySettings, value);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OffsetDateTime.class); }
        },
        JDK8_DATE_ZONED {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                String value = DateTimeUtil.format(((ZonedDateTime) o).toLocalDateTime());
                addString(stringBuilder, stringifySettings, value);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(ZonedDateTime.class); }
        },
        WRAPPED {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                addString(builder, stringifySettings, o);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(CharSequence.class,
                    java.util.StringJoiner.class,
                    StringBuilder.class,
                    StringBuffer.class,
                    StringJoiner.class,
                    JSONString.class,
                    CharBuffer.class,
                    String.class,
                    // wrapped
                    Character.class,
                    char.class);
            }
        },
        UNWRAPPED {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                addUnwrapped(builder, stringifySettings, o);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(Number.class,
                    byte.class,
                    short.class,
                    int.class,
                    long.class,
                    float.class,
                    double.class,
                    Byte.class,
                    Short.class,
                    Integer.class,
                    Long.class,
                    Float.class,
                    Double.class,
                    BigDecimal.class,
                    BigInteger.class,
                    AtomicInteger.class,
                    AtomicLong.class,
                    LongAdder.class,
                    DoubleAdder.class,
                    IntAccessor.class,
                    LongAccessor.class,
                    DoubleAccessor.class,
                    LongAccumulator.class,
                    DoubleAccumulator.class,
                    JSONNumber.class,
                    Boolean.class,
                    // unwrapped
                    boolean.class,
                    JSONBoolean.class,
                    AtomicBoolean.class,
                    BooleanAccessor.class,
                    // unwrapped JSONNull
                    JSONNull.class);
            }
        },
        /**
         * 数组
         */
        CHARSEQUENCES {
            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(CharSequence[].class,
                    java.util.StringJoiner[].class,
                    StringBuilder[].class,
                    StringBuffer[].class,
                    StringJoiner[].class,
                    String[].class);
            }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                stringifyObjects(builder, (Object[]) values, stringifySettings);
            }
        },
        CHARS {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(char[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (char[]) values, stringifySettings, (str, settings, vs) -> {
                    for (char v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        },
        BOOLEANS {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(boolean[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (boolean[]) values, stringifySettings, (str, settings, vs) -> {
                    for (boolean v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        },
        INTS {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(int[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (int[]) values, stringifySettings, (str, settings, vs) -> {
                    for (int v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        },
        BYTES {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(byte[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (byte[]) values, stringifySettings, (str, settings, vs) -> {
                    for (byte v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        },
        SHORTS {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(short[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (short[]) values, stringifySettings, (str, settings, vs) -> {
                    for (short v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        },
        LONGS {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(long[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (long[]) values, stringifySettings, (str, settings, vs) -> {
                    for (long v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        },
        FLOATS {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(float[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (float[]) values, stringifySettings, (str, settings, vs) -> {
                    for (float v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        },
        DOUBLES {
            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(double[].class); }

            @Override
            public void accept(StringBuilder builder, Object values, StringifySettings stringifySettings) {
                doAppendAroundArray(builder, (double[]) values, stringifySettings, (str, settings, vs) -> {
                    for (double v : vs) {
                        afterAppended(beforeAppend(str, settings).append(v).append(','), settings);
                    }
                });
            }
        };
    }

    private static <T> void doAppendAroundObject(
        StringBuilder builder, T value, StringifySettings settings,//
        TableConsumer<StringBuilder, StringifySettings, T> consumer
    ) { doAppendAround(builder, value, settings, '{', '}', consumer); }

    private static <T> void doAppendAroundArray(
        StringBuilder builder, T value, StringifySettings settings,//
        TableConsumer<StringBuilder, StringifySettings, T> consumer
    ) { doAppendAround(builder, value, settings, '[', ']', consumer); }

    private static <T> void doAppendAround(
        StringBuilder builder, T value, StringifySettings settings, char open, char close,//
        TableConsumer<StringBuilder, StringifySettings, T> consumer
    ) {
        startAppend(builder, open, settings);
        consumer.accept(builder, settings, value);
        endAppended(builder, close, settings);
    }

    private static StringBuilder addString(StringBuilder sb, StringifySettings settings, Object value) {
        return addAround(sb, settings, value, (builder, str) -> builder.append('"').append(str).append('"'));
    }

    private static StringBuilder addUnwrapped(StringBuilder sb, StringifySettings settings, Object value) {
        return addAround(sb, settings, value, (builder, v) -> builder.append(v));
    }

    private static <T> StringBuilder addAround(
        StringBuilder builder, StringifySettings settings, T data, BiConsumer<StringBuilder, T> appender
    ) {
        appender.accept(beforeAppend(builder, settings), data);
        return afterAppended(builder, settings);
    }

    private static StringBuilder close(StringBuilder builder, char value) {
        builder.setCharAt(builder.length() - 1, value);
        return builder;
    }

    private static StringBuilder startAppend(StringBuilder builder, char open, StringifySettings settings) {
        addUnwrapped(builder, settings, String.valueOf(open));
        settings.open();
        return builder;
    }

    private static StringBuilder endAppended(StringBuilder builder, char close, StringifySettings settings) {
        if (settings.getIndentWhitespaces() != null) {
            // close(builder, '\n');
            builder.deleteCharAt(builder.length() - 1);
            settings.close();
            beforeAppend(builder, settings);
            builder.append(close);
        } else {
            close(builder, close);
        }
        return builder;
    }

    private static StringBuilder beforeAppend(StringBuilder builder, StringifySettings settings) {
        if (builder.length() > 0) {
            char last = builder.charAt(builder.length() - 1);
            if (last == ':') {
                builder.append(' ');
            } else if (last != '\n') {
                String indent = settings.getIndentWhitespaces();
                if (indent != null) {
                    builder.append('\n');
                    builder.append(indent);
                }
            }
        }
        return builder;
    }

    private static StringBuilder afterAppended(StringBuilder builder, StringifySettings settings) {
        return builder;
    }

    /*
     ~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ ~ joda date ~~~~~~~~~~~~~~~~~~~~~~
     ~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static void importJodaTime() {
        for (Stringifier value : JodaStringifier.values()) {
            Set<Class> classes = value.getSupportsCls();
            for (Class cls : classes) {
                STRINGIFIER_MAP.put(cls, value);
            }
        }
    }

    @SuppressWarnings("all")
    enum JodaStringifier implements Stringifier {
        DATE {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                org.joda.time.LocalDate date = (org.joda.time.LocalDate) o;
                String formatted = DateTimeFormat.forPattern(Const.PATTERN_DATE).print(date);
                addString(builder, stringifySettings, formatted);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(org.joda.time.LocalDate.class); }
        },
        TIME {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                org.joda.time.LocalTime time = (org.joda.time.LocalTime) o;
                String formatted = DateTimeFormat.forPattern(Const.PATTERN_TIME).print(time);
                addString(builder, stringifySettings, formatted);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(org.joda.time.LocalTime.class); }
        },
        DATE_TIME {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                String formatted;
                if (o instanceof ReadableInstant) {
                    ReadableInstant instant = (ReadableInstant) o;
                    formatted = DateTimeFormat.forPattern(Const.PATTERN).print(instant);
                } else if (o instanceof ReadablePartial) {
                    ReadablePartial partial = (ReadablePartial) o;
                    formatted = DateTimeFormat.forPattern(Const.PATTERN).print(partial);
                } else {
                    throw new IllegalArgumentException("Invalid datetime value of: " + o);
                }
                addString(builder, stringifySettings, formatted);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(org.joda.time.DateTime.class,
                    org.joda.time.Instant.class,
                    org.joda.time.LocalDateTime.class,
                    MutableDateTime.class);
            }
        };
    }
}
