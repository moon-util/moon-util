package com.moon.processing.decl;

import com.moon.processing.holder.BaseHolder;
import com.moon.processing.holder.Holders;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;

/**
 * @author benshaoye
 */
public class BaseExecutableDeclared extends BaseHolder {

    private final TypeElement thisElement;

    private final TypeElement enclosingElement;

    private final ExecutableElement executable;

    private final TypeDeclared typeDeclared;

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
        Holders holders,
        TypeElement thisElement,
        TypeElement enclosingElement,
        TypeDeclared typeDeclared,
        Map<String, GenericDeclared> thisGenericMap,
        Map<String, String> parametersMap
    ) {
        super(holders);
        this.thisElement = thisElement;
        this.enclosingElement = enclosingElement;
        this.typeDeclared = typeDeclared;
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
            ParameterDeclared parameterDeclared = new ParameterDeclared(getHolders(),
                thisElement,
                enclosingElement,
                typeDeclared,
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
        Holders holders,
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement executable,
        TypeDeclared typeDeclared,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(holders);
        this.thisElement = thisElement;
        this.enclosingElement = enclosingElement;
        this.typeDeclared = typeDeclared;
        this.executable = executable;
        this.thisGenericMap = thisGenericMap;

        this.thisClassname = thisElement.getQualifiedName().toString();
        this.enclosingClassname = enclosingElement.getQualifiedName().toString();

        List<? extends VariableElement> parameters = executable.getParameters();
        int index = 0, size = parameters.size();
        List<ParameterDeclared> parametersDeclared = new ArrayList<>(size);
        Map<String, ParameterDeclared> parametersMap = new HashMap<>(size);
        for (VariableElement parameter : parameters) {
            ParameterDeclared parameterDeclared = new ParameterDeclared(getHolders(),
                thisElement,
                enclosingElement,
                executable,
                parameter,
                typeDeclared,
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

    public <A extends Annotation> A getAtExecutableAnnotation(Class<A> annotationClass) {
        return getExecutable().getAnnotation(annotationClass);
    }

    public TypeDeclared getTypeDeclared() { return typeDeclared; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public String getThisClassname() { return thisClassname; }

    public String getEnclosingClassname() { return enclosingClassname; }

    public int getParametersCount() { return parametersDeclared.size(); }

    public List<ParameterDeclared> getParametersDeclared() { return parametersDeclared; }

    public Map<String, ParameterDeclared> getParametersMap() { return parametersMap; }
}
