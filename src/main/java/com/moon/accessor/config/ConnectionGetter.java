package com.moon.accessor.config;

import com.moon.accessor.exception.SqlException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ConnectionGetter {

    /**
     * 得到数据库连接
     *
     * @param dataSource 数据源
     *
     * @return 数据库连接
     *
     * @throws SQLException 数据库连接或执行错误
     */
    Connection getConnection(DataSource dataSource) throws SqlException;
}
