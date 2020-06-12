package com.moon.more.excel.table;

import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author benshaoye
 */
final class TableRenderer implements Renderer {

    private final static TableCol[] EMPTY = new TableCol[0];

    private final TableCol[] columns;

    TableRenderer(TableCol[] columns) {
        this.columns = columns == null ? EMPTY : Arrays.copyOf(columns, columns.length);
    }

    @Override
    public void renderHead(SheetFactory sheetFactory) {

    }

    @Override
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        TableCol[] columns = this.columns;
        if (first != null) {
            renderRecord(columns, sheetFactory, first);
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                renderRecord(columns, sheetFactory, iterator.next());
            }
        }
    }

    public void renderRecord(TableCol[] columns, SheetFactory sheetFactory, Object record) {
        RowFactory row = sheetFactory.row();
        int length = columns.length;
        TableCol column;
        for (int i = 0; i < length; i++) {
            row.index(i);
            column = columns[i];
            column.render(row.index(i), record);
        }

    }
}
