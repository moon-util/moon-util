package com.moon.core.util;

import com.moon.core.io.FileUtil;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.SystemUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.FinalAccessor;

import java.io.*;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author moonsky
 */
final class LocalStorage<T> {

    private final static String PATH_NAMESPACE;
    private final static char CHAR_HYPHEN = '-';
    private final static char CHAR_WAVE = '~';
    private final static short FACTOR = 19968;

    private final static Function<Object, byte[]> SERIALIZER = IOUtil::serialize;
    private final static Function<byte[], Object> DESERIALIZER = IOUtil::deserialize;
    private final static LocalStorage STORAGE;

    static {
        String namespace = ".java-imoon/localStorage/";
        File file = new File(SystemUtil.getTempDir(), namespace);
        PATH_NAMESPACE = FileUtil.formatFilepath(file.getAbsolutePath());
        STORAGE = new LocalStorage(null, SERIALIZER, DESERIALIZER);
    }

    private final String namespace;
    private final Function<T, byte[]> serializer;
    private final Function<byte[], T> deserializer;

    public LocalStorage(
        String namespace, Function<T, byte[]> serializer, Function<byte[], T> deserializer
    ) {
        this.deserializer = Objects.requireNonNull(deserializer);
        this.serializer = Objects.requireNonNull(serializer);

        namespace = StringUtil.defaultIfBlank(namespace, "default");
        namespace = StringUtil.replace(namespace, '\\', CHAR_WAVE);
        namespace = StringUtil.replace(namespace, '/', CHAR_HYPHEN);
        char first = namespace.charAt(0);
        if (first == CHAR_HYPHEN || first == CHAR_WAVE) {
            this.namespace = PATH_NAMESPACE + namespace.substring(1);
        } else {
            this.namespace = PATH_NAMESPACE + namespace;
        }
    }

    public static <T extends Serializable> LocalStorage<T> of() { return STORAGE; }

    public static <T extends Serializable> LocalStorage<T> of(String namespace) {
        LocalStorage storage = new LocalStorage<>(namespace, SERIALIZER, DESERIALIZER);
        return storage;
    }

    public void set(String key, T value) {
        byte[] serialized = serializer.apply(value);
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
        } catch (IOException e) {
            ThrowUtil.unchecked(e);
        }
    }

    public T get(String key) { return getNullable(key); }

    private T getNullable(String key) {
        File enduranceFile = new File(namespace, key);
        if (enduranceFile.exists()) {
            FinalAccessor<String> accessor = FinalAccessor.of();
            try (FileReader reader = new FileReader(enduranceFile)){
                IteratorUtil.forEachLines(reader, line -> {
                    accessor.set(line);
                });
                if (accessor.isAbsent()) {
                    return null;
                }
                char[] line = accessor.get().toCharArray();
                byte[] serialized = new byte[line.length];
                for (int i = 0; i < line.length; i++) {
                    serialized[i] = (byte) (line[i] - FACTOR);
                }
                return deserializer.apply(serialized);
            } catch (IOException e) {
                ThrowUtil.unchecked(e);
            }
        }
        return null;
    }

    public void remove(String key) {
        FileUtil.delete(new File(namespace, key));
    }

    public void clear() {
        FileUtil.deleteAllFiles(new File(namespace));
    }

    public boolean has(String key) { return new File(namespace, key).exists(); }

    public final static void clearAll() {
        FileUtil.deleteAllFiles(new File(PATH_NAMESPACE));
    }
}
