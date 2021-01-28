package com.moon.accessor.result;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 单行数据映射器
 *
 * @author benshaoye
 */
public interface RowMapper<T> {

    /**
     * 将当前行数据映射到任意自定义类型中返回
     *
     * @param set 指针位于当前行的 SQL 结果集
     *
     * @return 自定义返回
     *
     * @throws SQLException 通常是{@link SQLException}或其子类
     */
    T doMap(ResultSet set) throws SQLException;
}
