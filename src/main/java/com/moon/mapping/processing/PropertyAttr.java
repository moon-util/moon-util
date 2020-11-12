package com.moon.mapping.processing;

/**
 * @author benshaoye
 */
class PropertyAttr {

    public final static PropertyAttr DFT = new PropertyDft();

    private final String targetCls;
    private final String field;
    private final boolean ignored;

    PropertyAttr(String targetCls, String field, boolean ignored) {
        this.field = StringUtils.isBlank(field) ? null : field.trim();
        this.targetCls = targetCls;
        this.ignored = ignored;
    }

    public boolean isIgnored() { return ignored; }

    public String getTargetCls() { return targetCls; }

    public String getField(String fromProperty) {
        return field == null ? fromProperty : field;
    }

    private final static class PropertyDft extends PropertyAttr {

        public PropertyDft() { super(null, null, false); }

        @Override
        public String getField(String fromProperty) { return fromProperty; }
    }
}
