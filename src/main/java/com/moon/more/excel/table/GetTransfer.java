package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;

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
    void transfer(CellFactory factory, Object value);
}
