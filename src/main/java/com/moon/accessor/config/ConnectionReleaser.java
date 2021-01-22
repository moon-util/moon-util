package com.moon.accessor.config;

import com.moon.accessor.exception.SqlException;

import javax.sql.DataSource;
import java.sql.Connection;

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
     * @throws SqlException 释放数据库连接
     */
    void release(Connection connection, DataSource dataSource) throws SqlException;
}
