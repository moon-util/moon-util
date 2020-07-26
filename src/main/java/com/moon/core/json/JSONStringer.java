package com.moon.core.json;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.enums.Const;
import com.moon.core.lang.StringJoiner;
import com.moon.core.lang.ref.BooleanAccessor;
import com.moon.core.lang.ref.DoubleAccessor;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.ref.LongAccessor;
import com.moon.core.time.DateTimeUtil;
import com.moon.core.util.DateUtil;
import com.moon.core.util.Datetime;
import com.moon.core.util.SetUtil;
import com.moon.core.util.function.TableConsumer;
import com.moon.core.util.interfaces.Stringify;
import org.joda.time.DateTime;
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
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.Predicate;

import static com.moon.core.util.CalendarUtil.isImportedJodaTime;

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

    private JSONStringer() { }

    private static StringBuilder stringify(StringBuilder builder, Object obj) {
        return stringify(builder, obj, StringifySettings.EMPTY);
    }

    private static StringBuilder stringify(StringBuilder builder, Object obj, StringifySettings settings) {
        if (obj == null) {
            return aroundAppend(builder.append("null"), settings);
        } else {
            Stringifier stringifier = STRINGIFIER_MAP.get(obj.getClass());
            if (stringifier == null) {
                if (obj instanceof Map) {
                    stringify(builder, (Map) obj, settings);
                } else if (obj instanceof Iterable) {
                    stringify(builder, (Iterable) obj, settings);
                } else if (obj instanceof Object[]) {
                    stringifyObjects(builder, (Object[]) obj, settings);
                } else if (obj instanceof Iterator) {
                    stringify(builder, (Iterator) obj, settings);
                } else {
                    stringifyOfJavaBean(builder, obj, settings);
                }
            } else {
                stringifier.accept(builder, obj, settings);
            }
        }
        return builder;
    }

    @Override
    public String stringify(Object obj) {
        return stringify(new StringBuilder(), obj).toString();
    }

    private static void stringifyOfJavaBean(StringBuilder stringer, Object obj, StringifySettings settings) {
        BeanInfoUtil.getFieldDescriptorsMap(obj.getClass()).forEach((name, desc) -> {
            stringer.append('"').append(name).append('"').append(':');
            stringify(stringer, desc.getValue(obj)).append(',');
            aroundAppend(stringer, settings);
        });
    }

    private static void stringify(StringBuilder builder, Iterator iterator, StringifySettings stringifySettings) {
        doAppendAroundArray(builder, iterator, stringifySettings, (str, settings, values) -> {
            while (values.hasNext()) {
                stringify(builder, values.next(), settings).append(',');
                aroundAppend(builder, settings);
            }
        });
    }

    private static void stringify(StringBuilder builder, Iterable iterable, StringifySettings stringifySettings) {
        doAppendAroundArray(builder, iterable, stringifySettings, (str, settings, values) -> {
            for (Object value : values) {
                stringify(builder, value, settings).append(',');
                aroundAppend(builder, settings);
            }
        });
    }

    private static void stringify(StringBuilder builder, Map inputMap, StringifySettings stringifySettings) {
        doAppendAroundObject(builder, inputMap, stringifySettings, (str, settings, values) -> {
            for (Object o : values.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                builder.append('"').append(entry.getKey()).append('"').append(':');
                stringify(builder, entry.getValue(), settings).append(',');
                aroundAppend(builder, settings);
            }
        });
    }

    private static void stringifyObjects(StringBuilder builder, Object[] arr, StringifySettings stringifySettings) {
        doAppendAroundArray(builder, arr, stringifySettings, (str, settings, values) -> {
            for (Object v : values) {
                if (v == null) {
                    aroundAppend(str.append("null").append(','), settings);
                } else {
                    str.append('"').append(v).append('"').append(',');
                    aroundAppend(str, settings);
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
                aroundAppend(builder.append(stringify(builder, value, stringifySettings)), stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(Optional.class); }
        },
        OPTIONAL_DOUBLE {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                aroundAppend(builder.append(((OptionalDouble) o).getAsDouble()), stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OptionalDouble.class); }
        },
        OPTIONAL_LONG {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                aroundAppend(builder.append(((OptionalLong) o).getAsLong()), stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OptionalLong.class); }
        },
        OPTIONAL_INT {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                aroundAppend(builder.append(((OptionalInt) o).getAsInt()), stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OptionalInt.class); }
        },
        CALENDAR {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                builder.append('"').append(DateUtil.format((Calendar) o)).append('"');
                aroundAppend(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(Calendar.class, GregorianCalendar.class, BuddhistCalendar.class);
            }
        },
        DATE {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                builder.append('"').append(DateUtil.format((Date) o)).append('"');
                aroundAppend(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(Date.class, Time.class, java.sql.Date.class, Timestamp.class, Datetime.class);
            }
        },
        JDK8_DATE {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                stringBuilder.append('"').append(DateTimeUtil.format((TemporalAccessor) o)).append('"');
                aroundAppend(stringBuilder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(LocalDateTime.class, LocalDate.class, LocalTime.class);
            }
        },
        JDK8_DATE_OFFSET {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                stringBuilder.append('"')
                    .append(DateTimeUtil.format(((OffsetDateTime) o).toLocalDateTime()))
                    .append('"');
                aroundAppend(stringBuilder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(OffsetDateTime.class); }
        },
        JDK8_DATE_ZONED {
            @Override
            public void accept(StringBuilder stringBuilder, Object o, StringifySettings stringifySettings) {
                stringBuilder.append('"')
                    .append(DateTimeUtil.format(((ZonedDateTime) o).toLocalDateTime()))
                    .append('"');
                aroundAppend(stringBuilder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() { return SetUtil.newSet(ZonedDateTime.class); }
        },
        WRAPPED {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                builder.append('"').append(o).append('"');
                aroundAppend(builder, stringifySettings);
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
                aroundAppend(builder.append(o), stringifySettings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
                        aroundAppend(str.append(v).append(','), settings);
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
        startAppend(builder.append(open), settings);
        consumer.accept(builder, settings, value);
        endAppended(close(builder, close), settings);
    }

    private static StringBuilder close(StringBuilder builder, char value) {
        builder.setCharAt(builder.length() - 1, value);
        return builder;
    }

    private static StringBuilder startAppend(StringBuilder builder, StringifySettings settings) {
        return builder;
    }

    private static StringBuilder endAppended(StringBuilder builder, StringifySettings settings) {
        return builder;
    }

    private static StringBuilder aroundAppend(StringBuilder builder, StringifySettings settings) {
        return builder;
    }

    /*
     ~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ ~ joda date ~~~~~~~~~~~~~~~~~~~~~~
     ~ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @SuppressWarnings("all")
    enum JodaStringifier implements Stringifier {
        DATE {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                org.joda.time.LocalDate time = (org.joda.time.LocalDate) o;
                builder.append('"').append(DateTimeFormat.forPattern(Const.PATTERN_TIME).print(time)).append('"');
                aroundAppend(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(org.joda.time.LocalDate.class);
            }
        },
        TIME {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                org.joda.time.LocalTime time = (org.joda.time.LocalTime) o;
                builder.append('"').append(DateTimeFormat.forPattern(Const.PATTERN_TIME).print(time)).append('"');
                aroundAppend(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(org.joda.time.LocalTime.class);
            }
        },
        DATE_TIME {
            @Override
            public void accept(StringBuilder builder, Object o, StringifySettings stringifySettings) {
                if (o instanceof ReadableInstant) {
                    ReadableInstant instant = (ReadableInstant) o;
                    builder.append('"').append(DateTimeFormat.forPattern(Const.PATTERN).print(instant)).append('"');
                } else if (o instanceof ReadablePartial) {
                    ReadablePartial partial = (ReadablePartial) o;
                    builder.append('"').append(DateTimeFormat.forPattern(Const.PATTERN).print(partial)).append('"');
                } else {
                    throw new IllegalArgumentException("Invalid datetime value of: " + o);
                }
                aroundAppend(builder, stringifySettings);
            }

            @Override
            public Set<Class> getSupportsCls() {
                return SetUtil.newSet(DateTime.class,
                    org.joda.time.Instant.class,
                    org.joda.time.LocalDateTime.class,
                    MutableDateTime.class);
            }
        };
    }

    public static void importJodaTime() {
        for (Stringifier value : JodaStringifier.values()) {
            Set<Class> classes = value.getSupportsCls();
            for (Class cls : classes) {
                STRINGIFIER_MAP.put(cls, value);
            }
        }
    }
}
