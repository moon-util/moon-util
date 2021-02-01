package com.moon.accessor.function;

import java.sql.SQLException;

/**
 * 可抛异常的二参函数
 *
 * @author benshaoye
 */
@FunctionalInterface
public interface ThrowingBiApplier<T, U, R> {

    /**
     * 执行函数
     *
     * @param t 参数1
     * @param u 参数2
     *
     * @return 返回值
     */
    R apply(T t, U u) throws SQLException;
}
