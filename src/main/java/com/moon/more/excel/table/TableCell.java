package com.moon.more.excel.table;

import com.moon.more.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author benshaoye
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
     * @return
     */
    int getRowIdx();

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
     * @param sheet
     */
    default void merge(Sheet sheet) {
        if (getRowspan() > 1 || getColspan() > 1) {
            sheet.addMergedRegion(ExcelUtil.region(getRowIdx(),

                getColIdx(), getRowspan(), getColspan()));
        }
    }
}
