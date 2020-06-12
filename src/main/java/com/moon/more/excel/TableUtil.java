package com.moon.more.excel;

import com.moon.more.excel.table.ParseUtil;

/**
 * @author benshaoye
 */
class TableUtil extends ParseUtil {

    public TableUtil() { }

    static Renderer parse(Class targetClass) {
        return doParseConfiguration(targetClass);
    }
}
