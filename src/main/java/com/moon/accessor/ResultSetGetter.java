package com.moon.accessor;

import com.moon.accessor.exception.SqlException;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ResultSetGetter<R> extends Serializable {

    /**
     * 从 ResultSet 中获取值
     *
     * @param set        result set
     * @param columnName column name
     *
     * @return value
     *
     * @throws SQLException 任何可能的 SQL 异常
     */
    R unwrap(ResultSet set, String columnName) throws SQLException;

    /**
     * 从 ResultSet 中获取值
     *
     * @param set        result set
     * @param columnName column name
     *
     * @return value
     */
    default R get(ResultSet set, String columnName) {
        try {
            return unwrap(set, columnName);
        } catch (SQLException e) {
            throw new SqlException(e);
        }
    }
}
