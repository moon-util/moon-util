package com.moon.poi.excel.table;

import com.moon.core.json.JSONNumber;
import com.moon.core.lang.DoubleUtil;
import com.moon.core.lang.Joiner;
import com.moon.core.lang.ref.DoubleAccessor;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.ref.LongAccessor;
import com.moon.core.time.CalendarUtil;
import com.moon.core.time.DateUtil;
import com.moon.poi.excel.CellWriter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * @author moonsky
 */
enum TransferForGet implements GetTransfer {

    /**
     * 真假
     */
    BOOLEAN(Boolean.class, boolean.class) {
        @Override
        boolean test(Object data) { return data instanceof Boolean; }

        @Override
        boolean test(Class propertyType) {
            return propertyType == boolean.class || propertyType == Boolean.class;
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
            if (value != null) {
                factory.val((Boolean) value);
            }
        }
    },
    /**
     * 数字
     */
    DOUBLE(Double.class, double.class, DoubleAccessor.class) {
        @Override
        public boolean test(Object data) { return data instanceof Double; }

        @Override
        boolean test(Class propertyType) {
            return propertyType == double.class || propertyType == Double.class || propertyType == LongAccessor.class;
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
            if (value != null) {
                factory.val((Double) value);
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
        Joiner.class,
        Character.class,
        char.class) {
        @Override
        public boolean test(Object data) { return data instanceof CharSequence; }

        @Override
        boolean test(Class propertyType) {
            return CharSequence.class.isAssignableFrom(propertyType);
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
            if (value != null) {
                factory.val(value.toString());
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
        long.class,
        Long.class,
        Byte.class,
        BigDecimal.class,
        BigInteger.class,
        AtomicInteger.class,
        AtomicLong.class,
        DoubleAdder.class,
        LongAdder.class,
        IntAccessor.class,
        LongAccessor.class,
        DoubleAccessor.class,
        LongAccumulator.class,
        DoubleAccumulator.class,
        JSONNumber.class) {
        @Override
        public boolean test(Object data) { return data instanceof Number; }

        @Override
        boolean test(Class propertyType) {
            return Number.class.isAssignableFrom(propertyType);
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
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
        boolean test(Class propertyType) {
            return Date.class.isAssignableFrom(propertyType);
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
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
    CALENDAR(Calendar.class, GregorianCalendar.class) {
        @Override
        public boolean test(Object data) { return data instanceof Calendar; }

        @Override
        boolean test(Class propertyType) {
            return Calendar.class.isAssignableFrom(propertyType);
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
            if (value instanceof Calendar) {
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
        boolean test(Class propertyType) {
            return LocalTime.class.isAssignableFrom(propertyType);
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
            if (value != null) {
                factory.val(((LocalTime) value).format(formatter));
            }
        }
    },
    LOCAL_DATE(LocalDate.class) {
        @Override
        boolean test(Object data) { return data instanceof LocalDate; }

        @Override
        boolean test(Class propertyType) {
            return LocalDate.class.isAssignableFrom(propertyType);
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
            factory.val((LocalDate) value);
        }
    },
    LOCAL_DATE_TIME(LocalDateTime.class) {
        @Override
        boolean test(Object data) { return data instanceof LocalDateTime; }

        @Override
        boolean test(Class propertyType) {
            return LocalDateTime.class.isAssignableFrom(propertyType);
        }

        @Override
        public void transfer(CellWriter factory, Object value) {
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
        boolean test(Class propertyType) { return false; }

        @Override
        public void transfer(CellWriter factory, Object data) {
            factory.getCell().setBlank();
        }
    },
    UTIL_OPTIONAL_INT {
        @Override
        public void transfer(CellWriter factory, Object value) {
            OptionalInt optional = (OptionalInt) value;
            if (optional.isPresent()) {
                factory.getCell().setCellValue(optional.getAsInt());
            } else {
                factory.getCell().setBlank();
            }
        }

        @Override
        boolean test(Object data) { return data instanceof OptionalInt; }

        @Override
        boolean test(Class propertyType) { return propertyType == OptionalInt.class; }
    },
    UTIL_OPTIONAL_LONG {
        @Override
        public void transfer(CellWriter factory, Object value) {
            OptionalLong optional = (OptionalLong) value;
            if (optional.isPresent()) {
                factory.getCell().setCellValue(optional.getAsLong());
            } else {
                factory.getCell().setBlank();
            }
        }

        @Override
        boolean test(Object data) { return data instanceof OptionalLong; }

        @Override
        boolean test(Class propertyType) { return propertyType == OptionalLong.class; }
    },
    UTIL_OPTIONAL_DOUBLE {
        @Override
        public void transfer(CellWriter factory, Object value) {
            OptionalDouble optional = (OptionalDouble) value;
            if (optional.isPresent()) {
                factory.getCell().setCellValue(optional.getAsDouble());
            } else {
                factory.getCell().setBlank();
            }
        }

        @Override
        boolean test(Object data) { return data instanceof OptionalDouble; }

        @Override
        boolean test(Class propertyType) { return propertyType == OptionalDouble.class; }
    },
    /**
     * 默认
     */
    DEFAULT() {
        @Override
        boolean test(Object data) { return true; }

        @Override
        boolean test(Class propertyType) { return true; }

        @Override
        public void transfer(CellWriter factory, Object data) {
            if (data != null) {
                factory.val(data.toString());
            }
        }
    };

    private static class Cached {

        final static Map<Class, TransferForGet> SUPPORTS = new HashMap<>();
    }

    TransferForGet(Class... supports) {
        for (Class<?> support : supports) {
            Cached.SUPPORTS.put(support, this);
        }
    }

    public static TransferForGet findOrDefault(Class propertyType) {
        TransferForGet transfer = Cached.SUPPORTS.get(propertyType);
        if (transfer == null) {
            for (TransferForGet value : values()) {
                if (value.test(propertyType)) {
                    return value;
                }
            }
        }
        return transfer == null ? DEFAULT : transfer;
    }

    abstract boolean test(Object data);

    abstract boolean test(Class propertyType);
}
