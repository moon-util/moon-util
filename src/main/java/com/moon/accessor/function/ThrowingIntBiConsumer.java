package com.moon.accessor.function;

import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ThrowingIntBiConsumer<V> {

    void accept(V value, int intValue) throws SQLException;
}
