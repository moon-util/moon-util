package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class SheetWriter {

    private final WorkFactory factory;
    private final RowWriter writer;

    private Sheet sheet;

    SheetWriter(WorkFactory factory) {
        this.factory = factory;
        writer = new RowWriter(factory);
    }

    SheetWriter with(Sheet sheet) {
        this.sheet = sheet;
        return this;
    }

    public SheetWriter columnsWidth(Integer... widths) {
        if (widths != null) {
            Integer width;
            Sheet sheet = this.sheet;
            for (int i = 0, len = widths.length; i < len; i++) {
                if ((width = widths[i]) != null) {
                    sheet.setColumnWidth(i, width);
                }
            }
        }
        return this;
    }

    private SheetWriter withRow(Row row, Consumer<RowWriter> consumer) {
        consumer.accept(writer.with(row));
        return this;
    }

    public SheetWriter withRow(int rowIndex, Consumer<RowWriter> consumer) {
        return withRow(factory.withRow(rowIndex), consumer);
    }

    public SheetWriter createRow(int rowIndex, Consumer<RowWriter> consumer) {
        return withRow(factory.createRow(rowIndex), consumer);
    }
}
