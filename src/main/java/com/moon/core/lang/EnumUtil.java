package com.moon.core.lang;

import com.moon.core.util.DetectUtil;
import com.moon.core.util.ListUtil;

import java.util.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class EnumUtil {

    private EnumUtil() { noInstanceError(); }

    /**
     * 枚举的第一项值
     *
     * @param enumType
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T first(Class<T> enumType) {
        T[] enums = values(enumType);
        return enums.length > 0 ? enums[0] : null;
    }

    /**
     * 枚举类最后一项值
     *
     * @param enumType
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T last(Class<T> enumType) {
        T[] enums = values(enumType);
        return enums.length > 0 ? enums[enums.length - 1] : null;
    }

    /**
     * 枚举类所有值
     *
     * @param enumType
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T[] values(Class<T> enumType) {
        return enumType.getEnumConstants();
    }

    /**
     * 枚举类所有值
     *
     * @param enumType
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> List<T> valuesList(Class<T> enumType) {
        return ListUtil.toList(values(enumType));
    }

    /**
     * {@link EnumSet}
     *
     * @param enumType
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> EnumSet<T> valuesSet(Class<T> enumType) { return EnumSet.allOf(enumType); }

    /**
     * 按名称排序后枚举类所有值
     *
     * @param type
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T[] sortValues(Class<T> type) {
        return sortValues(type, Comparator.comparing(Enum::name));
    }

    /**
     * 按指定顺序排序的枚举类所有值
     *
     * @param type
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T[] sortValues(Class<T> type, Comparator<? super T> comparator) {
        return ArrayUtil.sort(comparator, values(type));
    }

    /**
     * 枚举类所有值的名称与值的 Map
     * enum's name map to enum's value
     *
     * @param enumType
     * @param <T>
     * @return
     * @see Enum#name()
     */
    public static <T extends Enum<T>> Map<String, T> valuesMap(Class<T> enumType) {
        T[] enums = values(enumType);
        int length = enums.length;
        Map<String, T> ret = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            ret.put(enums[i].name(), enums[i]);
        }
        return ret;
    }

    /**
     * 枚举类包含多少项
     *
     * @param enumType
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> int length(Class<T> enumType) { return values(enumType).length; }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) { return Enum.valueOf(enumType, name); }

    public static <T extends Enum<T>> T valueAt(Class<T> enumType, int index) { return toEnum(enumType, index); }

    /**
     * 是否包含指定名称的枚举值
     *
     * @param enumType
     * @param name
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> boolean contains(Class<T> enumType, String name) {
        if (name == null) {
            return false;
        }
        try {
            Enum.valueOf(enumType, name);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    /**
     * 返回符合指定名称的枚举值
     *
     * @param enumType
     * @param name
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, String name) {
        try {
            return name == null ? null : Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException e) {
            return throwEnumConst(enumType, name);
        }
    }

    /**
     * 返回指定位置的枚举值
     *
     * @param enumType
     * @param ordinal
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, int ordinal) {
        try {
            return values(enumType)[ordinal];
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            return throwEnumConst(enumType, ordinal);
        }
    }

    /**
     * 超级转换器
     *
     * @param value
     * @param enumType
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T toEnum(Object value, Class<T> enumType) {
        if (value == null || enumType == null) {
            return null;
        } else if (enumType.isInstance(value)) {
            return (T) value;
        } else if (value instanceof CharSequence) {
            String name = value.toString();
            if (DetectUtil.isNumeric(name)) {
                int ordinal = Integer.parseInt(name);
                return toEnum(enumType, ordinal);
            }
            return toEnum(enumType, name);
        } else if (value instanceof Integer) {
            return toEnum(enumType, (int) value);
        } else if (value instanceof Number) {
            return toEnum(enumType, ((Number) value).intValue());
        } else if (value.getClass().isEnum()) {
            return toEnum(enumType, ((Enum) value).name());
        }
        return throwEnumConst(enumType, value);
    }

    private static <T> T throwEnumConst(Class type, Object value) {
        throw new IllegalArgumentException(
            "No enum constant " + type.getCanonicalName() + "[" + value + "]");
    }
}
