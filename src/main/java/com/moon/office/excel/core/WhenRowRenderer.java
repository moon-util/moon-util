package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
final class WhenRowRenderer extends BaseRowRenderer<TableRow> {
    private final String when;

    protected WhenRowRenderer(TableRow annotation, CenterRenderer[] children, String[] formatted) {
        super(annotation, children, annotation.var(), formatted,
            annotation.className(), annotation.skipRows(), annotation.height());
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
