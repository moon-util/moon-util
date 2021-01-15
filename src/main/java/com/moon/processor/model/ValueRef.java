package com.moon.processor.model;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public final class ValueRef<V> {

    private V value;

    public ValueRef() { }

    public void set(V value) { this.value = value; }

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
