package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
interface CenterRenderer {
    CenterRenderer[] EMPTY = new CenterRenderer[0];

    WorkCenterMap render(WorkCenterMap centerMap);

    default CenterRenderer[] getChildren() {
        return EMPTY;
    }

    default WorkCenterMap beforeRender(WorkCenterMap centerMap) {
        return centerMap;
    }

    default WorkCenterMap afterRender(WorkCenterMap centerMap) {
        return centerMap;
    }

    default boolean isWhen(WorkCenterMap centerMap) {
        return true;
    }
}
