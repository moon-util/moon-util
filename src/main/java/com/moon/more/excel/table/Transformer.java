package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;

/**
 * @author benshaoye
 */
interface Transformer {

    /**
     * 设置单元格值
     *
     * @param factory
     * @param value
     */
    void doTransform(CellFactory factory, Object value);
}
