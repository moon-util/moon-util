package com.moon.mapping.processing;

/**
 * @author benshaoye
 */
class PropertyAttr {

    public final static PropertyAttr DFT = new Dft();

    private final String targetCls;
    private final String field;
    private final boolean ignored;
    private final String format;
    private final String defaultValue;

    PropertyAttr(String targetCls, String field, String format, String defaultValue, boolean ignored) {
        this.field = StringUtils.isBlank(field) ? null : field.trim();
        this.format = StringUtils.isBlank(format) ? null : format;
        this.defaultValue = StringUtils.isEmpty(defaultValue) ? null : defaultValue;
        this.targetCls = targetCls;
        this.ignored = ignored;
    }

    public boolean isIgnored() { return ignored; }

    public String formatValue() { return format; }

    public String defaultValue() { return defaultValue; }

    public String getTargetCls() { return targetCls; }

    public String getField(String fromProperty) {
        return field == null ? fromProperty : field;
    }

    private static class Dft extends PropertyAttr {

        public Dft() { super(null, null, null, null, false); }

        @Override
        public String getField(String fromProperty) { return fromProperty; }
    }
}
