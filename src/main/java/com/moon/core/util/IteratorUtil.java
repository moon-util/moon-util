package com.moon.core.util;

import com.moon.core.beans.BeanInfoUtil;
import com.moon.core.beans.FieldDescriptor;
import com.moon.core.enums.ArraysEnum;
import com.moon.core.enums.Collects;
import com.moon.core.io.FileUtil;
import com.moon.core.lang.EnumUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.function.*;
import com.moon.core.util.iterator.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.*;

import static com.moon.core.io.FileUtil.getInputStream;
import static com.moon.core.lang.ThrowUtil.doThrow;
import static com.moon.core.util.iterator.EmtpyIterator.EMPTY;

/**
 * 通用迭代器
 * <p>
 * 【注意】：
 * <p>
 * 此类构造出来的大多数自定义迭代器没有实现{@link Iterator#remove()}方法，
 * <p>
 * 如果在运行是调用将会抛出{@link UnsupportedOperationException}异常；
 * <p>
 * 对于本身就是{@link Iterable}的集合，此类将原生调用其{@link Iterable#iterator()}方法，
 * <p>
 * 此时{@link Iterator#remove()}方法根据具体集合实现。
 *
 * @author benshaoye
 * @date 2018/9/11
 */
public final class IteratorUtil {

    private IteratorUtil() { ThrowUtil.noInstanceError(); }

    /*
     * ----------------------------------------------------------------------------
     * array iterator: 数组迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 获取一个遍历指定类型数组的迭代器
     *
     * @param values
     * @param <T>
     */
    public static <T> Iterator<T> of(T... values) {
        return values == null ? EMPTY : new ObjectsIterator<>(values);
    }

    /**
     * 获取 byte[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Byte> of(byte... values) { return values == null ? EMPTY : new BytesIterator(values); }

    /**
     * 获取 short[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Short> of(short... values) { return values == null ? EMPTY : new ShortsIterator(values); }

    /**
     * 获取 char[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Character> of(char... values) { return values == null ? EMPTY : new CharsIterator(values); }

    /**
     * 获取 int[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Integer> of(int... values) { return values == null ? EMPTY : new IntsIterator(values); }

    /**
     * 获取 long[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Long> of(long... values) { return values == null ? EMPTY : new LongsIterator(values); }

    /**
     * 获取 float[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Float> of(float... values) { return values == null ? EMPTY : new FloatsIterator(values); }

    /**
     * 获取 double[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Double> of(double... values) { return values == null ? EMPTY : new DoublesIterator(values); }

    /**
     * 获取 boolean[] 类型数组的迭代器
     *
     * @param values
     */
    public static Iterator<Boolean> of(boolean... values) {
        return values == null ? EMPTY : new BooleansIterator(values);
    }

    /*
     * ----------------------------------------------------------------------------
     * string iterator: 字符串字符迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 返回字符串的字符迭代器
     *
     * @param value
     */
    public static Iterator<Character> ofChars(CharSequence value) {
        return value == null ? EMPTY : new CharsIterator(value);
    }

    /*
     * ----------------------------------------------------------------------------
     * file line iterator: 文本行迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 返回文本文件行迭代器
     *
     * @param path
     */
    public static Iterator<String> ofLines(CharSequence path) { return path == null ? EMPTY : new LinesIterator(path); }

    /**
     * 从 Reader 中每次读取一行文本
     *
     * @param reader
     * @return
     */
    public static Iterator<String> ofLines(Reader reader) { return reader == null ? EMPTY : new LinesIterator(reader); }

    /**
     * 从 InputStream 中按默认字符编码（UTF-8）格式每次读取一行文本
     *
     * @param is
     * @return
     */
    public static Iterator<String> ofLines(InputStream is) { return is == null ? EMPTY : new LinesIterator(is); }

    /**
     * 从 InputStream 中按 charset 格式每次读取一行文本
     *
     * @param is
     * @param charset
     * @return
     */
    public static Iterator<String> ofLines(InputStream is, String charset) {
        return is == null ? EMPTY : new LinesIterator(is, charset);
    }

    /**
     * 从 InputStream 中按 charset 格式每次读取一行文本
     *
     * @param is
     * @param charset
     * @return
     */
    public static Iterator<String> ofLines(InputStream is, Charset charset) {
        return is == null ? EMPTY : new LinesIterator(is, charset);
    }

    /**
     * 获取一个文本文件读取迭代器，可用于常用的txt、json、xml等文本文件读取；
     * 迭代器每次返回一行数据，直到文本结尾，对象会自动关闭文件流
     *
     * @param file
     */
    public static Iterator<String> ofLines(File file) { return file == null ? EMPTY : new LinesIterator(file); }

    /*
     * ----------------------------------------------------------------------------
     * io iterator: I/O 迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 文件流迭代读取器
     * 每次将读取的字节放入数组 buffer 中，并返回读取到的长度
     *
     * @param filepath
     * @param buffer
     */
    public static Iterator<Integer> of(String filepath, byte[] buffer) {
        return filepath == null ? EMPTY : new FileStreamIterator(filepath, buffer);
    }

    /**
     * 文件流迭代读取器
     * 每次将读取的字节放入数组 buffer 中，并返回读取到的长度
     *
     * @param file
     * @param buffer
     */
    public static Iterator<Integer> of(File file, byte[] buffer) {
        return file == null ? EMPTY : new FileStreamIterator(file, buffer);
    }

    /**
     * 文件流迭代读取器
     * 每次将读取的字节放入数组 buffer 中，并返回读取到的长度
     *
     * @param is
     * @param buffer
     */
    public static Iterator<Integer> of(InputStream is, byte[] buffer) {
        return is == null ? EMPTY : new FileStreamIterator(is, buffer);
    }

    /*
     * ----------------------------------------------------------------------------
     * java bean iterator: 实体字段迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 将 JavaBean 中每个属性认为是一个字段进行迭代
     *
     * @param object
     * @return
     */
    public static Iterator<Map.Entry<String, FieldDescriptor>> ofFields(Object object) {
        return object == null ? EMPTY : of(BeanInfoUtil.getFieldDescriptorsMap(object.getClass()));
    }

    /*
     * ----------------------------------------------------------------------------
     * collection iterator: 集合迭代器
     * ----------------------------------------------------------------------------
     */

    public static <T> Iterator<T> of(Iterator<T> iterator) { return iterator == null ? EMPTY : iterator; }

    /**
     * 返回 List 集合的迭代器
     *
     * @param list
     * @param <T>
     */
    public static <T> Iterator<T> of(List<T> list) { return list == null ? EMPTY : list.iterator(); }

    /**
     * 返回 Collection 集合的迭代器
     *
     * @param c
     * @param <T>
     */
    public static <T> Iterator<T> of(Collection<T> c) { return c == null ? EMPTY : c.iterator(); }

    /**
     * 返回 Iterable 集合的迭代器
     *
     * @param iterable
     * @param <T>
     */
    public static <T> Iterator<T> of(Iterable<T> iterable) {
        return iterable == null ? EMPTY : iterable.iterator();
    }

    /**
     * 返回 Map 集合迭代器
     *
     * @param map
     * @param <K>
     * @param <V>
     */
    public static <K, V> Iterator<Map.Entry<K, V>> of(Map<K, V> map) {
        return map == null ? EMPTY : map.entrySet().iterator();
    }

    /**
     * 返回 Enumeration 迭代器
     *
     * @param enumeration
     * @param <T>
     */
    public static <T> Iterator<T> of(Enumeration<T> enumeration) {
        return enumeration == null ? EMPTY : new EnumerationIterator<>(enumeration);
    }

    /**
     * 返回 ResultSet 迭代器
     *
     * @param resultSet
     * @param <T>
     */
    public static <T> Iterator<ResultSet> of(ResultSet resultSet) {
        return resultSet == null ? EMPTY : new ResultSetIterator(resultSet);
    }

    /*
     * ----------------------------------------------------------------------------
     * enum iterator: 枚举迭代器
     * ----------------------------------------------------------------------------
     */

    /**
     * 返回类字段信息描述迭代器;
     * 迭代器每次返回一个枚举；
     *
     * @param clazz
     * @param <T>
     */
    public static <T extends Enum<T>> Iterator<T> of(Class<T> clazz) {
        return clazz != null && clazz.isEnum() ? of(clazz.getEnumConstants()) : EMPTY;
    }

    public static Iterator ofAny(Object value) {
        if (value == null) { return EMPTY; }
        if (value instanceof Iterable) { return of((Iterable) value); }
        if (value instanceof Map) { return of((Map) value); }
        if (value instanceof Iterator) { return (Iterator) value; }
        Class type = value.getClass();
        if (type.isEnum()) { return of(type); }
        if (type.isArray()) {
            if (value instanceof Object[]) { return of((Object[]) value); }
            if (value instanceof int[]) { return of((int[]) value); }
            if (value instanceof long[]) { return of((double[]) value); }
            if (value instanceof double[]) { return of((double[]) value); }
            if (value instanceof byte[]) { return of((byte[]) value); }
            if (value instanceof char[]) { return of((char[]) value); }
            if (value instanceof short[]) { return of((short[]) value); }
            if (value instanceof boolean[]) { return of((boolean[]) value); }
        }
        if (value instanceof CharSequence) { return ofChars((CharSequence) value); }
        if (value instanceof Enumeration) { return of((Enumeration) value); }
        if (value instanceof ResultSet) { return of((ResultSet) value); }
        if (value instanceof File) { return ofLines((File) value); }
        return ofFields(value);
    }

    /*
     * ----------------------------------------------------------------------------
     * for each(object)
     * ----------------------------------------------------------------------------
     */

    public final static void forEachAny(Object data, IntBiConsumer consumer) {
        if (data instanceof Iterable) {
            forEach((Iterable) data, consumer);
        } else if (data instanceof Map) {
            forEach(((Map) data).entrySet(), consumer);
        } else if (data instanceof Iterator) {
            forEach((Iterator) data, consumer);
        } else if (data == null) {
            return;
        } else if (data instanceof ResultSet) {
            forEach((ResultSet) data, consumer);
        } else {
            Class type = data.getClass();
            if (type.isArray()) {
                ArraysEnum.getOrObjects(data).forEach(data, consumer);
            } else {
                forEachFields(data, consumer);
            }
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * for each(JavaBean)
     * ----------------------------------------------------------------------------
     */

    public final static void forEachFields(Object bean, IntBiConsumer consumer) {
        if (bean != null) {
            IntAccessor indexer = IntAccessor.of();
            BeanInfoUtil.getFieldDescriptorsMap(bean.getClass()).forEach((name, desc) ->
                consumer.accept(desc.getValueIfPresent(bean, true), indexer.getAndIncrement()));
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * array for each(item)
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(boolean[] array, BooleanConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(double[] array, DoubleConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(float[] array, FloatConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(long[] array, LongConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(int[] array, IntConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(char[] array, CharConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(short[] array, ShortConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @return
     */
    public static void forEach(byte[] array, ByteConsumer consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param array
     * @param <T>
     */
    public static <T> void forEach(T[] array, Consumer<? super T> consumer) {
        int length = array == null ? 0 : array.length;
        for (int i = 0; i < length; i++) {
            consumer.accept(array[i]);
        }
    }

    /**
     * 遍历枚举类
     *
     * @param enumType
     * @param consumer
     * @param <T>
     */
    public static <T extends Enum<T>> void forEach(Class<T> enumType, Consumer<? super T> consumer) {
        forEach(EnumUtil.values(enumType), consumer);
    }

    /*
     * ----------------------------------------------------------------------------
     * array for each count
     * ----------------------------------------------------------------------------
     */

    public static void forEach(final int count, IntConsumer consumer) {
        for (int i = 0; i < count; i++) {
            consumer.accept(i);
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * array for each(item, index)
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     * @return
     */
    public static void forEach(int[] array, IntIntConsumer consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     * @return
     */
    public static void forEach(long[] array, IntLongConsumer consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     * @return
     */
    public static void forEach(double[] array, IntDoubleConsumer consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历处理
     *
     * @param array
     * @param consumer 处理对象
     * @return
     */
    public static <T> void forEach(T[] array, IntBiConsumer<? super T> consumer) {
        for (int i = 0, length = array == null ? 0 : array.length; i < length; i++) {
            consumer.accept(array[i], i);
        }
    }

    /**
     * 遍历枚举类
     *
     * @param enumType
     * @param consumer
     * @param <T>
     */
    public static <T extends Enum<T>> void forEach(Class<T> enumType, IntBiConsumer<? super T> consumer) {
        forEach(EnumUtil.values(enumType), consumer);
    }

    /*
     * ----------------------------------------------------------------------------
     * collect for each
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历
     *
     * @param consumer 处理对象
     * @param list
     * @param <T>
     * @return
     */
    public static <T> void forEach(List<T> list, Consumer<? super T> consumer) {
        if (list != null) { list.forEach(consumer); }
    }

    /**
     * 遍历 Collection
     *
     * @param consumer 处理对象
     * @param c
     * @param <T>
     * @return
     */
    public static <T> void forEach(Collection<T> c, Consumer<? super T> consumer) {
        if (c != null) { c.forEach(consumer); }
    }

    /**
     * 遍历 Iterable
     *
     * @param consumer 处理对象
     * @param c
     * @param <T>
     * @return
     */
    public static <T> void forEach(Iterable<T> c, Consumer<? super T> consumer) {
        if (c != null) { c.forEach(consumer); }
    }

    /**
     * 集合索引遍历
     *
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> void forEach(Iterable<T> list, IntBiConsumer<? super T> consumer) {
        if (list != null) {
            int i = 0;
            for (T item : list) {
                consumer.accept(item, i);
                i++;
            }
        }
    }

    /**
     * 遍历
     *
     * @param iterator
     * @param consumer
     * @param <T>
     */
    public static <T> void forEach(Iterator<T> iterator, Consumer<? super T> consumer) {
        if (iterator != null) {
            iterator.forEachRemaining(consumer);
        }
    }

    public static <T> void forEach(Iterator<T> iterator, IntBiConsumer<? super T> consumer) {
        if (iterator != null) {
            for (int i = 0; iterator.hasNext(); i++) {
                consumer.accept(iterator.next(), i);
            }
        }
    }

    /**
     * 遍历 Map
     *
     * @param consumer 处理对象
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> void forEach(Map<K, V> map, Consumer<Map.Entry<K, V>> consumer) {
        if (map != null) { map.entrySet().forEach(consumer); }
    }

    /**
     * 遍历 Map
     *
     * @param consumer 处理对象
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> void forEach(Map<K, V> map, BiConsumer<? super K, ? super V> consumer) {
        if (map != null) { map.forEach(consumer); }
    }

    /**
     * 遍历 Enumeration
     *
     * @param consumer 处理对象
     * @param e
     * @param <T>
     * @return
     */
    public static <T> void forEach(Enumeration<T> e, Consumer<? super T> consumer) {
        if (e != null) { while (e.hasMoreElements()) { consumer.accept(e.nextElement()); } }
    }

    /**
     * 遍历 ResultSet
     *
     * @param resultSet
     * @param consumer
     */
    public static void forEach(ResultSet resultSet, Consumer<? super ResultSet> consumer) {
        if (resultSet != null) { forEach(of(resultSet), consumer); }
    }

    /**
     * 遍历 ResultSet
     *
     * @param resultSet
     * @param consumer
     */
    public static void forEach(ResultSet resultSet, IntBiConsumer<? super ResultSet> consumer) {
        if (resultSet != null) { forEach(of(resultSet), consumer); }
    }

    /*
     * ----------------------------------------------------------------------------
     * io for each
     * ----------------------------------------------------------------------------
     */

    /**
     * 遍历处理文本文件每一行数据
     *
     * @param consumer 处理对象
     * @param file
     */
    public static void forEachLines(File file, Consumer<String> consumer) {
        ofLines(file).forEachRemaining(consumer);
    }

    public static void forEachLines(String filename, Consumer<String> consumer) {
        ofLines(filename).forEachRemaining(consumer);
    }

    /**
     * 文件流读取和处理
     *
     * @param consumer 处理对象，接受一个参数，代表每次读取 byte 长度
     * @param filepath
     * @param buffer
     */
    public static void forEach(String filepath, byte[] buffer, IntConsumer consumer) {
        forEach(FileUtil.getInputStream(filepath), buffer, consumer);
    }

    /**
     * 文件流读取和处理
     *
     * @param consumer 处理对象，接受一个参数，代表每次读取 byte 长度
     * @param file
     * @param buffer
     */
    public static void forEach(File file, byte[] buffer, IntConsumer consumer) {
        forEach(getInputStream(file), buffer, consumer);
    }

    public static void forEachLines(Reader reader, Consumer<String> consumer) {
        ofLines(reader).forEachRemaining(consumer);
    }

    public static void forEach(Reader reader, char[] buffer, Consumer<Integer> consumer) {
        if (reader != null) {
            try {
                int length = buffer.length;
                int limit = reader.read(buffer, 0, length);
                while (limit >= 0) {
                    consumer.accept(limit);
                    limit = reader.read(buffer, 0, length);
                }
            } catch (IOException e) {
                doThrow(e);
            }
        }
    }

    /**
     * 流读取和处理
     *
     * @param consumer    处理对象
     * @param inputStream
     * @param buffer
     */
    public static void forEach(InputStream inputStream, byte[] buffer, IntConsumer consumer) {
        try {
            final int length = buffer.length;
            boolean whiling;
            int limit;
            do {
                limit = inputStream.read(buffer, 0, length);
                if (whiling = (limit >= 0)) { consumer.accept(limit); }
            } while (whiling);
        } catch (Exception e) {
            doThrow(e);
        }
    }

    /*
     * ----------------------------------------------------------------------------
     * split
     * ----------------------------------------------------------------------------
     */

    /**
     * 集合拆分器
     * <p>
     * 默认拆分后每个容器里有十六个元素
     *
     * @param c
     * @param <E>
     * @param <C>
     * @return
     */
    public static <E, C extends Collection<E>> Iterator<C> split(C c) {
        return split(c, CollectSplitter.DEFAULT_SPLIT_COUNT);
    }

    /**
     * 集合拆分器
     * <p>
     * 将集合拆分成指定大小的若干个相同类型集合;
     * <p>
     * 不足个数的统一放入最后一个集合
     * <p>
     * 默认拆分后每个容器里有十六个元素
     *
     * @param c
     * @param size 指定拆分大小
     * @param <E>
     * @param <C>
     * @return
     */
    public static <E, C extends Collection<E>> Iterator<C> split(C c, int size) {
        return c == null ? EMPTY : new CollectSplitter<>(c, size);
    }

    /**
     * 集合拆分处理器
     *
     * @param c
     * @param size
     * @param consumer
     * @param <E>
     * @param <C>
     */
    public static <E, C extends Collection<E>> void splitter(C c, int size, Consumer<? super Collection<E>> consumer) {
        split(c, size).forEachRemaining(consumer);
    }

    /**
     * 集合拆分处理器
     *
     * @param c
     * @param consumer
     * @param <E>
     * @param <C>
     */
    public static <E, C extends Collection<E>> void splitter(C c, Consumer<? super Collection<E>> consumer) {
        split(c).forEachRemaining(consumer);
    }

    /*
     * ----------------------------------------------------------------------------
     * group by
     * ----------------------------------------------------------------------------
     */

    /**
     * 集合分组
     *
     * @param list     集合
     * @param function 分组键
     * @param <K>      键类型
     * @param <E>      集合单项类型
     * @param <L>      List 类型
     * @return
     */
    public static <K, E, L extends List<E>> Map<K, List<E>> groupBy(L list, Function<? super E, ? extends K> function) {
        return GroupUtil.groupAsList(list, function);
    }

    public static <K, E, S extends Set<E>> Map<K, Set<E>> groupBy(S set, Function<? super E, ? extends K> function) {
        return GroupUtil.groupAsSet(set, function);
    }

    public static <K, E, C extends Collection<E>> Map<K, Collection<E>> groupBy(C collect, Function<? super E, ? extends K> function) {
        return GroupUtil.groupBy(collect, function);
    }

    public static <K, E, C extends Collection<E>, CR extends Collection<E>>

    Map<K, CR> groupBy(C collect, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier) {
        return GroupUtil.groupBy(collect, function, groupingSupplier);
    }

    /*
     * ----------------------------------------------------------------------------
     * filter
     * ----------------------------------------------------------------------------
     */

    /**
     * @param list
     * @param tester
     * @param <E>
     * @param <L>
     * @return
     */
    public static <E, L extends List<E>> List<E> filter(L list, Predicate<? super E> tester) {
        return FilterUtil.filter(list, tester);
    }

    public static <E, S extends Set<E>> Set<E> filter(S set, Predicate<? super E> tester) {
        return FilterUtil.filter(set, tester);
    }

    /**
     * @param collect
     * @param tester
     * @param resultContainerSupplier 符合过滤条件项容器构造器
     * @param <E>
     * @param <C>
     * @param <CR>
     * @return
     */
    public static <E, C extends Collection<E>, CR extends Collection<E>>

    CR filter(C collect, Predicate<? super E> tester, Supplier<CR> resultContainerSupplier) {
        return FilterUtil.filter(collect, tester, resultContainerSupplier);
    }

    /**
     * @param collect
     * @param tester
     * @param toResultContainer 符合过滤条件的容器
     * @param <E>
     * @param <C>
     * @param <CR>
     * @return 返回提供的容器 toResultContainer
     */
    public static <E, C extends Collection<E>, CR extends Collection<E>>

    CR filter(C collect, Predicate<? super E> tester, CR toResultContainer) {
        return FilterUtil.filter(collect, tester, toResultContainer);
    }

    /*
     * ----------------------------------------------------------------------------
     * map
     * ----------------------------------------------------------------------------
     */

    public static <E, T, L extends List<E>> List<T> map(L list, Function<? super E, T> function) {
        final IntFunction supplier = Collects.getAsDeduceOrDefault(list, Collects.ArrayList);
        return (List) mapTo(list, function, supplier);
    }

    public static <E, T, S extends Set<E>> Set<T> map(S set, Function<? super E, T> function) {
        final IntFunction supplier = Collects.getAsDeduceOrDefault(set, Collects.HashSet);
        return (Set) mapTo(set, function, supplier);
    }

    public static <E, T, C extends Collection<E>> Collection<T> map(C collect, Function<? super E, T> function) {
        final IntFunction supplier = Collects.getAsDeduce(collect);
        return mapTo(collect, function, supplier);
    }

    public static <E, T, C extends Collection<E>, CR extends Collection<T>>

    CR mapTo(C collect, Function<? super E, T> function, IntFunction<CR> containerSupplier) {
        return mapTo(collect, function, containerSupplier.apply(collect == null ? 0 : collect.size()));
    }

    public static <E, T, C extends Collection<E>, CR extends Collection<T>>

    CR mapTo(C collect, Function<? super E, T> function, CR container) {
        if (collect != null) {
            for (E item : collect) {
                container.add(function.apply(item));
            }
        }
        return container;
    }
}
