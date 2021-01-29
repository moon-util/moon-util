package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public enum Param2 {
    ;

    public static void setObjectParameters(PreparedStatement stmt, Object[] parameters) throws SQLException {
        for (int i = 0, l = parameters.length; i < l; i++) {
            stmt.setObject(i + 1, parameters[i]);
        }
    }

    public static void setObjectParameters(PreparedStatement stmt, ParamSetter[] setters) {
        for (int i = 0, l = setters.length; i < l; i++) {
            setters[i].setParameter(stmt);
        }
    }
}
