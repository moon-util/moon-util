package com.moon.poi.excel;

import com.moon.poi.excel.table.TableParser;

/**
 * @author moonsky
 */
final class TableUtil extends TableParser {

    static Renderer parse(Class targetClass, TableFactory factory) { return parseConfiguration(targetClass, factory); }
}
