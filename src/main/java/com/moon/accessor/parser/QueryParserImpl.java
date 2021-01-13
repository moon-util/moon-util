package com.moon.accessor.parser;

import com.moon.accessor.meta.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class QueryParserImpl {

    private final Map<Class<?>, Table<?>> allTablesMap;

    public QueryParserImpl(Table<?>... tables) {
        Map<Class<?>, Table<?>> allTablesMap = new HashMap<>();
        for (Table<?> table : tables) {
            allTablesMap.put(table.getEntityType(), table);
        }
        this.allTablesMap = allTablesMap;
    }

    public void parse(String qlString) {

    }
}
