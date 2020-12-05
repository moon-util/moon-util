package com.moon.mapping.processing;

import java.util.Objects;

/**
 * @author moonsky
 */
final class GenericModel {

    private final String declare;
    private final String actual;
    private final String bound;

    public GenericModel(String declare, String actual, String bound) {
        this.declare = declare;
        this.actual = actual;
        this.bound = bound;
    }

    /**
     * 泛型声明类型，如 M
     *
     * @return
     */
    public String getDeclareType() { return declare; }

    /**
     * 泛型实际使用类型，可为 null
     *
     * @return
     */
    public String getActualType() { return actual; }

    /**
     * 泛型边界，如 M extends Map，默认 Object
     */
    public String getBoundType() { return bound; }

    public String getFinalType() {
        String act = getActualType(), bound = getBoundType();
        // 这里的 equals 判断是多余的，而且不会走
        return act == null || Objects.equals(act, declare) ? bound : act;
    }

    public String getSimpleFinalType() { return ElemUtils.toSimpleGenericTypename(getFinalType()); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenericModel{");
        sb.append("declare='").append(declare).append('\'');
        sb.append(", actual='").append(actual).append('\'');
        sb.append(", bound='").append(bound).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
