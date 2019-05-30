package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author benshaoye
 */
abstract class BaseCreator {

    Sheet currentSheet() { throw new UnsupportedOperationException(); }
    Row currentRow() { throw new UnsupportedOperationException(); }
    Cell currentCell() { throw new UnsupportedOperationException(); }
}
