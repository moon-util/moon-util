package com.moon.core.util;

import com.moon.core.io.FileUtil;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.SystemUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.FinalAccessor;

import java.io.*;
import java.util.Map;
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
        File file = new File(SystemUtil.getTempDir(), namespace);
        PATH_NAMESPACE = FileUtil.formatFilepath(file.getAbsolutePath());
        DEFAULT = new LocalStorage(null);
    }

    private final String namespace;

    private final Map<String, T> localCached = new ConcurrentHashMap<>();

    public LocalStorage(String namespace) {
        namespace = StringUtil.defaultIfBlank(namespace, "default");
        namespace = StringUtil.replace(namespace, '\\', CHAR_WAVE);
        namespace = StringUtil.replace(namespace, '/', CHAR_HYPHEN);
        char first = namespace.charAt(0);
        if (first == CHAR_HYPHEN || first == CHAR_WAVE) {
            this.namespace = PATH_NAMESPACE + namespace.substring(1);
        } else {
            this.namespace = PATH_NAMESPACE + namespace;
        }
        storageMap.put(namespace, this);
    }

    public static <T extends Serializable> LocalStorage<T> of() { return DEFAULT; }

    public static <T extends Serializable> LocalStorage<T> of(String namespace) {
        return storageMap.computeIfAbsent(namespace, LocalStorage::new);
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
        storageMap.forEach((s, localStorage) -> localStorage.clear());
        FileUtil.deleteAllFiles(new File(PATH_NAMESPACE));
    }

    protected Function<T, byte[]> getSerializer() {
        return (Function<T, byte[]>) SERIALIZER;
    }

    protected Function<byte[], T> getDeserializer() {
        return (Function<byte[], T>) DESERIALIZER;
    }

    protected int getCacheMemoryLimit() { return 10240; }
}
