package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
final class TrueCellRenderer extends BaseCellRenderer<TableCell> {
    protected TrueCellRenderer(TableCell annotation, CenterRenderer[] children, String[] formatted) {
        super(annotation, children, annotation.var(), formatted, annotation.value(), annotation.type(),
            annotation.rowspan(), annotation.colspan(), annotation.skipCells(),
            annotation.className(), annotation.height(), annotation.width());
    }
}
