package com.moon.core.util;

import java.util.Objects;

/**
 * @author benshaoye
 */
final class OptionalImpl<T> implements Optional<T> {

    private enum Empty implements Optional {INSTANCE}

    final static Optional EMPTY = Empty.INSTANCE;

    private T value;

    public OptionalImpl(T value) { this.value = Objects.requireNonNull(value); }

    @Override
    public Optional<T> nullableSet(T value) {
        this.value = value;
        return this;
    }

    @Override
    public T getOrNull() { return value; }

    @Override
    public int hashCode() { return Objects.hashCode(value); }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (!(obj instanceof OptionalImpl)) { return false; }
        return Objects.equals(value, ((OptionalImpl) obj).value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
