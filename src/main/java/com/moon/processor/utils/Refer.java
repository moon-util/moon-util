package com.moon.processor.utils;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public final class Refer<V> {

    private V value;

    public Refer() { }

    public static <V> Refer<V> of() { return new Refer<>(); }

    public void set(V value) { this.value = value; }

    public V getValue() { return value; }

    public void setIfAbsent(V value) {
        if (this.value == null) {
            set(value);
        }
    }

    public void useIfPresent(Consumer<V> consumer) {
        if (consumer != null && value != null) {
            consumer.accept(value);
        }
    }
}
