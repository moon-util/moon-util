package com.moon.mapping.convert;

import com.moon.core.util.function.SerializableFunction;

/**
 * @author moonsky
 */
public final class Selector {

    public Selector() {
    }

    public static Selector select() {
        return new Selector();
    }

    public <T, P> Selector column(SerializableFunction<T, P> column) {
        return this;
    }

    public <T, P> Selector columns(SerializableFunction<T, P>... columns) {
        return this;
    }

    public <T> Selector columns(Class<T> entityClass, Class<? super T> columnsInterface) {
        return this;
    }

    public <T> Selector from(Class<T> entityClass) {
        return this;
    }

    public <T> Selector leftJoin(Class<T> entityClass) {
        return this;
    }

    public <T> Selector rightJoin(Class<T> entityClass) {
        return this;
    }

    public <T, P> Selector on(SerializableFunction<T, P> column) {
        return this;
    }

    public <T, P> Selector eq(SerializableFunction<T, P> column) {
        return this;
    }

    public <T, P> Selector lt(SerializableFunction<T, P> column) {
        return this;
    }

    public <T, P> Selector gt(SerializableFunction<T, P> column) {
        return this;
    }

    public <T, P> Selector ne(SerializableFunction<T, P> column) {
        return this;
    }

    public <T, P> Selector nl(SerializableFunction<T, P> column) {
        return this;
    }

    public <T, P> Selector ng(SerializableFunction<T, P> column) {
        return this;
    }

    public void fetch() {

    }
}
