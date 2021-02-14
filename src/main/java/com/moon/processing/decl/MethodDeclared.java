package com.moon.processing.decl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class MethodDeclared {

    private final TypeElement thisElement;

    private final TypeElement enclosingElement;

    private final ExecutableElement method;

    private final Map<String, GenericDeclared> thisGenericMap;

    public MethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        this.thisElement = thisElement;
        this.enclosingElement = enclosingElement;
        this.method = method;
        this.thisGenericMap = Generic2.from(method, thisGenericMap);
    }
}
