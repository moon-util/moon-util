package com.moon.processing.decl;

import com.moon.processor.utils.Element2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class ParameterDeclared {

    private final TypeElement thisElement;
    private final TypeElement declaredElement;
    private final ExecutableElement thisExecutable;
    private final ParameEle
    private final Map<String, GenericDeclared> thisGenericMap;
    private final String thisClassname;
    private final String declaredClassname;

    private final int parameterIndex;
    private final String declaredType;
    private final String actualType;

    public ParameterDeclared(
        TypeElement thisElement,
        TypeElement declaredElement,
        ExecutableElement thisExecutable,

        Map<String, GenericDeclared> thisGenericMap
    ) {
        this.thisElement = thisElement;
        this.declaredElement = declaredElement;
        this.thisExecutable = thisExecutable;
        this.thisGenericMap = thisGenericMap;
        this.thisClassname = Element2.getQualifiedName(thisElement);
        String declaredClassname = Element2.getQualifiedName(declaredElement);
        String
        Generic2.mapToActual(thisGenericMap, declaredClassname, )
    }
}
