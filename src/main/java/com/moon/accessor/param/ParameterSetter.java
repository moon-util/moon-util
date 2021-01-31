package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 鉴于{@link PreparedStatement#setObject(int, Object)}的实现方式是运行是逐个判断数据类型
 *
 * @author benshaoye
 */
public interface ParameterSetter {

    void setParameter(PreparedStatement stmt) throws SQLException;
}
