package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author benshaoye
 */
class ProxyStyleSetter extends ProxySetter<CellStyle, Object> {

    public ProxyStyleSetter(Cell cell) { super(cell); }

    public ProxyStyleSetter(Row row) { super(row); }

    @Override
    void setup(CellStyle style) {
        Object object = this.getKey();
        if (object instanceof Cell) {
            ((Cell) object).setCellStyle(style);
        } else if (object instanceof Row) {
            ((Row) object).setRowStyle(style);
        }
    }
}
