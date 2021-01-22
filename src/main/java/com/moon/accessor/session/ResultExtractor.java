package com.moon.accessor.session;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface ResultExtractor<T> {

    T extract(ResultSet resultSet) throws SQLException;
}
