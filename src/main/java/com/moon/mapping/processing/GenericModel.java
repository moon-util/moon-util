package com.moon.mapping.processing;

import javax.lang.model.element.TypeParameterElement;

/**
 * @author moonsky
 */
public class GenericModel {

    private final String declare;
    private final String actual;
    private final String bound;

    public GenericModel(TypeParameterElement parameterElement, String actual) {
        this(parameterElement.toString(), actual, parameterElement.getBounds().toString());
    }

    public GenericModel(String declare, String actual, String bound) {
        this.declare = declare;
        this.actual = actual;
        this.bound = bound;
    }

    /**
     * 泛型声明类型，如 M
     * @return
     */
    public String getDeclareType() { return declare; }

    /**
     * 泛型实际使用类型，可为 null
     * @return
     */
    public String getActualType() { return actual; }

    /**
     * 泛型边界，如 M extends Map，默认 Object
     */
    public String getBoundType() { return bound; }

    public String getFinalType() {
        String act = getActualType(), bound = getBoundType();
        return act == null ? bound : act;
    }

    public String getSimpleFinalType() {
        return simpleGenericTypename(getFinalType());
    }

    private final static String simpleGenericTypename(String value) {
        if (value == null) {
            return null;
        }
        int index = value.indexOf('<');
        return index < 0 ? value : value.substring(0, index);
    }
}
