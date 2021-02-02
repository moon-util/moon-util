package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author benshaoye
 */
public enum Parameter2 {
    ;

    public static void setAll(PreparedStatement stmt, Object[] parameters) throws SQLException {
        for (int i = 0, l = parameters.length; i < l; i++) {
            stmt.setObject(i + 1, parameters[i]);
        }
    }

    public static void setAll(PreparedStatement stmt, ParameterSetter[] setters) throws SQLException {
        for (int i = 0, l = setters.length; i < l; i++) {
            setters[i].setParameter(stmt, i);
        }
    }

    public static void setAll(PreparedStatement stmt, Collection<ParameterSetter> setters) throws SQLException {
        int index = 0;
        for (ParameterSetter setter : setters) {
            setter.setParameter(stmt, index++);
        }
    }
}
