package com.moon.office.excel.core;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 */
abstract class BaseRowRenderer<T extends Annotation> extends AbstractRenderer<T> {
    private final String className;
    private final String skipRows;
    private final short height;

    protected BaseRowRenderer(
        T annotation, CenterRenderer[] children, String var, String[] delimiters,
        String className, String skipRows, short height
    ) {
        super(annotation, children, var, delimiters);
        this.className = className;
        this.skipRows = skipRows;
        this.height = height;
    }

    @Override
    public WorkCenterMap beforeRender(WorkCenterMap centerMap) {
        centerMap.createNextRow(getSkipsValue(centerMap, skipRows));
        centerMap.setRowStyle(className);
        centerMap.setHeight(height);
        return centerMap;
    }
}
