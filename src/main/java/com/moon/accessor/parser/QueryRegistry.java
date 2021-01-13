package com.moon.accessor.parser;

import com.moon.accessor.ResultSetGetter;

import java.sql.ResultSet;

/**
 * @author benshaoye
 */
public class QueryRegistry {

    static {
        ResultSetGetter<String> getter = ResultSet::getString;
    }

    public QueryRegistry() {}
}
