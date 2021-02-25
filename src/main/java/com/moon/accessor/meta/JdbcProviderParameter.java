package com.moon.accessor.meta;

import com.moon.accessor.type.JdbcType;
import com.moon.accessor.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public final class JdbcProviderParameter<T> implements JdbcParameter<T> {

    private final TypeHandler<T> handler;
    private final JdbcType jdbcType;
    private final Supplier<T> valueProvider;
    private final int index;
    private boolean resolved;
    private T resolvedValue;

    JdbcProviderParameter(TableField<T, ?, ?> field, Supplier<T> valueProvider, int index) {
        this(field.getTypeHandler(), field.getJdbcType(), valueProvider, index);
    }

    JdbcProviderParameter(TypeHandler<T> handler, JdbcType jdbcType, Supplier<T> valueProvider, int index) {
        this.valueProvider = valueProvider;
        this.jdbcType = jdbcType;
        this.handler = handler;
        this.index = index;
    }

    @Override
    public T resolve() {
        T value = getResolvedValue();
        if (value == null) {
            if (isResolved()) {
                return null;
            }
            value = valueProvider.get();
            this.resolvedValue = value;
            this.resolved = true;
        }
        return value;
    }

    public boolean isResolved() { return resolved; }

    public TypeHandler<T> getHandler() { return handler; }

    public T getResolvedValue() { return resolvedValue; }

    public int getIndex() { return index; }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        handler.setParameter(stmt, index, resolve(), jdbcType.getTypeCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        JdbcProviderParameter<?> that = (JdbcProviderParameter<?>) o;
        return index == that.index && Objects.equals(handler, that.handler) && Objects.equals(resolve(),
            that.resolve());
    }

    @Override
    public int hashCode() { return Objects.hash(handler, resolve(), index); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JdbcParameter{");
        sb.append("handler=").append(handler);
        sb.append(", value=").append(resolve());
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
