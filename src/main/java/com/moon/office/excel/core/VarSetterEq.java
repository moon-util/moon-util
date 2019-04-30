package com.moon.office.excel.core;

import com.moon.core.util.runner.RunnerUtil;

/**
 * @author benshaoye
 */
class VarSetterEq implements VarSetter {
    private final String[] keys;
    private final String expression;

    public VarSetterEq(String[] keys, String expression) {
        this.keys = keys;
        this.expression = expression;
    }

    /**
     * value, key, index, size, first, last
     */
    private final static Object[] objects = {null, 0, 0, 1, true, true, null};

    private static final WorkCenterMap setVar(WorkCenterMap centerMap, String[] keys, Object data) {
        Object[] values = objects;
        centerMap.put(keys[0], data);
        for (int i = 1, len = keys.length; i < len; i++) {
            centerMap.put(keys[i], values[i]);
        }
        return centerMap;
    }

    @Override
    public boolean isEq() {
        return true;
    }

    @Override
    public void beforeSetAndRender(WorkCenterMap centerMap, CenterRenderer target) {
        setVar(centerMap, keys, RunnerUtil.run(expression, centerMap));
    }
}
