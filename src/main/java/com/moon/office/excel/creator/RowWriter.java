package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class RowWriter {

    private final WorkFactory factory;

    private final CellWriter writer;

    private Row row;

    RowWriter(WorkFactory factory) {
        this.factory = factory;
        writer = new CellWriter(factory);
    }

    RowWriter with(Row row) {
        this.row = row;
        return this;
    }

    private RowWriter withCell(Cell cell, Consumer<CellWriter> consumer) {
        consumer.accept(writer.with(cell));
        return this;
    }

    public RowWriter withCell(int rowIndex, Consumer<CellWriter> consumer) {
        return withCell(factory.withCell(rowIndex), consumer);
    }

    public RowWriter createCell(int rowIndex, Consumer<CellWriter> consumer) {
        return withCell(factory.withCell(rowIndex), consumer);
    }
}
