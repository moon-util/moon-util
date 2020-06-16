package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;

/**
 * @author benshaoye
 */
final class AttrConfig {

    private final IntAccessor indexer = IntAccessor.of(-1);
    private final Class targetClass;
    private Attribute attribute;
    private int index;

    AttrConfig(Class targetClass) { this.targetClass = targetClass; }


    public void setAttribute(Attribute attribute, int index) {
        this.attribute = attribute;
        this.index = index;
        indexer.increment();
    }

    public Class getTargetClass() { return targetClass; }

    Attribute getAttribute() {
        return attribute;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AttrConfig{");
        sb.append("indexer=").append(indexer);
        sb.append(", index=").append(index);
        sb.append(", attribute=").append(attribute);
        sb.append('}');
        return sb.toString();
    }
}
