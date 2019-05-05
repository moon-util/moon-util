package com.moon.core.time;

import com.moon.core.lang.SupportUtil;
import com.moon.core.util.function.IntBiFunction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.util.Calendar.*;

/**
 * @author benshaoye
 */
public final class TimeUtil {
    private TimeUtil() {
        noInstanceError();
    }

    public final static long now() { return System.currentTimeMillis(); }

    public final static LocalDate nowDate() { return LocalDate.now(); }

    public final static LocalTime nowTime() { return LocalTime.now(); }

    public final static LocalDateTime nowDateTime() { return LocalDateTime.now(); }

    /*
     * ----------------------------------------------------------------------------------
     * converters
     * ----------------------------------------------------------------------------------
     */

    public final static LocalDate toDate(Calendar calendar) {
        return LocalDate.of(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH));
    }

    public final static LocalTime toTime(Calendar calendar) {
        return LocalTime.of(calendar.get(HOUR_OF_DAY), calendar.get(MINUTE),
            calendar.get(SECOND), calendar.get(MILLISECOND));
    }

    public final static LocalDateTime toDateTime(Calendar calendar) {
        return LocalDateTime.of(calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH),
            calendar.get(HOUR_OF_DAY), calendar.get(MINUTE), calendar.get(SECOND),
            calendar.get(MILLISECOND));
    }

    public final static LocalDate toDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return toDate(calendar);
    }

    public final static LocalTime toTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return toTime(calendar);
    }

    public final static LocalDateTime toDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return toDateTime(calendar);
    }

    public final static LocalDate toDate(CharSequence date) { return LocalDate.parse(date); }

    public final static LocalTime toTime(CharSequence date) { return LocalTime.parse(date); }

    public final static LocalDateTime toDateTime(CharSequence date) { return LocalDateTime.parse(date); }

    public final static LocalDate toDate(long milliseconds) { return toDate(new Date(milliseconds)); }

    public final static LocalTime toTime(long milliseconds) { return toTime(new Date(milliseconds)); }

    public final static LocalDateTime toDateTime(long milliseconds) { return toDateTime(new Date(milliseconds)); }

    public final static LocalDate toDate(int... values) {
        int i = 0, len = values.length, max = 2;
        switch (len) {
            case 0:
                return LocalDate.now();
            case 1:
                return LocalDate.of(values[i++], 1, 1);
            case 2:
                return LocalDate.of(values[i++], values[i++], 1);
            default:
                if (len > max) {
                    return LocalDate.of(values[i++], values[i++], values[i++]);
                }
                throw new IllegalArgumentException("Can not cast to LocalDate of values: " + Arrays.toString(values));
        }
    }

    public final static LocalTime toTime(int... values) {
        int i = 0, len = values.length, max = 3;
        switch (len) {
            case 0:
                return LocalTime.now();
            case 1:
                return LocalTime.of(values[i++], 0);
            case 2:
                return LocalTime.of(values[i++], values[i++]);
            case 3:
                return LocalTime.of(values[i++], values[i++], values[i++]);
            default:
                if (len > max) {
                    return LocalTime.of(values[i++], values[i++], values[i++], values[i++]);
                }
                throw new IllegalArgumentException("Can not cast to LocalTime of values: " + Arrays.toString(values));
        }
    }

    public final static LocalDateTime toDateTime(int... values) {
        int i = 0, len = values.length, max = 6;
        switch (len) {
            case 0:
                return LocalDateTime.now();
            case 1:
                return LocalDateTime.of(values[i++], 1, 1, 0, 0);
            case 2:
                return LocalDateTime.of(values[i++], values[i++], 1, 0, 0);
            case 3:
                return LocalDateTime.of(values[i++], values[i++], values[i++], 0, 0);
            case 4:
                return LocalDateTime.of(values[i++], values[i++], values[i++], values[i++], 0);
            case 5:
                return LocalDateTime.of(values[i++], values[i++], values[i++], values[i++], values[i++]);
            case 6:
                return LocalDateTime.of(values[i++], values[i++], values[i++], values[i++], values[i++], values[i++]);
            default:
                if (len > max) {
                    return LocalDateTime.of(values[i++], values[i++], values[i++],
                        values[i++], values[i++], values[i++], values[i++]);
                }
                throw new IllegalArgumentException(
                    "Can not cast to LocalTime of values: " + Arrays.toString(values)
                );
        }
    }

    public final static LocalDate toDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return toDate((CharSequence) obj);
        }
        if (obj instanceof Number) {
            if (obj instanceof Long || obj instanceof Double) {
                return toDate(((Number) obj).longValue());
            }
            return toDate(new int[]{((Integer) obj).intValue()});
        }
        if (obj instanceof int[]) {
            return toDate((int[]) obj);
        }
        if (obj instanceof Date) {
            return toDate((Date) obj);
        }
        if (obj instanceof Calendar) {
            return toDate((Calendar) obj);
        }
        try {
            return toDate(SupportUtil.onlyOneItemOrSize(obj));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Can not cast to LocalDate of: %s", obj.toString()), e);
        }
    }

    public final static LocalTime toTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return toTime((CharSequence) obj);
        }
        if (obj instanceof Number) {
            if (obj instanceof Long || obj instanceof Double) {
                return toTime(((Number) obj).longValue());
            }
            return toTime(new int[]{((Integer) obj).intValue()});
        }
        if (obj instanceof int[]) {
            return toTime((int[]) obj);
        }
        if (obj instanceof Date) {
            return toTime((Date) obj);
        }
        if (obj instanceof Calendar) {
            return toTime((Calendar) obj);
        }
        try {
            return toTime(SupportUtil.onlyOneItemOrSize(obj));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Can not cast to LocalTime of: %s", obj.toString()), e);
        }
    }

    public final static LocalDateTime toDateTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return toDateTime((CharSequence) obj);
        }
        if (obj instanceof Number) {
            if (obj instanceof Long || obj instanceof Double) {
                return toDateTime(((Number) obj).longValue());
            }
            return toDateTime(new int[]{((Integer) obj).intValue()});
        }
        if (obj instanceof int[]) {
            return toDateTime((int[]) obj);
        }
        if (obj instanceof Date) {
            return toDateTime((Date) obj);
        }
        if (obj instanceof Calendar) {
            return toDateTime((Calendar) obj);
        }
        try {
            return toDateTime(SupportUtil.onlyOneItemOrSize(obj));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Can not cast to LocalDateTime of: %s", obj.toString()), e);
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * for each
     * ----------------------------------------------------------------------------------
     */

    public final static void forEachYears(LocalDate begin, LocalDate end, IntBiFunction<LocalDate, Boolean> consumer) {
        for (; begin.isBefore(end) && consumer.apply(begin.getYear(), begin); begin = begin.plusYears(1)) {
        }
    }

    public final static void forEachMonths(LocalDate begin, LocalDate end, IntBiFunction<LocalDate, Boolean> consumer) {
        for (; begin.isBefore(end) && consumer.apply(begin.getMonthValue(), begin); begin = begin.plusMonths(1)) {
        }
    }

    public final static void forEachDays(LocalDate begin, LocalDate end, IntBiFunction<LocalDate, Boolean> consumer) {
        for (; begin.isBefore(end) && consumer.apply(begin.getDayOfMonth(), begin); begin = begin.plusDays(1)) {
        }
    }

    public final static void forEachHours(LocalTime begin, LocalTime end, IntBiFunction<LocalTime, Boolean> consumer) {
        LocalDate now = LocalDate.now();
        LocalDateTime last = begin.isBefore(end) ? LocalDateTime.of(now, end) : LocalDateTime.of(now.plusDays(1), end);
        for (LocalDateTime start = LocalDateTime.of(now, begin);
             start.isBefore(last) && consumer.apply(start.getHour(), start.toLocalTime());
             start = start.plusHours(1)) {
        }
    }

    public final static void forEachMinutes(LocalTime begin, LocalTime end, IntBiFunction<LocalTime, Boolean> consumer) {
        LocalDate now = LocalDate.now();
        LocalDateTime last = begin.isBefore(end) ? LocalDateTime.of(now, end) : LocalDateTime.of(now.plusDays(1), end);
        for (LocalDateTime start = LocalDateTime.of(now, begin);
             start.isBefore(last) && consumer.apply(start.getMinute(), start.toLocalTime());
             start = start.plusMinutes(1)) {
        }
    }

    public final static void forEachSeconds(LocalTime begin, LocalTime end, IntBiFunction<LocalTime, Boolean> consumer) {
        LocalDate now = LocalDate.now();
        LocalDateTime last = begin.isBefore(end) ? LocalDateTime.of(now, end) : LocalDateTime.of(now.plusDays(1), end);
        for (LocalDateTime start = LocalDateTime.of(now, begin);
             start.isBefore(last) && consumer.apply(start.getSecond(), start.toLocalTime());
             start = start.plusSeconds(1)) {
        }
    }
}
