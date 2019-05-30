package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class SheetCreator extends BaseCreator {

    private int indexOfRow = 0;

    private final Sheet sheet;

    private final RowCreator rowCreator;

    public SheetCreator(Sheet sheet) {
        rowCreator = new RowCreator();
        this.sheet = sheet;
    }

    public SheetCreator skip() { return skip(1); }

    public SheetCreator skip(int count) {
        indexOfRow += count;
        return this;
    }

    public SheetCreator createRow(Consumer<RowCreator> consumer) {
        int index = indexOfRow++;
        consumer.accept(rowCreator.setRow(sheet.createRow(index), index));
        return this;
    }

    /**
     * 设置列宽，null 值代表默认，不设置
     *
     * @param values
     *
     * @return
     */
    public SheetCreator widths(int... values) {
        return this;
    }
    public SheetCreator columnsWidth(Integer... values) {
        final int length = values == null ? 0 : values.length;
        final Sheet sheet = this.sheet;
        Integer source;
        for (int i = 0; i < length; i++) {
            if ((source = values[i]) != null) {
                sheet.setColumnWidth(i, source.intValue());
            }
        }
        return this;
    }
}
