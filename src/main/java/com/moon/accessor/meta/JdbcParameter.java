package com.moon.accessor.meta;

import com.moon.accessor.type.TypeHandler;

import java.util.Objects;

/**
 * @author benshaoye
 */
public final class JdbcParameter<T, R, TB extends Table<R, TB>> {

    private final TypeHandler<T> handler;
    private final T value;
    private final int index;

    JdbcParameter(TypeHandler<T> handler, T value, int index) {
        this.handler = handler;
        this.value = value;
        this.index = index;
    }

    public TypeHandler<T> getHandler() { return handler; }

    public T getValue() { return value; }

    public int getIndex() { return index; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        JdbcParameter<?, ?, ?> that = (JdbcParameter<?, ?, ?>) o;
        return index == that.index && Objects.equals(handler, that.handler) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(handler, value, index); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JdbcParameter{");
        sb.append("handler=").append(handler);
        sb.append(", value=").append(value);
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
