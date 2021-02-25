package com.moon.accessor.meta;

import com.moon.accessor.type.JdbcType;
import com.moon.accessor.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author benshaoye
 */
public final class JdbcValueParameter<T> implements JdbcParameter<T> {

    private final TypeHandler<T> handler;
    private final JdbcType jdbcType;
    private final T value;
    private final int index;

    JdbcValueParameter(TableField<T, ?, ?> field, T value, int index) {
        this(field.getTypeHandler(), field.getJdbcType(), value, index);
    }

    JdbcValueParameter(TypeHandler<T> handler, JdbcType jdbcType, T value, int index) {
        this.jdbcType = jdbcType;
        this.handler = handler;
        this.value = value;
        this.index = index;
    }

    @Override
    public T resolve() { return getValue(); }

    public TypeHandler<T> getHandler() { return handler; }

    public T getValue() { return value; }

    public int getIndex() { return index; }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        handler.setParameter(stmt, index, value, jdbcType.getTypeCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        JdbcValueParameter<?> that = (JdbcValueParameter<?>) o;
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
