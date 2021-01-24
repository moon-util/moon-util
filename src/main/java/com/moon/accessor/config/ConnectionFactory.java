package com.moon.accessor.config;

import com.moon.accessor.exception.Exception2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public interface ConnectionFactory {

    /**
     * 获得数据库连接
     *
     * @return 数据库连接
     *
     * @throws SQLException SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * 释放数据库连接
     *
     * @param connection 数据库连接
     */
    default void release(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {}
    }

    /**
     * 使用连接
     *
     * @param consumer 处理器
     * @param <T>      返回数据类型
     *
     * @return data
     */
    default <T> T use(Function<Connection, T> consumer) {
        Connection connection;
        try {
            connection = getConnection();
        } catch (SQLException e) {
            throw Exception2.with(e);
        }
        try {
            return consumer.apply(connection);
        } finally {
            try {
                release(connection);
            } catch (Exception ignored) {}
        }
    }
}
