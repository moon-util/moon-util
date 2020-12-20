package com.moon.mapper.processing;

import com.moon.mapper.annotation.IgnoreMode;

import static com.moon.mapper.annotation.IgnoreMode.*;

/**
 * @author benshaoye
 */
class PropertyAttr {

    public final static PropertyAttr DFT = new Dft();

    private final String targetCls;
    private final String field;
     final IgnoreMode ignoreMode;
    private final String format;
    private final String defaultValue;

    PropertyAttr(
        String targetCls, String field, String format, String defaultValue, IgnoreMode ignoreMode
    ) {
        this.field = StringUtils.isBlank(field) ? null : field.trim();
        this.format = StringUtils.isBlank(format) ? null : format;
        this.defaultValue = StringUtils.isEmpty(defaultValue) ? null : defaultValue;
        this.ignoreMode = ignoreMode;
        this.targetCls = targetCls;
    }

    public boolean isIgnoreForward() {
        return ignoreMode == ALL || ignoreMode == FORWARD;
    }

    public boolean isIgnoreBackward() {
        return ignoreMode == ALL || ignoreMode == BACKWARD;
    }

    public String formatValue() { return format; }

    public String defaultValue() { return defaultValue; }

    public String getTargetCls() { return targetCls; }

    public String getField(String fromProperty) {
        return field == null ? fromProperty : field;
    }

    private static class Dft extends PropertyAttr {

        public Dft() { super(null, null, null, null, IgnoreMode.NONE); }

        @Override
        public String getField(String fromProperty) { return fromProperty; }
    }
}
