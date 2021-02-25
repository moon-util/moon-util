package com.moon.accessor.meta;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public interface JdbcParameter<T> {

    T resolve();

    void setParameter(PreparedStatement stmt) throws SQLException;

    // static <T> JdbcParameter<T> resolveIfNull(T value, Supplier<T> fallbackProvider) {
    //
    // }
    //
    // static <T extends CharSequence> JdbcParameter<T> resolveIfEmpty(T value, Supplier<T> fallbackProvider) {
    //
    // }
}
