package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 应用样式
 *
 * @author moonsky
 */
class ProxyStyleSetter extends ProxySetter<CellStyleProxy, Object> {

    private final CellRangeAddress rangeAddress;

    public ProxyStyleSetter(Cell cell, CellRangeAddress rangeAddress) {
        super(cell);
        this.rangeAddress = rangeAddress;
    }

    public ProxyStyleSetter(Row row) {
        super(row);
        this.rangeAddress = null;
    }

    /**
     * 应用到单元格或单元行
     *
     * @param style 样式
     */
    @Override
    void setup(CellStyleProxy style) {
        Object object = this.getKey();
        if (object instanceof Cell) {
            style.accept((Cell) object, rangeAddress);
        } else if (object instanceof Row) {
            style.accept((Row) object);
        }
    }
}
