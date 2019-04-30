package com.moon.office.excel.core;

import com.moon.core.util.runner.Runner;
import com.moon.core.util.runner.RunnerUtil;

/**
 * @author benshaoye
 */
final class WhenSheetRenderer extends AbstractRenderer<TableSheet> {
    private final Runner sheetName;
    private final String when;

    protected WhenSheetRenderer(TableSheet annotation, CenterRenderer[] children, String[] formatted) {
        super(annotation, children, annotation.var(), formatted);
        String sheetName = annotation.sheetName();
        this.sheetName = isZero() ? RunnerUtil.parse(sheetName)
            : RunnerUtil.parse(sheetName, getDelimiters());

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

    @Override
    public WorkCenterMap beforeRender(WorkCenterMap centerMap) {
        return centerMap.createSheet(sheetName.run(centerMap));
    }
}
