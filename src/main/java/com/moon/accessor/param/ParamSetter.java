package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ParamSetter {

    void setParameter(PreparedStatement stmt) throws SQLException;
}
