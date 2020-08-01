package com.moon.poi.excel.table;

import com.moon.poi.excel.TableFactory;

/**
 * @author moonsky
 */
public class TableParser {

    private final static Parser GETTER = new Parser(Creates.GETTER);

    protected final static TableRenderer parseOnly(Class targetClass) {
        return GETTER.doParseConfiguration(targetClass);
    }

    protected final static TableRenderer parseConfiguration(Class targetClass, TableFactory factory) {
        TableRenderer renderer = parseOnly(targetClass);
        renderer.definitionStyles(factory);
        return renderer;
    }
}
