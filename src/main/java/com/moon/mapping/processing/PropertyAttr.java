package com.moon.mapping.processing;

import com.moon.mapping.annotation.IgnoreMode;

import static com.moon.mapping.annotation.IgnoreMode.*;

/**
 * @author benshaoye
 */
class PropertyAttr {

    public final static PropertyAttr DFT = new Dft();

    private final String targetCls;
    private final String field;
    private final IgnoreMode ignoreType;
    private final String format;
    private final String defaultValue;

    PropertyAttr(
        String targetCls, String field, String format, String defaultValue, IgnoreMode ignoreType
    ) {
        this.field = StringUtils.isBlank(field) ? null : field.trim();
        this.format = StringUtils.isBlank(format) ? null : format;
        this.defaultValue = StringUtils.isEmpty(defaultValue) ? null : defaultValue;
        this.targetCls = targetCls == null ? null : defaultTargetCls(targetCls);
        this.ignoreType = ignoreType;
    }

    private static String defaultTargetCls(String cls) {
        @SuppressWarnings("all") boolean isLang = cls.split("\\.").length == 3//
            && (cls.startsWith("java.lang.") || cls.startsWith("java.util."));
        return (isLang || StringUtils.isPrimitive(cls)) ? "void" : cls;
    }

    public boolean isIgnoreForward() {
        return ignoreType == ALL || ignoreType == FORWARD;
    }

    public boolean isIgnoreBackward() {
        return ignoreType == ALL || ignoreType == BACKWARD;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PropertyAttr{");
        sb.append("targetCls='").append(targetCls).append('\'');
        sb.append(", field='").append(field).append('\'');
        sb.append(", ignoreType=").append(ignoreType);
        sb.append(", format='").append(format).append('\'');
        sb.append(", defaultValue='").append(defaultValue).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
