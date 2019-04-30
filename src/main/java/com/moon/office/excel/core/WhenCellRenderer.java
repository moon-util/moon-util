package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
final class WhenCellRenderer extends BaseCellRenderer<TableCell> {
    private final String when;

    protected WhenCellRenderer(TableCell annotation, CenterRenderer[] children, String[] formatted) {
        super(annotation, children, annotation.var(), formatted, annotation.value().trim(), annotation.type(),
            annotation.rowspan(), annotation.colspan(), annotation.skipCells(),
            annotation.className(), annotation.height(), annotation.width());
        this.when = annotation.when().trim();
    }

    private Boolean whenValue;

    @Override
    protected void setWhen(Boolean value) {
        whenValue = value;
    }

    @Override
    public boolean isWhen(WorkCenterMap centerMap) {
        return getValue(centerMap, when, whenValue, whenSetter);
    }
}
