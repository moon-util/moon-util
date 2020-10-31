package com.moon.core.util;

import com.moon.core.io.FileUtil;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.SystemUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.FinalAccessor;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 仿{@code JavaScript#localStorage}实现，可以简单实现键值对本地文件存储；
 * <p>
 * 现在{@code java}存储媒介已经非常多并且非常成熟，{@link LocalStorage}通常并不是合适的选择，
 * <p>
 * 所以任何时候还是推荐尽量使用其他管理更为优秀的存储介质，如数据库、Redis、Caffeine 等。
 * <p>
 * <strong>【使用提醒和注意】：</strong>
 * <ol>
 * <li>{@link LocalStorage}实现了相对闭环的管理机制，但它不能成为一个解决方案，只在某些场景提供简便的操作；</li>
 * <li>存储原则上无上限，与本地磁盘空间有关；</li>
 * <li>建议存储小的键值对，较少磁盘 IO；</li>
 * <li>小存储会在内存中缓存{@link LocalStorage#valueCached}，也可以手动释放{@link LocalStorage#clearMemoryCached()}；</li>
 * <li>清除缓存的时候会将存储目录{@link Factory#root}下 <strong>所有内容和目录都删除（包括系统原来就存在的文件）</strong>，
 *     所以在存储根目录应该是空目录（不存在的根目录会自动创建）</li>
 * <li>为了尽量规避删除系统原有文件，{@link LocalStorage}在根目录做了一层隔离{@link LocalStorage#ISOLATION}</li>
 * </ol>
 *
 * @author moonsky
 */
public class LocalStorage<T> implements Storage<String, T> {

    private final static String ISOLATION = ".JAVAIMOONUTILLOCALSTORAGEISOLATION";
    private final static char CHAR_HYPHEN = '-';
    private final static char CHAR_WAVE = '~';
    private final static short FACTOR = 19968;

    private final static Map<String, Object> FACTORY_MAP = new ConcurrentHashMap<>();
    private final static Function<byte[], Object> DESERIALIZER = IOUtil::deserialize;
    private final static Function SERIALIZER = IOUtil::serialize;

    private final static String DEFAULT_ROOT;
    private final static LocalStorage DEFAULT;
    private final static Factory FACTORY;

    static {
        String tempRoot = SystemUtil.getTempDir();
        DEFAULT_ROOT = absolutePath(tempRoot, ISOLATION);
        FACTORY = ofFactory(DEFAULT_ROOT);
        DEFAULT = FACTORY.create(null);
    }

    private enum Holder {
        HOLDER
    }

    private static boolean isPlaceholder(Object data) { return data == Holder.HOLDER; }

    private static String absolutePath(String root, String namespace) {
        return new File(root, namespace).getAbsolutePath();
    }

    private static String useRootPath(String root) {
        if (StringUtil.isBlank(root)) {
            return DEFAULT_ROOT;
        }
        String trimmed = root.trim();
        return trimmed.endsWith(ISOLATION) ? trimmed : new File(trimmed, ISOLATION).getAbsolutePath();
    }

    private static String useNamespace(String namespace) {
        namespace = StringUtil.defaultIfBlank(namespace, "default");
        namespace = StringUtil.replace(namespace, '\\', CHAR_WAVE);
        return StringUtil.replace(namespace, '/', CHAR_HYPHEN);
    }

    private static void placedRootPath(String formattedRootPath) {
        if (!FACTORY_MAP.containsKey(formattedRootPath)) {
            FACTORY_MAP.put(formattedRootPath, Holder.HOLDER);
        }
    }

    private final String namespace;
    private final Map valueCached;
    private int cacheLengthLimit = 10240;

    /**
     * 构造器
     *
     * @param root      存储根路径
     * @param namespace 命名路径
     */
    protected LocalStorage(String root, String namespace) {
        this(root, namespace, new ConcurrentHashMap());
    }

    /**
     * 提供自定义本地缓存构造器，如需自定义实现{@link #valueCached}可以通过传入{@code null}
     * 并覆盖方法{@link #getValueCached()}实现
     *
     * @param root        存储根路径
     * @param namespace   命名路径
     * @param valueCached 自定义 valueCached
     */
    protected LocalStorage(String root, String namespace, Map<String, T> valueCached) {
        String r = useRootPath(root);
        String ns = useNamespace(namespace);
        String localNs = absolutePath(r, ns);
        this.valueCached = valueCached;
        this.namespace = localNs;
        placedRootPath(r);
    }

    public static <T extends Serializable> LocalStorage<T> of() { return DEFAULT; }

    public static <T extends Serializable> LocalStorage<T> of(String namespace) {
        return FACTORY.create(namespace);
    }

    /**
     * 自定义存储位置
     *
     * @param location 存储位置
     * @param <T>      数据类型
     *
     * @return Storage 建造工厂
     */
    public static <T extends Serializable> Factory<T> ofFactory(String location) {
        final String rootPath = useRootPath(location);
        Object cached = FACTORY_MAP.get(rootPath);
        if (cached == null || isPlaceholder(cached)) {
            cached = new Factory<>(rootPath);
            FACTORY_MAP.put(rootPath, cached);
        }
        return (Factory<T>) cached;
    }

    @Override
    public void set(String key, T value) { set(key, value, false); }

    public void set(String key, T value, boolean forceCache) {
        if (isPlaceholder(value)) {
            remove(key);
            return;
        } else {
            doStorage(key, value, false);
        }
    }

    private void doStorage(String key, T value, boolean forceCache) {
        char[] chars = doSerialize(value);
        try (Writer writer = createStorageWriter(key)) {
            writer.write(chars);
            writer.flush();
            afterStorage(key, value, forceCache, chars.length);
        } catch (IOException e) {
            ThrowUtil.unchecked(e);
        }
    }

    private void afterStorage(String key, T value, boolean forceCache, int maxLength) {
        final Map valueCacheMap = getValueCached();
        valueCacheMap.remove(key);
        if (forceCache || maxLength <= getCacheLengthLimit()) {
            valueCacheMap.put(key, value);
        }
    }

    private Writer createStorageWriter(String key) {
        File enduranceFile = new File(namespace, key);
        if (enduranceFile.exists()) {
            enduranceFile.delete();
        }
        FileUtil.createNewFile(enduranceFile);
        return IOUtil.getWriter(enduranceFile);
    }

    private char[] doSerialize(T value) {
        byte[] serialized = getSerializer().apply(value);
        char[] chars = new char[serialized.length];
        for (int i = 0; i < serialized.length; i++) {
            chars[i] = (char) (serialized[i] + FACTOR);
        }
        return chars;
    }

    /**
     * 直接放入一个值，且不持久化
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, T value) { getValueCached().put(key, value); }

    public T getOrPull(String key, Supplier<T> puller) {
        return getOrPull(key, puller, false);
    }

    public T getOrPull(String key, Supplier<T> puller, boolean forceCacheOnPull) {
        T value = get(key);
        if (value != null) {
            return value;
        }
        T pulled = puller.get();
        if (pulled == null) {
            return null;
        }
        set(key, pulled, forceCacheOnPull);
        return pulled;
    }

    /**
     * 获取值
     *
     * @param key 键
     *
     * @return 值
     */
    @Override
    public T get(String key) { return get(key, false); }

    /**
     * 获取值
     *
     * @param key           键
     * @param forceNonCache 强制不缓存当前值
     *
     * @return 值
     */
    public T get(String key, boolean forceNonCache) { return getNullable(key, forceNonCache); }

    private T getNullable(String key, boolean forceNonCache) {
        final Map valueCacheMap = getValueCached();
        Object cached = valueCacheMap.get(key);
        // null 占位符
        if (isPlaceholder(cached)) {
            return null;
        }
        // 已缓存正确值
        if (cached != null) {
            return (T) cached;
        }
        // 读取存储
        byte[] serialized = readStorage(key);
        if (serialized == null) {
            // 不存在
            if (!forceNonCache) {
                valueCacheMap.put(key, Holder.HOLDER);
            }
            return null;
        }
        // 存在
        T data = getDeserializer().apply(serialized);
        if (!forceNonCache && serialized.length < getCacheLengthLimit()) {
            valueCacheMap.put(key, data);
        }
        return data;
    }

    private byte[] readStorage(String key) {
        File enduranceFile = new File(namespace, key);
        if (!enduranceFile.exists()) {
            return null;
        }
        FinalAccessor<String> accessor = FinalAccessor.of();
        try (FileReader reader = new FileReader(enduranceFile)) {
            IteratorUtil.forEachLines(reader, accessor::set);
            return accessor.ifPresentOrNull(v -> preDeserialize(v.toCharArray()));
        } catch (IOException e) {
            return ThrowUtil.unchecked(e);
        }
    }

    private byte[] preDeserialize(char[] line) {
        byte[] serialized = new byte[line.length];
        for (int i = 0; i < line.length; i++) {
            serialized[i] = (byte) (line[i] - FACTOR);
        }
        return serialized;
    }

    @Override
    public void remove(String key) {
        getValueCached().remove(key);
        FileUtil.delete(new File(namespace, key));
    }

    @Override
    public boolean hasKey(String key) {
        return getValueCached().containsKey(key) || new File(namespace, key).exists();
    }

    protected Map getValueCached() { return valueCached; }

    public LocalStorage<T> clearMemoryCached() {
        getValueCached().clear();
        return this;
    }

    public LocalStorage<T> clear() {
        clearMemoryCached();
        doClear(namespace);
        return this;
    }

    public LocalStorage<T> cacheLengthLimit(int lengthLimit) {
        this.cacheLengthLimit = lengthLimit;
        return this;
    }

    public int getCacheLengthLimit() { return cacheLengthLimit; }

    protected Function<T, byte[]> getSerializer() { return SERIALIZER; }

    protected Function<byte[], T> getDeserializer() { return (Function<byte[], T>) DESERIALIZER; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LocalStorage<?> storage = (LocalStorage<?>) o;
        return cacheLengthLimit == storage.cacheLengthLimit && Objects.equals(namespace, storage.namespace);
    }

    @Override
    public int hashCode() { return Objects.hash(namespace, cacheLengthLimit); }

    /**
     * 清除所有存储
     */
    public final static void clearAll() { FACTORY_MAP.forEach((root, f) -> doClear(root)); }

    private static void doClear(String path) {
        File file = new File(path);
        // 这里尽量避免删除系统原有文件，但不能完全避免
        FileUtil.deleteAllFiles(file);
        String[] list = file.list();
        if (list == null || list.length == 0) {
            FileUtil.deleteAllFiles(file, true);
        }
    }

    /**
     * 自定义存储路径工厂
     *
     * @param <T>
     */
    public final static class Factory<T> {

        private final Map<String, LocalStorage> STORAGE_MAP = new ConcurrentHashMap<>();
        private BiFunction<String, String, LocalStorage<T>> builder;
        private final String root;

        private Factory(String root) { this.root = root; }

        private static String toLocalNamespace(String root, String ns) {
            return absolutePath(useRootPath(root), useNamespace(ns));
        }

        private BiFunction<String, String, LocalStorage<T>> getBuilder() {
            return builder == null ? LocalStorage::new : builder;
        }

        public String getRootPath() { return root; }

        /**
         * 自定义实现{@link LocalStorage}
         *
         * @param builder LocalStorage
         */
        public void setStorageBuilder(BiFunction<String, String, LocalStorage<T>> builder) {
            this.builder = builder;
        }

        public LocalStorage<T> create(String namespace) {
            final String root = getRootPath();
            return STORAGE_MAP.computeIfAbsent(toLocalNamespace(root, namespace),
                k -> getBuilder().apply(root, namespace));
        }

        /**
         * 清除当前{@link Factory}所有缓存
         */
        public void clearAll() { STORAGE_MAP.forEach((k, s) -> s.clear()); }
    }
}
