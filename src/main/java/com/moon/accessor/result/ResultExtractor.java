package com.moon.accessor.result;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ResultExtractor<T> {

    /**
     * SQL 结果集提取器
     * <p>
     * 自定义完整提取 SQL 结果集
     *
     * @param resultSet SQL 结果集
     *
     * @return 自定义返回
     *
     * @throws SQLException SQLException
     */
    T extract(ResultSet resultSet) throws SQLException;
}
