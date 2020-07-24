package com.moon.poi.excel.table;

import com.moon.poi.excel.Renderer;

/**
 * @author moonsky
 */
public class TableParser {

    private final static Parser GETTER = new Parser(Creates.GETTER);

    protected final static Renderer parseConfiguration(Class targetClass) {
        Renderer renderer = GETTER.doParseConfiguration(targetClass);
        return renderer;
    }
}
