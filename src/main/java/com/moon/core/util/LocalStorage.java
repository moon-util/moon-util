package com.moon.core.util;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.SystemUtil;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author moonsky
 */
final class LocalStorage<T> {

    private final static String TEMP_DIR = SystemUtil.getTempDir();
    private final static String PATH_NAMESPACE = ".java-moon-util/storage/";
    private final static char CHAR_HYPHEN = '-';
    private final static char CHAR_WAVE = '~';

    private final String namespace;
    private final Function<T, byte[]> serializer;
    private final Function<byte[], T> deserializer;

    public LocalStorage(
        String namespace, Function<T, byte[]> serializer, Function<byte[], T> deserializer
    ) {
        this.deserializer = Objects.requireNonNull(deserializer);
        this.serializer = Objects.requireNonNull(serializer);

        Objects.requireNonNull(namespace);
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

    public T get(String key) {
        return null;
    }

    public void remove(String key) {
    }

    public void set(String key, T value) {
        byte[] serialized = serializer.apply(value);
    }
}
