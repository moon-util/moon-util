package com.moon.processing.decl;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author benshaoye
 */
public class FieldDeclared {

    private final TypeElement thisElement;
    private final TypeElement declareEnclosingElement;
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

    public FieldDeclared(
        TypeElement thisElement, TypeElement declareEnclosingElement, VariableElement fieldElement, String name, String type
    ) {
        this.thisElement = thisElement;
        this.declareEnclosingElement = declareEnclosingElement;
        this.fieldElement = fieldElement;
        this.name = name;
        this.type = type;
    }

    public VariableElement getFieldElement() { return fieldElement; }

    public String getName() { return name; }

    public String getType() { return type; }
}
