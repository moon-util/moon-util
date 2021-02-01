package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ParameterSetter {

    void setNull(PreparedStatement stmt) throws SQLException;

    void setParameter(PreparedStatement stmt) throws SQLException;
}
