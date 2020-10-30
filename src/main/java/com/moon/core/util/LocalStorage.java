package com.moon.core.util;

import com.moon.core.io.FileUtil;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.SystemUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.FinalAccessor;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author moonsky
 */
public class LocalStorage<T> implements Storage<String, T> {

    private final static String PATH_NAMESPACE;
    private final static char CHAR_HYPHEN = '-';
    private final static char CHAR_WAVE = '~';
    private final static short FACTOR = 19968;

    private final static Function<Object, byte[]> SERIALIZER = IOUtil::serialize;
    private final static Function<byte[], Object> DESERIALIZER = IOUtil::deserialize;
    private final static LocalStorage DEFAULT;
    private final static Map<String, LocalStorage> storageMap = new ConcurrentHashMap<>();

    static {
        String namespace = ".java-imoon/localStorage/";
        PATH_NAMESPACE = toFormatted(new File(SystemUtil.getTempDir(), namespace));
        DEFAULT = new LocalStorage(null, null);
    }

    private static String toFormatted(File root) {
        return FileUtil.formatFilepath(root.getAbsolutePath());
    }

    private static String toLocation(String root, String namespace) {
        char first = namespace.charAt(0);
        if (first == CHAR_HYPHEN || first == CHAR_WAVE) {
            return root + namespace.substring(1);
        } else {
            return root + namespace;
        }
    }

    private final String namespace;
    private int cacheLimit = 10240;

    private final Map<String, T> localCached = new ConcurrentHashMap<>();

    private LocalStorage(String namespace) {
        this.namespace = namespace;
        storageMap.put(namespace, this);
    }

    public LocalStorage(String root, String namespace) {
        String ROOT = StringUtil.defaultIfBlank(root, PATH_NAMESPACE);
        namespace = StringUtil.defaultIfBlank(namespace, "default");
        namespace = StringUtil.replace(namespace, '\\', CHAR_WAVE);
        namespace = StringUtil.replace(namespace, '/', CHAR_HYPHEN);
        this.namespace = toLocation(ROOT, namespace);
        storageMap.put(namespace, this);
    }

    public static <T extends Serializable> LocalStorage<T> of() { return DEFAULT; }

    public static <T extends Serializable> LocalStorage<T> of(String namespace) {
        return storageMap.computeIfAbsent(namespace, LocalStorage::new);
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
        return new Factory<>(location);
    }

    @Override
    public void set(String key, T value) {
        set(key, value, false);
    }

    public void set(String key, T value, boolean forceCache) {
        byte[] serialized = getSerializer().apply(value);
        char[] chars = new char[serialized.length];
        for (int i = 0; i < serialized.length; i++) {
            chars[i] = (char) (serialized[i] + FACTOR);
        }
        String serializedStr = new String(chars);
        File enduranceFile = new File(namespace, key);
        if (enduranceFile.exists()) {
            enduranceFile.delete();
        }
        FileUtil.createNewFile(enduranceFile);
        try (Writer writer = IOUtil.getWriter(enduranceFile)) {
            writer.write(serializedStr);
            writer.flush();
            if (forceCache || chars.length <= getCacheMemoryLimit()) {
                localCached.put(key, value);
            }
        } catch (IOException e) {
            ThrowUtil.unchecked(e);
        }
    }

    @Override
    public T get(String key) { return getNullable(key); }

    private T getNullable(String key) {
        T cached = localCached.get(key);
        if (cached != null) {
            return cached;
        }
        File enduranceFile = new File(namespace, key);
        if (enduranceFile.exists()) {
            FinalAccessor<String> accessor = FinalAccessor.of();
            try (FileReader reader = new FileReader(enduranceFile)) {
                IteratorUtil.forEachLines(reader, accessor::set);
                if (accessor.isAbsent()) {
                    return null;
                }
                char[] line = accessor.get().toCharArray();
                byte[] serialized = new byte[line.length];
                for (int i = 0; i < line.length; i++) {
                    serialized[i] = (byte) (line[i] - FACTOR);
                }
                return getDeserializer().apply(serialized);
            } catch (IOException e) {
                return ThrowUtil.unchecked(e);
            }
        }
        return null;
    }

    @Override
    public void remove(String key) {
        localCached.remove(key);
        FileUtil.delete(new File(namespace, key));
    }

    public void clear() {
        localCached.clear();
        FileUtil.deleteAllFiles(new File(namespace));
    }

    @Override
    public boolean hasKey(String key) {
        return localCached.containsKey(key) || new File(namespace, key).exists();
    }

    public final static void clearAll() {
        storageMap.forEach((r, s) -> s.clear());
        doClear(PATH_NAMESPACE);
    }

    protected Function<T, byte[]> getSerializer() {
        return (Function<T, byte[]>) SERIALIZER;
    }

    protected Function<byte[], T> getDeserializer() {
        return (Function<byte[], T>) DESERIALIZER;
    }

    private static void doClear(String root) {
        FileUtil.delete(new File(root));
    }

    public int getCacheMemoryLimit() { return cacheLimit; }

    public static class Factory<T> {

        private static Map<String, LocalStorage> factoryMap = new ConcurrentHashMap<>();
        private Function<String, LocalStorage<T>> builder;
        private final String root;

        private Factory(String root) {
            this.root = StringUtil.defaultIfBlank(root, PATH_NAMESPACE);
        }

        private Function<String, LocalStorage<T>> getBuilder() {
            return builder == null ? LocalStorage::new : builder;
        }

        public void setStorageBuilder(Function<String, LocalStorage<T>> builder) {
            this.builder = builder;
        }

        public LocalStorage<T> create(String namespace) {
            LocalStorage<T> storage = getBuilder().apply(toFormatted(new File(root, namespace)));
            factoryMap.put(storage.namespace, storage);
            return storage;
        }

        public void clearAll(){
            factoryMap.forEach((k, s) -> s.clear());
            doClear(root);
        }
    }
}
