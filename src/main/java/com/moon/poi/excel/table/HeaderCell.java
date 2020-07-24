package com.moon.poi.excel.table;

/**
 * @author moonsky
 */
abstract class HeaderCell implements TableCell {

    /**
     * 当前是否是一个偏移的单元格
     *
     * @return true: 是
     */
    abstract boolean isOffsetCell();

    /**
     * 如果是偏移的单元格，是否填充偏移
     *
     * @return true|false, 默认总是填充
     */
    abstract boolean isFillSkipped();
}
