package com.moon.processor.model;

import com.moon.mapper.annotation.IgnoreMode;

/**
 * @author benshaoye
 */
public class IgnoredModel {

    private final IgnoreMode mode;
    private final String targetCls;

    public IgnoredModel(IgnoreMode mode, String targetCls) {
        this.targetCls = targetCls;
        this.mode = mode;
    }

    public IgnoreMode getMode() { return mode; }

    public String getTargetCls() { return targetCls; }
}
