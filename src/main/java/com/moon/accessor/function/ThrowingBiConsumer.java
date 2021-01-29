package com.moon.accessor.function;

import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ThrowingBiConsumer<T1, T2> {

    void accept(T1 t1, T2 t2) throws SQLException;
}
