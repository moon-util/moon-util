package com.moon.more.excel;

import java.util.Objects;

/**
 * 保存主要 key，并限制 hashCode 和 equals 方法
 *
 * @author benshaoye
 */
abstract class BaseProxy<T> {

    private final T key;

    protected BaseProxy(T key) { this.key = key; }

    public final T getKey() { return key; }

    @Override
    public final boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        BaseProxy<?> baseProxy = (BaseProxy<?>) o;
        return Objects.equals(key, baseProxy.key);
    }

    @Override
    public final int hashCode() { return Objects.hash(key); }
}
