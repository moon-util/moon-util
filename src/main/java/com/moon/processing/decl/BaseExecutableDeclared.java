package com.moon.processing.decl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * @author benshaoye
 */
public class BaseExecutableDeclared {

    private final TypeElement thisElement;

    private final TypeElement enclosingElement;

    private final ExecutableElement executable;

    private final Map<String, GenericDeclared> thisGenericMap;

    private final String thisClassname;

    private final String enclosingClassname;
    /**
     * 所有参数，按顺序
     */
    private final List<ParameterDeclared> parametersDeclared;
    /**
     * 所有参数，按名称索引
     */
    private final Map<String, ParameterDeclared> parametersMap;

    protected final String parametersTypesSignature;

    public BaseExecutableDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement executable,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        this.thisElement = thisElement;
        this.enclosingElement = enclosingElement;
        this.executable = executable;
        this.thisGenericMap = thisGenericMap;

        this.thisClassname = thisElement.getQualifiedName().toString();
        this.enclosingClassname = enclosingElement.getQualifiedName().toString();

        List<? extends VariableElement> parameters = executable.getParameters();
        int index = 0, size = parameters.size();
        List<ParameterDeclared> parametersDeclared = new ArrayList<>(size);
        Map<String, ParameterDeclared> parametersMap = new HashMap<>(size);
        for (VariableElement parameter : parameters) {
            ParameterDeclared parameterDeclared = new ParameterDeclared(thisElement,
                enclosingElement,
                executable,
                parameter,
                index++,
                thisGenericMap);
            parametersDeclared.add(parameterDeclared);
            parametersMap.put(parameterDeclared.getParameterName(), parameterDeclared);
        }
        this.parametersDeclared = Collections.unmodifiableList(parametersDeclared);
        this.parametersMap = Collections.unmodifiableMap(parametersMap);
        this.parametersTypesSignature = parametersDeclared.stream().map(ParameterDeclared::getSimplifyActualType)
            .collect(joining(","));
    }

    public TypeElement getThisElement() { return thisElement; }

    public TypeElement getEnclosingElement() { return enclosingElement; }

    public ExecutableElement getExecutable() { return executable; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public String getThisClassname() { return thisClassname; }

    public String getEnclosingClassname() { return enclosingClassname; }

    public List<ParameterDeclared> getParametersDeclared() { return parametersDeclared; }

    public Map<String, ParameterDeclared> getParametersMap() { return parametersMap; }
}
