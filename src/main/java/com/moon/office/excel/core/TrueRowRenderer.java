package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
final class TrueRowRenderer extends BaseRowRenderer<TableRow> {
    protected TrueRowRenderer(TableRow annotation, CenterRenderer[] children, String[] formatted) {
        super(annotation, children, annotation.var(), formatted,
            annotation.className(), annotation.skipRows(), annotation.height());
    }
}
