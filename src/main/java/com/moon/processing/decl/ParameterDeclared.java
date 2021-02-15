package com.moon.processing.decl;

import com.moon.processor.utils.Element2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class ParameterDeclared {

    private final TypeElement thisElement;
    private final TypeElement declaredElement;
    private final ExecutableElement thisExecutable;
    private final VariableElement parameter;
    private final Map<String, GenericDeclared> thisGenericMap;
    private final String thisClassname;
    private final String declaredClassname;
    private final int parameterIndex;
    private final String parameterName;
    private final String declaredType;
    private final String actualType;
    private final String simplifyActualType;

    public ParameterDeclared(
        TypeElement thisElement,
        TypeElement declaredElement,
        ExecutableElement thisExecutable,
        VariableElement parameter,
        int parameterIndex,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        this.thisElement = thisElement;
        this.declaredElement = declaredElement;
        this.thisExecutable = thisExecutable;
        this.thisGenericMap = thisGenericMap;
        this.parameter = parameter;
        this.parameterIndex = parameterIndex;
        this.thisClassname = Element2.getQualifiedName(thisElement);
        String declaredClassname = Element2.getQualifiedName(declaredElement);
        String declaredType = parameter.asType().toString();
        String actualType = Generic2.mapToActual(thisGenericMap, declaredClassname, declaredType);
        this.parameterName = Element2.getSimpleName(parameter);
        this.declaredClassname = declaredClassname;
        this.declaredType = declaredType;
        this.actualType = actualType;
        this.simplifyActualType = Generic2.typeSimplify(actualType);
    }

    public TypeElement getThisElement() { return thisElement; }

    public TypeElement getDeclaredElement() { return declaredElement; }

    public ExecutableElement getThisExecutable() { return thisExecutable; }

    public VariableElement getParameter() { return parameter; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public String getThisClassname() { return thisClassname; }

    public String getDeclaredClassname() { return declaredClassname; }

    public int getParameterIndex() { return parameterIndex; }

    public String getParameterName() { return parameterName; }

    public String getDeclaredType() { return declaredType; }

    public String getActualType() { return actualType; }

    public String getSimplifyActualType() { return simplifyActualType; }
}
