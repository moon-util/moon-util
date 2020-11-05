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

    public String getDeclare() { return declare; }

    public String getActual() { return actual; }

    public String getBound() { return bound; }

    static String simpleActual(String value) {
        if (value == null) {
            return null;
        }
        int index = value.indexOf('<');
        return index < 0 ? value : value.substring(0, index);
    }

    public String getValue() {
        String act = getActual(), bound = getBound();
        return act == null ? bound : act;
    }

    public String getSimpleValue() {
        return simpleActual(getValue());
    }

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
