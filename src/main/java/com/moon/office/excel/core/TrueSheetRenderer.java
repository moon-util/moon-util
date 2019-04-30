package com.moon.office.excel.core;

import com.moon.core.util.runner.Runner;
import com.moon.core.util.runner.RunnerUtil;

/**
 * @author benshaoye
 */
final class TrueSheetRenderer extends AbstractRenderer<TableSheet> {
    private final Runner sheetName;

    protected TrueSheetRenderer(TableSheet annotation, CenterRenderer[] children, String[] formatted) {
        super(annotation, children, annotation.var(), formatted);
        String sheetName = annotation.sheetName();
        this.sheetName = isZero() ? RunnerUtil.parse(sheetName)
            : RunnerUtil.parse(sheetName, getDelimiters());
    }

    @Override
    public WorkCenterMap beforeRender(WorkCenterMap centerMap) {
        return centerMap.createSheet(sheetName.run(centerMap));
    }
}
