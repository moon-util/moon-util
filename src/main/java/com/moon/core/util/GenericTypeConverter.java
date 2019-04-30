package com.moon.core.util;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.enums.Converters;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.lang.reflect.ConstructorUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiFunction;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class GenericTypeConverter implements TypeConverter {

    protected final WeakAccessor<MapBuilder> mapAccessor = WeakAccessor.of(MapBuilder::new);
    protected final WeakAccessor<ListBuilder> listAccessor = WeakAccessor.of(ListBuilder::new);
    protected final WeakAccessor<ArrayBuilder> arrayAccessor = WeakAccessor.of(ArrayBuilder::new);
    protected final WeakAccessor<CollectionBuilder> collectionAccessor = WeakAccessor.of(CollectionBuilder::new);

    protected final Map<Class, BiFunction<Object, Class, Object>> converters = new HashMap<>();

    public GenericTypeConverter() {
        registerDefaultConverter();
    }

    /**
     * 注册默认转换器
     */
    private void registerDefaultConverter() {
        for (Converters value : Converters.values()) {
            BiFunction converter = value;
            add(value.TYPE, converter);
        }
        // Collection convert
        add(Collection.class, (value, toType) -> {
            if (value == null || toType == null) {
                return null;
            }
            Class cls;
            CollectionBuilder builder = collectionAccessor.get();
            if (value instanceof List) {
                return builder.toCollection((List) value, toType);
            } else if (value instanceof Collection) {
                return builder.toCollection((Collection) value, toType);
            } else if (value instanceof Map) {
                return builder.toCollection((Map) value, toType);
            } else if ((cls = value.getClass()).isArray()) {
                if (cls == int[].class) {
                    return builder.toCollection((int[]) value, toType);
                } else if (cls == long[].class) {
                    return builder.toCollection((long[]) value, toType);
                } else if (cls == double[].class) {
                    return builder.toCollection((double[]) value, toType);
                } else if (cls == byte[].class) {
                    return builder.toCollection((byte[]) value, toType);
                } else if (cls == char[].class) {
                    return builder.toCollection((char[]) value, toType);
                } else if (cls == short[].class) {
                    return builder.toCollection((short[]) value, toType);
                } else if (cls == boolean[].class) {
                    return builder.toCollection((boolean[]) value, toType);
                } else if (cls == float[].class) {
                    return builder.toCollection((float[]) value, toType);
                }
                return builder.toCollection((Object[]) value, toType);
            }
            return builder.toCollection(value, toType);
        });
        // List convert
        add(List.class, (value, toType) -> {
            if (value == null || toType == null) {
                return null;
            }
            ListBuilder builder = listAccessor.get();
            if (value instanceof List) {
                return builder.toList((List) value, toType);
            } else if (value instanceof Collection) {
                return builder.toList((Collection) value, toType);
            } else if (value instanceof Map) {
                return builder.toList((Map) value, toType);
            }
            Class cls;
            if ((cls = value.getClass()).isArray()) {
                if (cls == int[].class) {
                    return builder.toList((int[]) value, toType);
                } else if (cls == long[].class) {
                    return builder.toList((long[]) value, toType);
                } else if (cls == double[].class) {
                    return builder.toList((double[]) value, toType);
                } else if (cls == byte[].class) {
                    return builder.toList((byte[]) value, toType);
                } else if (cls == char[].class) {
                    return builder.toList((char[]) value, toType);
                } else if (cls == short[].class) {
                    return builder.toList((short[]) value, toType);
                } else if (cls == boolean[].class) {
                    return builder.toList((boolean[]) value, toType);
                } else if (cls == float[].class) {
                    return builder.toList((float[]) value, toType);
                }
                return builder.toList((Object[]) value, toType);
            }
            return builder.toList(value, toType);
        });
        add(Map.class, converterOfMap());
    }

    /**
     * Map convert
     *
     * @param <C>
     * @return
     */
    private <C> BiFunction<Object, Class<C>, C> converterOfMap() {
        return (value, toType) -> {
            if (value == null || toType == null) {
                return null;
            }
            Class cls;
            MapBuilder builder = mapAccessor.get();
            if (value instanceof Collection) {
                return builder.toMap((Collection) value, toType);
            } else if (value instanceof Map) {
                return builder.toMap((Map) value, toType);
            } else if ((cls = value.getClass()).isArray()) {
                if (cls == int[].class) {
                    return builder.toMap((int[]) value, toType);
                } else if (cls == long[].class) {
                    return builder.toMap((long[]) value, toType);
                } else if (cls == double[].class) {
                    return builder.toMap((double[]) value, toType);
                } else if (cls == byte[].class) {
                    return builder.toMap((byte[]) value, toType);
                } else if (cls == char[].class) {
                    return builder.toMap((char[]) value, toType);
                } else if (cls == short[].class) {
                    return builder.toMap((short[]) value, toType);
                } else if (cls == boolean[].class) {
                    return builder.toMap((boolean[]) value, toType);
                } else if (cls == float[].class) {
                    return builder.toMap((float[]) value, toType);
                }
                return builder.toMap((Object[]) value, toType);
            } else {
                return builder.toMap(value, toType);
            }
        };
    }

    /**
     * 添加新的转换器，受保护，实际执行的方法
     *
     * @param toType
     * @param func
     * @param <C>
     * @return
     */
    private <C> void add(Class<C> toType, BiFunction<Object, Class<C>, C> func) {
        BiFunction converter = func;
        converters.put(toType, converter);
    }

    /**
     * 注册新的转换器，如果已存在将覆盖原有转换器
     *
     * @param toType
     * @param func
     * @param <C>
     * @return
     */
    @Override
    public <C> TypeConverter register(Class<C> toType, BiFunction<Object, Class<C>, C> func) {
        add(toType, func);
        return this;
    }

    /**
     * 注册缺少的转换器
     *
     * @param toType
     * @param func
     * @param <C>
     * @return
     */
    @Override
    public <C> TypeConverter registerIfAbsent(Class<C> toType, BiFunction<Object, Class<C>, C> func) {
        BiFunction converter = func;
        converters.putIfAbsent(toType, converter);
        return this;
    }

    /**
     * 转换成指定类型数据
     *
     * @param value
     * @param type
     * @param <T>
     * @return
     */
    @Override
    public <T> T toType(Object value, Class<T> type) {
        if (value == null || type == null) {
            return null;
        }

        BiFunction<Object, Class, Object> func = converters.get(type);
        if (func != null) {
            return (T) func.apply(value, type);
        } else if (type.isEnum()) {
            return (T) converters.get(Enum.class).apply(value, type);
        } else if (type.isArray() || type == Array.class) {
            return toArray(value, type);
        } else if (List.class.isAssignableFrom(type)) {
            return (T) converters.get(List.class).apply(value, type);
        } else if (Collection.class.isAssignableFrom(type)) {
            return (T) converters.get(Collection.class).apply(value, type);
        } else if (Map.class.isAssignableFrom(type)) {
            return (T) converters.get(Map.class).apply(value, type);
        } else if (value instanceof Map) {
            return toBean((Map) value, type);
        }

        return (T) value;
    }

    private <T, S> T convert(Object value, Class<T> type, Class<S> superType) {
        return (T) converters.get(superType).apply(value, type);
    }

    private <E> E convert(Object value, Class<E> type) {
        return convert(value, type, type);
    }

    @Override
    public boolean toBooleanValue(Object value) {
        return convert(value, boolean.class);
    }

    @Override
    public Boolean toBoolean(Object value) {
        return convert(value, Boolean.class);
    }

    @Override
    public char toCharValue(Object value) {
        return convert(value, char.class);
    }

    @Override
    public Character toCharacter(Object value) {
        return convert(value, Character.class);
    }

    @Override
    public byte toByteValue(Object value) {
        return convert(value, byte.class);
    }

    @Override
    public Byte toByte(Object value) {
        return convert(value, Byte.class);
    }

    @Override
    public short toShortValue(Object value) {
        return convert(value, short.class);
    }

    @Override
    public Short toShort(Object value) {
        return convert(value, Short.class);
    }

    @Override
    public int toIntValue(Object value) {
        return convert(value, int.class);
    }

    @Override
    public Integer toInteger(Object value) {
        return convert(value, Integer.class);
    }

    @Override
    public long toLongValue(Object value) {
        return convert(value, long.class);
    }

    @Override
    public Long toLong(Object value) {
        return convert(value, Long.class);
    }

    @Override
    public float toFloatValue(Object value) {
        return convert(value, float.class);
    }

    @Override
    public Float toFloat(Object value) {
        return convert(value, Float.class);
    }

    @Override
    public double toDoubleValue(Object value) {
        return convert(value, double.class);
    }

    @Override
    public Double toDouble(Object value) {
        return convert(value, Double.class);
    }

    @Override
    public BigInteger toBigInteger(Object value) {
        return convert(value, BigInteger.class);
    }

    @Override
    public BigDecimal toBigDecimal(Object value) {
        return convert(value, BigDecimal.class);
    }

    @Override
    public Date toDate(Object value) {
        return convert(value, Date.class);
    }

    @Override
    public java.sql.Date toSqlDate(Object value) {
        return convert(value, java.sql.Date.class);
    }

    @Override
    public Timestamp toTimestamp(Object value) {
        return convert(value, Timestamp.class);
    }

    @Override
    public Time toTime(Object value) {
        return convert(value, Time.class);
    }

    @Override
    public Calendar toCalendar(Object value) {
        return convert(value, Calendar.class);
    }

    @Override
    public String toString(Object value) {
        return convert(value, String.class);
    }

    @Override
    public StringBuilder toStringBuilder(Object value) {
        return convert(value, StringBuilder.class);
    }

    @Override
    public StringBuffer toStringBuffer(Object value) {
        return convert(value, StringBuffer.class);
    }

    /**
     * if data is null or clazz is null will back null
     *
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T extends Enum<T>> T toEnum(Object value, Class<T> clazz) {
        return convert(value, clazz, Enum.class);
    }

    /**
     * if data is null or clazz is null will back null.
     * And can not convert to clazz will throw Exception
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    @Override
    public <T> T toBean(Map map, Class<T> clazz) {
        if (clazz == null || map == null) {
            return null;
        }
        T obj = ConstructorUtil.newInstance(clazz);
        if (!map.isEmpty()) {
            BeanInfoUtil.getFieldDescriptorsMap(clazz).forEach((name, desc) -> {
                map.computeIfPresent(name, (key, value) -> {
                    desc.ifSetterPresent(descriptor ->
                        descriptor.setValue(obj, value, true));
                    return value;
                });
            });
        }
        return obj;
    }

    /**
     * if data is null or clazz is null will back null
     *
     * @param value
     * @param arrayType
     * @param <T>
     * @return
     */
    @Override
    public <T> T toArray(Object value, Class<T> arrayType) {
        if (arrayType == null) {
            return null;
        } else if (arrayType == Array.class) {
            return (T) toTypeArray(value, Object.class);
        } else if (arrayType.isArray()) {
            return (T) toTypeArray(value, arrayType.getComponentType());
        }
        return ThrowUtil.doThrow("Must an array type:" + arrayType);
    }

    /**
     * if data is null or clazz is null will back null
     *
     * @param value
     * @param componentType
     * @param <T>
     * @return
     */
    @Override
    public <T> T[] toTypeArray(Object value, Class<T> componentType) {
        if (value == null || componentType == null) {
            return null;
        }
        Class cls;
        ArrayBuilder builder = arrayAccessor.get();
        if (value instanceof List) {
            return builder.toArray((List) value, componentType);
        } else if (value instanceof Collection) {
            return builder.toArray((Collection) value, componentType);
        } else if (value instanceof Map) {
            return builder.toArray((Map) value, componentType);
        } else if ((cls = value.getClass()).isArray()) {
            if (cls == int[].class) {
                return builder.toArray((int[]) value, componentType);
            } else if (cls == long[].class) {
                return builder.toArray((long[]) value, componentType);
            } else if (cls == double[].class) {
                return builder.toArray((double[]) value, componentType);
            } else if (cls == byte[].class) {
                return builder.toArray((byte[]) value, componentType);
            } else if (cls == char[].class) {
                return builder.toArray((char[]) value, componentType);
            } else if (cls == short[].class) {
                return builder.toArray((short[]) value, componentType);
            } else if (cls == boolean[].class) {
                return builder.toArray((boolean[]) value, componentType);
            } else if (cls == float[].class) {
                return builder.toArray((float[]) value, componentType);
            }
            return builder.toArray((Object[]) value, componentType);
        }
        return builder.toArray(value, componentType);
    }

    @Override
    public <T extends Map> T toMap(Object value, Class<T> mapClass) {
        return convert(value, mapClass, Map.class);
    }

    @Override
    public <T extends List> T toList(Object value, Class<T> listType) {
        return convert(value, listType, List.class);
    }

    @Override
    public <T extends Collection> T toCollection(Object value, Class<T> collectionType) {
        return convert(value, collectionType, Collection.class);
    }

    // **********************************************************************************************
    // collections builder: to collection, to data, to array and to map.
    // 凡是进入 builder 的指定类型（type）和 data 均不为 null，故不做 null 值判断
    // **********************************************************************************************

    private static class CollectionBuilder {
        Collection toCollection(Map value, Class listImplType) {
            return createCollect(listImplType, isAbstract(listImplType), value.values());
        }

        Collection toCollection(Collection value, Class listImplType) {
            if (listImplType.isInstance(value)) {
                return value;
            }
            return createCollect(listImplType, isAbstract(listImplType), value);
        }

        Collection toCollection(Object[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(int[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(long[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(double[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(char[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(byte[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(short[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(float[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(boolean[] array, Class listImplType) {
            boolean isDefault;
            if (isDefault = isAbstract(listImplType)) {
                return ListUtil.toList(array);
            } else {
                return createCollect(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        Collection toCollection(Object value, Class listImplType) {
            Collection ret = createCollect(listImplType, isAbstract(listImplType));
            ret.add(value);
            return ret;
        }

        <T extends Collection> Collection createCollect(Class<T> listImplType, boolean isDefault, Collection c) {
            Collection list = createCollect(listImplType, isDefault);
            list.addAll(c);
            return list;
        }

        <T extends Collection> Collection createCollect(Class<T> listImplType, boolean isDefault) {
            return newInstance(isDefault, listImplType,
                isDefault && Set.class.isAssignableFrom(listImplType)
                    ? HashSet.class : ArrayList.class);
        }
    }

    private static class ListBuilder {
        List toList(Map value, Class listImplType) {
            return createList(listImplType, isAbstract(listImplType), value.values());
        }

        List toList(Collection value, Class listImplType) {
            if (listImplType.isInstance(value)) {
                return (List) value;
            }
            return createList(listImplType, isAbstract(listImplType), value);
        }

        List toList(Object[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(int[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(long[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(double[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(short[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(char[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(byte[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(float[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(boolean[] array, Class listImplType) {
            boolean isDefault = isAbstract(listImplType);
            if (isDefault) {
                return ListUtil.toList(array);
            } else {
                return createList(listImplType, isDefault, ListUtil.toList(array));
            }
        }

        List toList(Object value, Class listImplType) {
            List list = createList(listImplType, isAbstract(listImplType));
            list.add(value);
            return list;
        }

        <T extends List> List createList(Class<T> listImplType, boolean isDefault, Collection c) {
            List list = createList(listImplType, isDefault);
            list.addAll(c);
            return list;
        }

        <T extends List> List createList(Class<T> listImplType, boolean isDefault) {
            return newInstance(isDefault, listImplType, ArrayList.class);
        }
    }

    private static class ArrayBuilder {
        <T> T[] toArray(List value, Class<T> componentType) {
            return (T[]) value.toArray(createArray(componentType, value.size()));
        }

        <T> T[] toArray(Collection value, Class<T> componentType) {
            return (T[]) value.toArray(createArray(componentType, value.size()));
        }

        <T> T[] toArray(Map value, Class<T> componentType) {
            return toArray(value.values(), componentType);
        }

        <T> T[] toArray(Object[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(int[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(long[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(double[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(byte[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(char[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(short[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(float[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(boolean[] value, Class<T> componentType) {
            int length = value.length;
            T[] array = createArray(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, TypeUtil.cast().toType(value[i], componentType));
            }
            return array;
        }

        <T> T[] toArray(Object value, Class<T> componentType) {
            T[] array = createArray(componentType, 1);
            Array.set(array, 0, value);
            return array;
        }

        <T> T[] createArray(Class<T> componentType, int length) {
            return (T[]) Array.newInstance(componentType, length);
        }
    }

    private enum StructureEnum {
        NONE, DEFAULT, ARRAY_LENGTH_2, ENTRY, LIST_SIZE_2
    }

    private static class MapBuilder {

        <T> T toMap(Object value, Class mapClass) {
            Map result = createMap(mapClass);
            BeanInfoUtil.getFieldDescriptorsMap(value.getClass()).forEach((name, desc) ->
                desc.ifGetterPresent(descriptor ->
                    result.put(name, descriptor.getValue(value, true))));
            return (T) result;
        }

        <T> T toMap(Map value, Class<T> mapClass) {
            if (mapClass.isInstance(value)) {
                return (T) value;
            }
            Map result = createMap(mapClass);
            result.putAll(value);
            return (T) result;
        }

        <T> T toMap(Object[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = Array.getLength(value); index < len; index++) {
                Object object = value[index];
                if (mode == StructureEnum.NONE && (mode = structureMode(object)) == StructureEnum.NONE) {
                    continue;
                }
                try {
                    putKeyValue(mode, result, object, index);
                } catch (NullPointerException e) {
                    continue;
                } catch (Exception e) {
                    index = -1;
                    mode = StructureEnum.DEFAULT;
                    result.clear();
                }
            }
            return (T) result;
        }

        <T> T toMap(int[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(long[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(double[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(char[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(byte[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(short[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(float[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(boolean[] value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            for (int index = 0, len = value.length; index < len; index++) {
                putKeyValue(mode, result, value[index], index);
            }
            return (T) result;
        }

        <T> T toMap(Collection value, Class<T> mapClass) {
            Map result = createMap(mapClass);
            StructureEnum mode = StructureEnum.NONE;
            int index = 0;
            for (Iterator iterator = IteratorUtil.of(value); iterator.hasNext(); index++) {
                Object object = iterator.next();
                if (mode == StructureEnum.NONE && (mode = structureMode(object)) == StructureEnum.NONE) {
                    continue;
                }
                try {
                    putKeyValue(mode, result, object, index);
                } catch (NullPointerException e) {
                    continue;
                } catch (Exception e) {
                    index = -1;
                    mode = StructureEnum.DEFAULT;
                    result.clear();
                }
            }
            return (T) result;
        }

        void putKeyValue(StructureEnum mode, Map result, Object object, int index) {
            switch (mode) {
                case ENTRY:
                    Map.Entry entry = (Map.Entry) object;
                    result.put(entry.getKey(), entry.getValue());
                    break;
                case ARRAY_LENGTH_2:
                    result.put(Array.get(object, 0), Array.get(object, 1));
                    break;
                case LIST_SIZE_2:
                    List list = (List) object;
                    result.put(list.get(0), list.get(1));
                    break;
                default:
                    result.put(index, object);
                    break;
            }
        }

        StructureEnum structureMode(Object object) {
            if (object == null) {
                return StructureEnum.NONE;
            } else if (object instanceof Map.Entry) {
                return StructureEnum.ENTRY;
            } else if (object.getClass().isArray() && Array.getLength(object) == 2) {
                return StructureEnum.ARRAY_LENGTH_2;
            } else if (object instanceof List && ListUtil.size((List) object) == 2) {
                return StructureEnum.LIST_SIZE_2;
            } else {
                return StructureEnum.DEFAULT;
            }
        }

        <T> T createMap(Class mapImplClass) {
            return newInstance(isAbstract(mapImplClass), mapImplClass, HashMap.class);
        }
    }


    private static boolean isAbstract(Class type) {
        if (type == null) {
            return true;
        }
        int modifier = type.getModifiers();
        return Modifier.isInterface(modifier) || Modifier.isAbstract(modifier);
    }

    private static <T> T newInstance(boolean isDefault, Class type, Class defaultType) {
        try {
            return (T) ConstructorUtil.newInstance((isDefault ? defaultType : type));
        } catch (Exception e) {
            return (T) ConstructorUtil.newInstance(defaultType);
        }
    }
}
