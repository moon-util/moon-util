package com.moon.core.util;

import com.moon.core.lang.SystemUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author moonsky
 */
final class LocalStorage<T> {

    private final static String TEMP_DIR = SystemUtil.getTempDir();

    private final Function<T, byte[]> serializer;
    private final Function<byte[], T> deserializer;
    private final BiConsumer<String, byte[]> caching;

    public LocalStorage(
        Function<T, byte[]> serializer, Function<byte[], T> deserializer, BiConsumer<String, byte[]> caching
    ) {
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.caching = caching;
    }

    public void get(String key) {

    }

    public void remove(String key) {
    }

    public void set(String key, T value) {
        caching.accept(key, serializer.apply(value));
    }
}
