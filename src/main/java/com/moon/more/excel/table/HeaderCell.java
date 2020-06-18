package com.moon.more.excel.table;

/**
 * @author benshaoye
 */
abstract class HeaderCell implements TableCell {

    abstract boolean isOffsetCell();

    abstract boolean isFillSkipped();
}
