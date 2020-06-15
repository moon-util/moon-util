package com.moon.more.excel.table;

import com.moon.more.excel.ExcelUtil;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 一个合并的单元格区域信息
 *
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

    /**
     * 合并表头单元格
     *
     * @return 单元格区域信息
     */
    final CellRangeAddress region() {
        return ExcelUtil.region(rowIdx, colIdx, rowspan, colspan);
    }
}
