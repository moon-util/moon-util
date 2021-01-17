package com.moon.accessor.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ConnectionReleaser {

    /**
     * 释放数据库连接
     *
     * @param connection 数据库连接
     * @param dataSource 数据源
     *
     * @throws SQLException 释放数据库连接
     */
    void release(Connection connection, DataSource dataSource) throws SQLException;
}
