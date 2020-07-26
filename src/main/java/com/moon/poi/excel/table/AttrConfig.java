package com.moon.poi.excel.table;

/**
 * @author moonsky
 */
final class AttrConfig {

    private final Class targetClass;
    private Attribute attribute;
    private int index;

    AttrConfig(Class targetClass) { this.targetClass = targetClass; }

    public void setAttribute(Attribute attribute, int index) {
        this.attribute = attribute;
        this.index = index;
    }

    public Class getTargetClass() { return targetClass; }

    Attribute getAttribute() { return attribute; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AttrConfig{");
        sb.append("index=").append(index);
        sb.append(", attribute=").append(attribute);
        sb.append('}');
        return sb.toString();
    }
}