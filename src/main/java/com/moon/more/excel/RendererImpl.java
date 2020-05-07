package com.moon.more.excel;

import com.moon.more.excel.parse.PropertyGet;
import com.moon.more.excel.parse.PropertiesGroupGet;
import com.moon.more.excel.parse.ParseUtil;

/**
 * @author benshaoye
 */
class RendererImpl extends ParseUtil implements Renderer {

    private final PropertyGet[] columns;

    public RendererImpl(Class targetClass) {
        PropertiesGroupGet detailGet = parseGetter(targetClass);
        this.columns = detailGet.getColumnsArr();
    }

    @Override
    public void renderHead(SheetFactory sheetFactory, int skipCol) { }

    @Override
    public void renderRecord(SheetFactory sheetFactory, int skipCol, Object data) {
        PropertyGet[] columns = this.columns;
        sheetFactory.row(rowFactory -> {
            final int length = columns.length;
            PropertyGet column = columns[0];
            column.exec(data, rowFactory.cell(1, 1, skipCol).getCell());
            for (int i = 1; i < length; i++) {
                column = columns[i];
                column.exec(data, rowFactory.cell().getCell());

                // column.isCanListable();
                // TableListable listable = column.getListable();
            }
        });
    }
}
