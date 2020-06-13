package com.moon.more.excel.table;

import com.moon.core.lang.DoubleUtil;
import com.moon.core.util.CalendarUtil;
import com.moon.core.util.DateUtil;
import com.moon.core.util.SetUtil;
import com.moon.more.excel.CellFactory;
import sun.util.BuddhistCalendar;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import static java.util.Collections.unmodifiableSet;

/**
 * @author benshaoye
 */
enum TransformForGet implements Transformer {

    /**
     * 真假
     */
    BOOLEAN(Boolean.class, boolean.class) {
        @Override
        boolean test(Object data) { return data instanceof Boolean; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            if (value != null) {
                factory.val((Boolean) value);
            }
        }
    },
    /**
     * 字符串
     */
    STRING(String.class,
        StringBuilder.class,
        StringBuffer.class,
        StringJoiner.class,
        com.moon.core.lang.StringJoiner.class,
        Character.class,
        char.class) {
        @Override
        public boolean test(Object data) { return data instanceof CharSequence; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            if (value != null) {
                factory.val(value.toString());
            }
        }
    },
    DOUBLE(Double.class, double.class) {
        @Override
        public boolean test(Object data) { return data instanceof Double; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            if (value != null) {
                factory.val((Double) value);
            }
        }
    },
    /**
     * 数字
     */
    NUMBER(Integer.class,
        int.class,
        Float.class,
        float.class,
        Short.class,
        short.class,
        byte.class,
        Byte.class,
        BigDecimal.class,
        BigInteger.class,
        AtomicInteger.class,
        AtomicLong.class,
        DoubleAdder.class,
        LongAdder.class) {
        @Override
        public boolean test(Object data) { return data instanceof Number; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            if (value instanceof Number) {
                factory.val(((Number) value).doubleValue());
            } else {
                factory.val(DoubleUtil.toDoubleValue(value));
            }
        }
    },
    /**
     * 日期
     */
    DATE(Date.class, Time.class, Timestamp.class, java.sql.Date.class) {
        @Override
        public boolean test(Object data) { return data instanceof Date; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            if (value instanceof Number) {
                factory.val((Date) value);
            } else {
                factory.val(DateUtil.toDate(value));
            }
        }
    },
    /**
     * 日历
     */
    CALENDAR(Calendar.class, GregorianCalendar.class, BuddhistCalendar.class) {
        @Override
        public boolean test(Object data) { return data instanceof Calendar; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            if (value instanceof Number) {
                factory.val((Calendar) value);
            } else {
                factory.val(CalendarUtil.toCalendar(value));
            }
        }
    },
    LOCAL_TIME(LocalTime.class) {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        @Override
        boolean test(Object data) { return data instanceof LocalTime; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            if (value != null) {
                factory.val(((LocalTime) value).format(formatter));
            }
        }
    },
    LOCAL_DATE(LocalDate.class) {
        @Override
        boolean test(Object data) { return data instanceof LocalDate; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            factory.val((LocalDate) value);
        }
    },
    LOCAL_DATE_TIME(LocalDateTime.class) {
        @Override
        boolean test(Object data) { return data instanceof LocalDateTime; }

        @Override
        public void doTransform(CellFactory factory, Object value) {
            factory.val((LocalDateTime) value);
        }
    },
    /**
     * 空
     */
    NULL() {
        @Override
        public boolean test(Object data) { return data == null; }

        @Override
        public void doTransform(CellFactory factory, Object data) {
            factory.getCell().setBlank();
        }
    },
    /**
     * 默认
     */
    DEFAULT() {
        @Override
        boolean test(Object data) { return true; }

        @Override
        public void doTransform(CellFactory factory, Object data) {
            if (data != null) {
                factory.val(data.toString());
            }
        }
    };

    private final Set<Class> supports;

    private static class Cached {

        final static Map<Class, TransformForGet> SUPPORTS = new HashMap<>();
    }

    TransformForGet(Class... supports) {
        for (Class<?> support : supports) {
            Cached.SUPPORTS.put(support, this);
        }
        this.supports = unmodifiableSet(SetUtil.toSet(supports));
    }

    public static TransformForGet findOrDefault(Class propertyType) {
        TransformForGet transfer = Cached.SUPPORTS.get(propertyType);
        return transfer == null ? DEFAULT : transfer;
    }

    abstract boolean test(Object data);
}
