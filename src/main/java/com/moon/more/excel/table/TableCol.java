package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;

/**
 * @author benshaoye
 */
final class TableCol {

    private final String[] titles;
    private final Operation operation;

    TableCol(String[] titles, Operation operation) {
        this.operation = operation;
        this.titles = titles;
    }

    void render(CellFactory factory, Object data) {
        operation.operate(factory, data);
    }
}
