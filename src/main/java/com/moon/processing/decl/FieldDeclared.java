package com.moon.processing.decl;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Map;

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

    private final Map<String, GenericDeclared> thisGenericMap;
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
    /**
     * 字段声明类型
     */
    private final String declaredType;

    public FieldDeclared(
        TypeElement thisElement,
        TypeElement declareEnclosingElement,
        VariableElement fieldElement,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        this.thisElement = thisElement;
        this.declareEnclosingElement = declareEnclosingElement;
        this.thisGenericMap = thisGenericMap;
        this.fieldElement = fieldElement;
        this.name = fieldElement.getSimpleName().toString();

        String enclosingClass = declareEnclosingElement.getQualifiedName().toString();
        String declaredType = fieldElement.asType().toString();
        String actualType = Generic2.mapToActual(thisGenericMap, enclosingClass, declaredType);
        this.declaredType = declaredType;
        this.type = actualType;
    }

    public VariableElement getFieldElement() { return fieldElement; }

    public String getName() { return name; }

    public String getType() { return type; }

    public TypeElement getThisElement() { return thisElement; }

    public TypeElement getDeclareEnclosingElement() { return declareEnclosingElement; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public String getDeclaredType() { return declaredType; }
}
