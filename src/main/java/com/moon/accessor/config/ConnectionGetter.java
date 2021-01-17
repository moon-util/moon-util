package com.moon.accessor.config;

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
     */
    void getConnection(DataSource dataSource) throws SQLException;
}
