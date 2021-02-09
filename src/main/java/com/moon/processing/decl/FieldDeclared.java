package com.moon.processing.decl;

import javax.lang.model.element.VariableElement;

/**
 * @author benshaoye
 */
public class FieldDeclared {

    /**
     * 字段声明
     */
    private final VariableElement fieldElement;
    /**
     * 字段名
     */
    private final String name;
    /**
     * 字段实际类型
     * <p>
     * 这里不列举泛型声明类型
     */
    private final String type;

    public FieldDeclared(VariableElement fieldElement, String name, String type) {
        this.fieldElement = fieldElement;
        this.name = name;
        this.type = type;
    }

    public VariableElement getFieldElement() { return fieldElement; }

    public String getName() { return name; }

    public String getType() { return type; }
}
