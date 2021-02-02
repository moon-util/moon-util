package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ParameterSetter {

    void setParameter(PreparedStatement stmt, int index) throws SQLException;
}
