package com.moon.core.util.runner.core;

/**
 * @author benshaoye
 */
class DataObj extends DataConst {
    private DataObj(Object value) {
        super(value);
    }

    @Override
    public boolean isObject() {
        return true;
    }

    final static AsConst valueOf(Object str) {
        AsConst CONST = getValue(str);
        if (CONST == null) {
            CONST = putValue(str, new DataObj(str));
        }
        return CONST;
    }

    final static AsConst tempObj(Object str) {
        return new DataObj(str);
    }
}
