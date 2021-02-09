package com.moon.processing.decl;

import java.util.Objects;

/**
 * @author benshaoye
 */
public class GenericDeclared {

    /**
     * 泛型声明类型，如：T
     */
    private final String declare;
    /**
     * 泛型实际类型：如：java.lang.String
     */
    private final String actual;
    /**
     * 泛型边界，如：java.util.function.Supplier&lt;T>
     * <p>
     * T 的边界为: java.lang.Object
     */
    private final String bound;
    /**
     * 有效类，结合 declare、actual、bound 计算出来的，功能最后使用
     * 一般不直接用 declare、actual、bound
     * <p>
     * 而是使用{@code effectType}
     */
    private final String effectType;
    /**
     * 如: java.util.List&lt;java.lang.String>  返回 java.util.List
     */
    private final String simpleType;

    public GenericDeclared(String declare, String actual, String bound) {
        this.declare = declare;
        this.actual = actual;
        this.bound = bound;
        boolean isBound = actual == null || Objects.equals(actual, declare);
        this.effectType = isBound ? bound : actual;
        this.simpleType = toSimpleGenericTypename(this.effectType);
    }

    public String getDeclare() { return declare; }

    public String getActual() { return actual; }

    public String getBound() { return bound; }

    public String getEffectType() { return effectType; }

    public String getSimpleType() { return simpleType; }

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
        sb.append(", simpleEffectType='").append(simpleType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
