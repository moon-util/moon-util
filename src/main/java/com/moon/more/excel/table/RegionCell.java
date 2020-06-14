package com.moon.more.excel.table;

import com.moon.more.excel.ExcelUtil;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author benshaoye
 */
final class RegionCell {

    private final int rowIdx;
    private final int colIdx;
    private final int rowspan;
    private final int colspan;

    RegionCell(int rowIdx, int colIdx, int rowspan, int colspan) {
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
        this.rowspan = rowspan;
        this.colspan = colspan;
    }

    final CellRangeAddress region() {
        return ExcelUtil.region(rowIdx, colIdx, rowspan, colspan);
    }
}
