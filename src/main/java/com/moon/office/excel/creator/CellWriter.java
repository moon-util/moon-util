package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @author benshaoye
 */
public class CellWriter {

    private final WorkFactory factory;

    private Cell cell;

    CellWriter(WorkFactory factory) {this.factory = factory;}

    CellWriter with(Cell cell) {
        this.cell = cell;
        return this;
    }
}
