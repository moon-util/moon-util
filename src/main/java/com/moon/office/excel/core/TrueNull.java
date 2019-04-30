package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
enum TrueNull implements CenterRenderer {
    NULL;

    private final CenterRenderer[] children = new CenterRenderer[0];

    @Override
    public WorkCenterMap render(WorkCenterMap centerMap) {
        return centerMap;
    }

    @Override
    public CenterRenderer[] getChildren() {
        return children;
    }
}
