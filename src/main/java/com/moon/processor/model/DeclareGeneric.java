package com.moon.processor.model;

import java.util.Objects;

/**
 * @author benshaoye
 */
public class DeclareGeneric {

    /**
     * 泛型声明类型
     */
    private final String declare;
    /**
     * 泛型实际类型
     */
    private final String actual;
    /**
     * 泛型边界
     */
    private final String bound;
    /**
     * 有效类，结合 declare、actual、bound 计算出来的，功能最后使用
     * 一般不直接用 declare、actual、bound，如: java.util.List&lt;String&gt;
     */
    private final String effectType;
    /**
     * 如: java.util.List
     */
    private final String simpleEffectType;

    public DeclareGeneric(String declare, String actual, String bound) {
        this.declare = declare;
        this.actual = actual;
        this.bound = bound;
        boolean isBound = actual == null || Objects.equals(actual, declare);
        this.effectType = isBound ? bound : actual;
        this.simpleEffectType = toSimpleGenericTypename(this.effectType);
    }

    public String getDeclare() { return declare; }

    public String getActual() { return actual; }

    public String getBound() { return bound; }

    public String getEffectType() { return effectType; }

    public String getSimpleEffectType() { return simpleEffectType; }

    private static String toSimpleGenericTypename(String value) {
        if (value == null) {
            return null;
        }
        int index = value.indexOf('<');
        return index < 0 ? value : value.substring(0, index);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeclareGeneric{");
        sb.append("declare='").append(declare).append('\'');
        sb.append(", actual='").append(actual).append('\'');
        sb.append(", bound='").append(bound).append('\'');
        sb.append(", effectType='").append(effectType).append('\'');
        sb.append(", simpleEffectType='").append(simpleEffectType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
