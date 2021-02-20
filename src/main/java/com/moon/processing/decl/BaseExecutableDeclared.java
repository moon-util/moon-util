package com.moon.processing.decl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

import static java.util.Collections.emptyMap;
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
    /**
     * 参数或构造器所声明的类名
     */
    private final String enclosingClassname;
    /**
     * 所有参数，按顺序
     */
    private final List<ParameterDeclared> parametersDeclared;
    /**
     * 所有参数，按名称索引
     */
    private final Map<String, ParameterDeclared> parametersMap;
    /**
     * 参数签名，如:
     * <p>
     * java.util.String,java.util.List,int
     * <p>
     * 不会出现带有泛型的类型，如: java.util.List&lt;java.lang.String>
     */
    protected final String parametersTypesSignature;

    /**
     * 主要用于 lombok 自动生成的 getter/setter
     *
     * @param thisElement
     * @param enclosingElement
     * @param thisGenericMap
     * @param parametersMap
     */
    protected BaseExecutableDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        Map<String, GenericDeclared> thisGenericMap,
        Map<String, String> parametersMap
    ) {
        this.thisElement = thisElement;
        this.enclosingElement = enclosingElement;
        this.executable = null;
        this.thisGenericMap = thisGenericMap;

        this.thisClassname = thisElement.getQualifiedName().toString();
        this.enclosingClassname = enclosingElement.getQualifiedName().toString();

        Map<String, String> typesMap = parametersMap == null ? emptyMap() : parametersMap;
        int index = 0, size = typesMap.size();
        List<ParameterDeclared> parametersDeclared = new ArrayList<>(size);
        Map<String, ParameterDeclared> parametersIndexedMap = new HashMap<>(size);
        for (Map.Entry<String, String> stringEntry : typesMap.entrySet()) {
            String name = stringEntry.getKey(), type = stringEntry.getValue();
            ParameterDeclared parameterDeclared = new ParameterDeclared(thisElement,
                enclosingElement,
                type,
                name,
                index++,
                thisGenericMap);
            parametersDeclared.add(parameterDeclared);
            parametersIndexedMap.put(parameterDeclared.getParameterName(), parameterDeclared);
        }
        this.parametersDeclared = parametersDeclared;
        this.parametersMap = parametersIndexedMap;
        this.parametersTypesSignature = parametersDeclared.stream().map(ParameterDeclared::getSimplifyActualType)
            .collect(joining(","));
    }

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

    public ParameterDeclared getParameterAt(int index) { return parametersDeclared.get(index); }

    public ParameterDeclared getParameterOf(String parameterName) { return parametersMap.get(parameterName); }

    public TypeElement getThisElement() { return thisElement; }

    public TypeElement getEnclosingElement() { return enclosingElement; }

    public ExecutableElement getExecutable() { return executable; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public String getThisClassname() { return thisClassname; }

    public String getEnclosingClassname() { return enclosingClassname; }

    public List<ParameterDeclared> getParametersDeclared() { return parametersDeclared; }

    public Map<String, ParameterDeclared> getParametersMap() { return parametersMap; }
}
