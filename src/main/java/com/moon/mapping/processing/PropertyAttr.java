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
        this.targetCls = targetCls == null ? null : defaultTargetCls(targetCls);
        this.ignored = ignored;
    }

    private static String defaultTargetCls(String cls) {
        @SuppressWarnings("all")
        boolean isLang = cls.split("\\.").length == 3//
            && (cls.startsWith("java.lang.") || cls.startsWith("java.util."));
        return (isLang || StringUtils.isPrimitive(cls)) ? "void" : cls;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PropertyAttr{");
        sb.append("targetCls='").append(targetCls).append('\'');
        sb.append(", field='").append(field).append('\'');
        sb.append(", ignored=").append(ignored);
        sb.append(", format='").append(format).append('\'');
        sb.append(", defaultValue='").append(defaultValue).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
