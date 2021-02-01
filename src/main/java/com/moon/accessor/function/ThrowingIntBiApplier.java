package com.moon.accessor.function;

import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ThrowingIntBiApplier<T, R> {

    R apply(T t, int value) throws SQLException;
}
