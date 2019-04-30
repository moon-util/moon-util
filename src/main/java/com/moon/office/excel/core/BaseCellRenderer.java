package com.moon.office.excel.core;

import com.moon.office.excel.enums.ValueType;
import com.moon.core.util.runner.Runner;
import com.moon.core.util.runner.RunnerUtil;
import com.moon.core.util.function.IntBiConsumer;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 */
abstract class BaseCellRenderer<T extends Annotation> extends AbstractRenderer<T> {

    private final Runner value;
    private final ValueType valueType;
    private final String skipCells;
    private final String colspan;
    private final String rowspan;
    private final String className;
    private final short height;
    private final int width;

    protected BaseCellRenderer(
        T annotation, CenterRenderer[] children, String var, String[] delimiters,
        String value, ValueType valueType,
        String rowspan, String colspan, String skipCells,
        String className, short height, int width
    ) {
        super(annotation, children, var, delimiters);

        this.value = isZero() ? RunnerUtil.parse(value) : RunnerUtil.parse(value, getDelimiters());
        this.valueType = valueType;
        this.rowspan = rowspan;
        this.colspan = colspan;
        this.skipCells = skipCells;
        this.height = height;
        this.width = width;
        this.className = className;
    }

    protected int colspanValue = NOT_INIT;
    protected int rowspanValue = NOT_INIT;

    private final static IntBiConsumer<BaseCellRenderer> colspanValueSetter
        = (renderer, value) -> renderer.colspanValue = value;
    private final static IntBiConsumer<BaseCellRenderer> rowspanValueSetter
        = (renderer, value) -> renderer.rowspanValue = value;

    public int getColspanValue(WorkCenterMap centerMap, String expression) {
        return getValue(centerMap, expression, colspanValue, colspanValueSetter);
    }

    public int getRowspanValue(WorkCenterMap centerMap, String expression) {
        return getValue(centerMap, expression, rowspanValue, rowspanValueSetter);
    }

    protected int getRowspan(WorkCenterMap centerMap) {
        return getRowspanValue(centerMap, rowspan);
    }

    protected int getColspan(WorkCenterMap centerMap) {
        return getColspanValue(centerMap, colspan);
    }

    protected int getSkips(WorkCenterMap centerMap) {
        return getSkipsValue(centerMap, skipCells);
    }

    @Override
    public WorkCenterMap beforeRender(WorkCenterMap centerMap) {
        centerMap.createNextCell(getColspan(centerMap), getRowspan(centerMap), getSkips(centerMap), valueType);
        centerMap.setCellStyle(className);
        centerMap.setHeight(height);
        centerMap.setWidth(width);
        return centerMap;
    }

    @Override
    public WorkCenterMap afterRender(WorkCenterMap centerMap) {
        centerMap.setCellValue(getValue(centerMap));
        return centerMap;
    }

    protected Object getValue(WorkCenterMap centerMap) {
        return valueType.apply(value.run(centerMap));
    }
}
