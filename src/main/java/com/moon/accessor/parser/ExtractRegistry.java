package com.moon.accessor.parser;

import com.moon.accessor.ResultSetGetter;

import java.sql.ResultSet;

/**
 * @author benshaoye
 */
public class ExtractRegistry {

    static {
        ResultSetGetter<String> getter = ResultSet::getString;
    }

    public ExtractRegistry() {}
}
