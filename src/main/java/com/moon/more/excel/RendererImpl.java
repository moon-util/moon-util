package com.moon.more.excel;

import com.moon.more.excel.parse.*;

import java.util.List;

/**
 * @author benshaoye
 */
class RendererImpl extends ParseUtil implements Renderer {

    private final MarkedColumnGroup group;

    public RendererImpl(Class targetClass) {
        this.group = parse(targetClass);
    }

    @Override
    public void renderHead(SheetFactory sheetFactory, int skipCol) { }

    @Override
    public void renderRecord(SheetFactory sheetFactory, int skipCol, Object data) {
        // group.
        List<MarkedColumn> columns = this.group.getColumns();
        sheetFactory.row(rowFactory -> {
            final int length = columns.size();
            MarkedColumn column = columns.get(0);
            // column.exec(data, rowFactory.cell(1, 1, skipCol).getCell());
            for (int i = 1; i < length; i++) {
                column = columns.get(i);
                // column.exec(data, rowFactory.cell().getCell());

                // column.isCanListable();
                // TableListable listable = column.getListable();
            }
        });
    }
}
