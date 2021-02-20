package com.moon.processing.decl;

import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 专门描述 setter/getter 方法
 *
 * @author benshaoye
 */
public class PropertyMethodDeclared extends MethodDeclared {

    private final boolean lombokGenerated;

    public PropertyMethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        this(thisElement, enclosingElement, method, thisGenericMap, false);
    }

    public PropertyMethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        Map<String, GenericDeclared> thisGenericMap,
        boolean lombokGenerated
    ) {
        super(thisElement, enclosingElement, method, thisGenericMap);
        this.lombokGenerated = lombokGenerated;
    }

    protected PropertyMethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        String methodName,
        String returnDeclaredType,
        Map<String, GenericDeclared> thisGenericMap,
        Map<String, String> parametersMap,
        boolean lombokGenerated
    ) {
        super(thisElement, enclosingElement, methodName, returnDeclaredType, thisGenericMap, parametersMap);
        this.lombokGenerated = lombokGenerated;
    }

    public static PropertyMethodDeclared ofLombokGetterGenerated(
        TypeElement thisElement, VariableElement field, Map<String, GenericDeclared> thisGenericMap
    ) {
        String type = field.asType().toString();
        String getterName = String2.toGetterName(Element2.getSimpleName(field), type);
        return new PropertyMethodDeclared(thisElement,
            (TypeElement) field.getEnclosingElement(),
            getterName,
            type,
            thisGenericMap,
            Collections.emptyMap(),
            true);
    }

    public static PropertyMethodDeclared ofLombokSetterGenerated(
        TypeElement thisElement, VariableElement field, Map<String, GenericDeclared> thisGenericMap
    ) {
        String type = field.asType().toString();
        String name = Element2.getSimpleName(field);
        Map<String, String> parametersMap = new LinkedHashMap<>();
        parametersMap.put(name, type);
        String getterName = String2.toSetterName(Element2.getSimpleName(field));
        return new PropertyMethodDeclared(thisElement,
            (TypeElement) field.getEnclosingElement(),
            getterName,
            "void",
            thisGenericMap,
            parametersMap,
            true);
    }
}
