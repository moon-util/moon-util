package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.*;
import java.util.function.Supplier;

import static com.moon.core.util.TypeUtil.cast;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class MapUtil {
    private MapUtil() {
        ThrowUtil.noInstanceError();
    }

    public static Map empty() {return EmptyHashMap.EMPTY_MAP;}

    /*
     * ---------------------------------------------------------------------------------
     * of hash map
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> HashMap<K, V> ofHashMap() { return new HashMap<>(); }

    public static <K, V> HashMap<K, V> ofHashMap(int capacity) { return new HashMap<>(capacity); }

    public static <K, V> HashMap<K, V> ofHashMap(Map<K, V> map) { return new HashMap<>(map); }

    public static <K, V> HashMap<K, V> ofHashMap(Map<K, V> map, Map<K, V>... maps) {
        return putAll(putAll(ofHashMap((maps.length + 1) * 16), map), maps);
    }

    public static <K, V> Map<K, V> ofHashMapIfNull(Map<K, V> map) { return map == null ? new HashMap<>(16) : map; }

    /*
     * ---------------------------------------------------------------------------------
     * of linked hash map
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> LinkedHashMap<K, V> ofLinkedHashMap() { return new LinkedHashMap<>(); }

    public static <K, V> LinkedHashMap<K, V> ofLinkedHashMap(int capacity) { return new LinkedHashMap<>(capacity); }

    public static <K, V> LinkedHashMap<K, V> ofLinkedHashMap(Map<K, V> map) { return new LinkedHashMap<>(map); }

    public static <K, V> LinkedHashMap<K, V> ofLinkedHashMap(Map<K, V> map, Map<K, V>... maps) {
        return putAll(putAll(ofLinkedHashMap((maps.length + 1) * 16), map), maps);
    }

    public static <K, V> LinkedHashMap<K, V> ofLinkedHashMapIfNull(Map<K, V> map) {
        return map == null ? new LinkedHashMap<>() : null;
    }

    /*
     * ---------------------------------------------------------------------------------
     * assertions
     * ---------------------------------------------------------------------------------
     */

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    public static int size(Map map) {
        return map == null ? 0 : map.size();
    }

    public static int sizeByObject(Object map) {
        return map == null ? 0 : ((Map) map).size();
    }

    public static int sizeOfAll(Map... maps) {
        int size = 0, i = 0;
        for (; i < maps.length; ) {
            size += size(maps[i++]);
        }
        return size;
    }

    /*
     * ---------------------------------------------------------------------------------
     * operators
     * ---------------------------------------------------------------------------------
     */

    /**
     * @param map
     * @param key
     * @param <T>
     * @param <E>
     */
    public static <T, E> E get(Map<T, E> map, T key) {
        return map == null ? null : map.get(key);
    }

    /**
     * @param map
     * @param key
     */
    public static Object getByObject(Object map, Object key) {
        return map == null ? null : ((Map) map).get(key);
    }

    /**
     * 忽略类型兼容，数据一定能放进 map 里面；
     * 如果数据类型与 map 不一致，可能会导致处理返回数据的过程中出现异常；
     *
     * @param map
     * @param key
     * @param value
     * @param <K>
     * @param <V>
     */
    public static <K, V> Map<K, V> put(Map<K, V> map, Object key, Object value) {
        map = ofHashMapIfNull(map);
        map.put((K) key, (V) value);
        return map;
    }

    /**
     * 忽略类型兼容
     *
     * @param map
     * @param key
     * @param value
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> putToObject(Object map, Object key, Object value) {
        return put((Map) map, key, value);
    }

    /**
     * 忽略类型兼容，elements 里面的数据一定能放进 map；
     * 如果 elements 数据类型与 map 不一致，可能会导致处理返回数据的过程中出现异常；
     *
     * @param map
     * @param elements
     * @param <K>
     * @param <V>
     */
    public static <K, V, M extends Map<K, V>> M putAll(M map, Map elements) {
        map.putAll(elements);
        return map;
    }

    public static <K, V, M extends Map<K, V>> M putAll(M map, Map<? extends K, ? extends V>... maps) {
        for (int i = 0; i < maps.length; i++) {
            map.putAll(maps[i]);
        }
        return map;
    }

    public static Object[] toArray(Map map) {
        return map == null ? null : map.values().toArray();
    }

    /*
     * ---------------------------------------------------------------------------------
     * iterators
     * ---------------------------------------------------------------------------------
     */

    public static <K, V> Iterator<Map.Entry<K, V>> iterator(Map<K, V> map) {
        return IteratorUtil.of(map);
    }

    public static <K, V> Set<K> keys(Map<K, V> map) {
        return map == null ? null : map.keySet();
    }

    public static <K, V> Collection<V> values(Map<K, V> map) {
        return map == null ? null : map.values();
    }

    public static <K, V> Set<Map.Entry<K, V>> entries(Map<K, V> map) {
        return map == null ? null : map.entrySet();
    }

    /*
     * ---------------------------------------------------------------------------------
     * get to type or basic value
     * ---------------------------------------------------------------------------------
     */

    /**
     * 按指定类型获取 map 的值
     *
     * @param map
     * @param key
     * @param clazz
     * @param <T>
     * @param <E>
     * @param <C>
     */
    public static <T, E, C> C getOfType(Map<T, E> map, T key, Class<C> clazz) {
        if (map == null) {
            return null;
        }

        E e = map.get(key);

        if (e == null) {
            return null;
        }

        return cast().toType(e, clazz);
    }

    public static <K> String getString(Map<K, ?> map, K key) {
        return map == null ? null : cast().toString(map.get(key));
    }

    public static <K> Boolean getBoolean(Map<K, ?> map, K key) {
        return map == null ? null : cast().toBoolean(map.get(key));
    }

    public static <K> boolean getBooleanValue(Map<K, ?> map, K key) {
        return map == null ? false : cast().toBooleanValue(map.get(key));
    }

    public static <K> Double getDouble(Map<K, ?> map, K key) {
        return map == null ? null : cast().toDouble(map.get(key));
    }

    public static <K> double getDoubleValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toDoubleValue(map.get(key));
    }

    public static <K> Float getFloat(Map<K, ?> map, K key) {
        return map == null ? null : cast().toFloat(map.get(key));
    }

    public static <K> float getFloatValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toFloatValue(map.get(key));
    }

    public static <K> Long getLong(Map<K, ?> map, K key) {
        return map == null ? null : cast().toLong(map.get(key));
    }

    public static <K> long getLongValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toLongValue(map.get(key));
    }

    public static <K> Integer getInteger(Map<K, ?> map, K key) {
        return map == null ? null : cast().toInteger(map.get(key));
    }

    public static <K> int getIntValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toIntValue(map.get(key));
    }

    public static <K> Short getShort(Map<K, ?> map, K key) {
        return map == null ? null : cast().toShort(map.get(key));
    }

    public static <K> short getShortValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toShortValue(map.get(key));
    }

    public static <K> Byte getByte(Map<K, ?> map, K key) {
        return map == null ? null : cast().toByte(map.get(key));
    }

    public static <K> byte getByteValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toByteValue(map.get(key));
    }

    public static <K> Character getCharacter(Map<K, ?> map, K key) {
        return map == null ? null : cast().toCharacter(map.get(key));
    }

    public static <K> char getCharValue(Map<K, ?> map, K key) {
        return map == null ? 0 : cast().toCharValue(map.get(key));
    }

    /*
     * ---------------------------------------------------------------------------------
     * default getSheet invalid
     * ---------------------------------------------------------------------------------
     */

    public static <K> char getOrDefault(Map<K, ?> map, K key, char defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : ((value = map.get(key)) instanceof Character
            ? ((Character) value).charValue()
            : defaultVal);
    }

    public static <K> double getOrDefault(Map<K, ?> map, K key, double defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : ((value = map.get(key)) instanceof Number
            ? ((Number) value).doubleValue()
            : defaultVal);
    }

    public static <K> float getOrDefault(Map<K, ?> map, K key, float defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : ((value = map.get(key)) instanceof Number
            ? ((Number) value).floatValue()
            : defaultVal);
    }

    public static <K> long getOrDefault(Map<K, ?> map, K key, long defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : ((value = map.get(key)) instanceof Number
            ? ((Number) value).longValue()
            : defaultVal);
    }

    public static <K> int getOrDefault(Map<K, ?> map, K key, int defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : ((value = map.get(key)) instanceof Number
            ? ((Number) value).intValue()
            : defaultVal);
    }

    public static <K> short getOrDefault(Map<K, ?> map, K key, short defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : ((value = map.get(key)) instanceof Number
            ? ((Number) value).shortValue()
            : defaultVal);
    }

    public static <K> byte getOrDefault(Map<K, ?> map, K key, byte defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : ((value = map.get(key)) instanceof Number
            ? ((Number) value).byteValue()
            : defaultVal);
    }

    public static <K> boolean getOrDefault(Map<K, ?> map, K key, boolean defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : (value = map.get(key)) instanceof Boolean
            ? (Boolean) value
            : defaultVal;
    }

    /**
     * Object value = map.getSheet(key);
     * 如果 value 不是一个 boolean 型数据或 map == null，返回 true，否则返回 value
     *
     * @param map
     * @param key
     * @return
     */
    public static <K> boolean getOrTrue(Map<K, ?> map, K key) {
        return getOrDefault(map, key, true);
    }

    /**
     * Object value = map.getSheet(key);
     * 如果 value 不是一个 boolean 型数据或 map == null，返回 false，否则返回 value
     *
     * @param map
     * @param key
     * @return
     */
    public static <K> boolean getOrFalse(Map<K, ?> map, K key) {
        return getOrDefault(map, key, false);
    }

    public static <K> String getOrDefault(Map<K, ?> map, K key, String defaultVal) {
        Object value;
        return map == null
            ? defaultVal
            : (value = map.get(key)) instanceof CharSequence
            ? value.toString()
            : defaultVal;
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultVal) {
        return map == null ? defaultVal : map.getOrDefault(key, defaultVal);
    }

    public static <K, V> V getOrElse(Map<K, V> map, K key, Supplier<V> supplier) {
        V value;
        return map == null ? supplier.get() :
            (value = map.get(key)) == null
                ? supplier.get() : value;
    }
}
