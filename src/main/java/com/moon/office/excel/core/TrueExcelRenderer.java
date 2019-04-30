package com.moon.office.excel.core;

import com.moon.core.enums.ArraysEnum;
import com.moon.core.lang.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
class TrueExcelRenderer extends AbstractRenderer<TableExcel> {
    private final Map<String, TableStyle> styleMaps;

    protected TrueExcelRenderer(TableExcel annotation, CenterRenderer[] children) {
        super(annotation, children, annotation.var(), ArraysEnum.STRINGS.empty());

        TableStyle[] styles = annotation.styles();
        if (styles.length > 0) {
            Map<String, TableStyle> styleMaps = this.styleMaps = new HashMap<>();
            for (TableStyle style : annotation.styles()) {
                styleMaps.put(StringUtil.requireNotBlank(style.className()), style);
            }
        } else {
            this.styleMaps = null;
        }
    }

    @Override
    public WorkCenterMap beforeRender(WorkCenterMap centerMap) {
        centerMap.setStyleMaps(styleMaps);
        return centerMap;
    }
}
