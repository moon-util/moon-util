package com.moon.more.excel;

import com.moon.more.excel.table.TableParser;

/**
 * @author benshaoye
 */
class TableUtil extends TableParser {

    static Renderer parse(Class targetClass) {
        return parseConfiguration(targetClass);
    }
}
