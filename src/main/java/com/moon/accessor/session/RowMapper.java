package com.moon.accessor.session;

import java.sql.ResultSet;

/**
 * 单行数据映射器
 *
 * @author benshaoye
 */
public interface RowMapper<T> {

    T doMap(ResultSet set);
}
