package com.moon.poi.excel.table;

import com.moon.poi.excel.TableFactory;

/**
 * @author moonsky
 */
public class TableParser {

    private final static Parser GETTER = new Parser(Creates.GETTER);

    protected final static TableRenderer parseOnly(Class targetClass) {
        return GETTER.doParseConfiguration(targetClass, true);
    }

    protected final static TableRenderer parseOnly(Class targetClass, boolean cacheDisabled) {
        return GETTER.doParseConfiguration(targetClass, cacheDisabled);
    }

    protected final static TableRenderer parseConfiguration(
        Class targetClass, TableFactory factory, boolean cacheDisabled
    ) {
        TableRenderer renderer = parseOnly(targetClass, cacheDisabled);
        renderer.definitionStyles(factory);
        return renderer;
    }
}
