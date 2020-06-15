package com.moon.more.excel.table;

import com.moon.more.excel.Renderer;

/**
 * @author benshaoye
 */
public class TableParser {

    private final static Parser GETTER = new Parser(Creates.GETTER);

    protected final static Renderer parseConfiguration(Class targetClass) {
        Renderer renderer = GETTER.doParseConfiguration(targetClass);
        return renderer;
    }
}
