package com.moon.poi.excel.table;

import com.moon.poi.excel.CellWriter;

/**
 * @author moonsky
 */
interface GetTransfer {

    /**
     * 设置单元格值
     *
     * @param factory
     * @param value
     */
    void transfer(CellWriter factory, Object value);
}
