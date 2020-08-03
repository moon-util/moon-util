package com.moon.poi.excel.table;

import com.moon.poi.excel.annotation.style.DefinitionStyle;

import java.util.Map;

/**
 * @author moonsky
 */
final class AttrConfig {

    private final Map<String, Object> definedAllStylesOnTargetClass;
    private final boolean definedDefaultStyle;
    private final Class targetClass;
    private Attribute attribute;
    private int index;

    AttrConfig(Class targetClass, Map<String, Object> definedAllStylesOnTargetClass) {
        this.definedDefaultStyle = definedAllStylesOnTargetClass.containsKey(StyleUtil.classnameOfEmpty(targetClass));
        this.definedAllStylesOnTargetClass = definedAllStylesOnTargetClass;
        this.targetClass = targetClass;

    }

    public void setAttribute(Attribute attribute, int index) {
        this.attribute = attribute;
        this.index = index;
    }

    /**
     * 是否定义全局默认样式，即类上是否定义了 classname 为空的{@link DefinitionStyle}
     *
     * @return
     */
    public boolean isDefinedDefaultStyle() {
        return definedDefaultStyle;
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
