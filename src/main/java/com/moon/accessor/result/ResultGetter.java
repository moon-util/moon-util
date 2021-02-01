package com.moon.accessor.result;

import java.sql.ResultSet;

/**
 * @author benshaoye
 */
public interface ResultGetter<T> {

    T getNullableValue(ResultSet set);
}
