package com.moon.poi.excel.table;

import com.moon.poi.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author moonsky
 */
interface TableCell {

    /**
     * title
     *
     * @return
     */
    String getValue();

    /**
     * rowIdx
     *
     * @param rowIdxOffset 行偏移量
     *
     * @return
     */
    int getRowIdx(int rowIdxOffset);

    /**
     * colIdx
     *
     * @return
     */
    int getColIdx();

    /**
     * rowspan
     *
     * @return
     */
    int getRowspan();

    /**
     * colspan
     *
     * @return
     */
    int getColspan();

    /**
     * 单元格行高（excel 按行设置高度，多个单元格，追踪取最大高度设置）
     *
     * @return
     */
    short getHeight();

    /**
     * 合并表头
     *
     * @param rowIdxOffset 行索引偏移
     * @param sheet
     */
    default void merge(Sheet sheet, int rowIdxOffset) {
        if (getRowspan() > 1 || getColspan() > 1) {
            sheet.addMergedRegion(ExcelUtil.region(getRowIdx(rowIdxOffset),

                getColIdx(), getRowspan(), getColspan()));
        }
    }
}
