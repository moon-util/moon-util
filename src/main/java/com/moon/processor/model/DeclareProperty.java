package com.moon.processor.model;

import com.moon.processor.Completable;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Generic2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

/**
 * @author benshaoye
 */
public class DeclareProperty implements Completable {

    /**
     * 所在的声明类，如在类 A 中声明了属性 b，这个就是 A
     */
    private final TypeElement enclosingElement;
    /**
     * 如: 这里是 A
     * <pre>
     * class B {
     *     private int a;
     * }
     * class A extends B {
     *     private int age;
     * }
     * </pre>
     */
    private final TypeElement thisElement;
    /**
     * 字段声明
     */
    private VariableElement field;

    private final String name;
    /**
     * 字段声明类型（可能是泛型）
     */
    private String declareType;
    /**
     * 字段实际类型
     */
    private String actualType;

    private DeclareMethod getter;
    private DeclareMethod setter;

    private final List<DeclareMethod> getters = new ArrayList<>();
    private final List<DeclareMethod> setters = new ArrayList<>();

    private final Map<String, DeclareMapping> fieldMappings = new HashMap<>();
    private final Map<String, DeclareMapping> setterMappings = new HashMap<>();
    private final Map<String, DeclareMapping> getterMappings = new HashMap<>();

    /**
     * targetClass : propertyType : methodName
     */
    private final Map<String, Map<String, String>> injectorsMap = new HashMap<>();

    /**
     * targetClass : propertyType : methodName
     */
    private final Map<String, Map<String, String>> providersMap = new HashMap<>();

    public DeclareProperty(String name, TypeElement enclosingElement, TypeElement thisElement) {
        this.enclosingElement = enclosingElement;
        this.thisElement = thisElement;
        this.name = name;
    }

    public String getName() { return name; }

    public TypeElement getEnclosingElement() { return enclosingElement; }

    public TypeElement getThisElement() { return thisElement; }

    public VariableElement getField() { return field; }

    public void setField(VariableElement field) { this.field = field; }

    public String getDeclareType() { return declareType; }

    public void setDeclareType(String declareType) { this.declareType = declareType; }

    public String getActualType() { return actualType; }

    public void setActualType(String actualType) { this.actualType = actualType; }

    public DeclareMethod getGetter() { return getter; }

    public void setGetter(DeclareMethod getter) { this.getter = getter; }

    public DeclareMethod getSetter() { return setter; }

    public void setSetter(DeclareMethod setter) { this.setter = setter; }

    public List<DeclareMethod> getGetters() { return getters; }

    public List<DeclareMethod> getSetters() { return setters; }

    public Map<String, DeclareMapping> getFieldMappings() { return fieldMappings; }

    public Map<String, DeclareMapping> getGetterMappings() { return getterMappings; }

    public Map<String, DeclareMapping> getSetterMappings() { return setterMappings; }

    public Map<String, Map<String, String>> getInjectorsMap() { return injectorsMap; }

    public Map<String, Map<String, String>> getProvidersMap() { return providersMap; }

    public void addInjector(String fromType, String injectedType, String injectorMethodName) {
        putConverter(getInjectorsMap(), fromType, injectedType, injectorMethodName);
    }

    public void addProvider(String toType, String providedType, String providerMethodName) {
        putConverter(getInjectorsMap(), toType, providedType, providerMethodName);
    }

    public DeclareMapping getForwardMapping(String targetClass) {
        return getMapping(getGetterMappings(), getFieldMappings(), targetClass);
    }

    public DeclareMapping getBackwardMapping(String targetClass) {
        return getMapping(getSetterMappings(), getFieldMappings(), targetClass);
    }

    private static DeclareMapping getMapping(
        Map<String, DeclareMapping> atMethod, Map<String, DeclareMapping> atField, String targetClass
    ) {
        DeclareMapping mapping = atMethod.get(targetClass);
        return mapping == null ? atField.getOrDefault(targetClass, DeclareMapping.DFT) : mapping;
    }

    public void addFieldMapping(DeclareMapping mapping) { putMapping(getFieldMappings(), mapping); }

    public void addSetterMapping(DeclareMapping mapping) { putMapping(getSetterMappings(), mapping); }

    public void addGetterMapping(DeclareMapping mapping) { putMapping(getGetterMappings(), mapping); }

    private static void putMapping(Map<String, DeclareMapping> mappings, DeclareMapping mapping) {
        mappings.put(mapping.getTargetCls(), mapping);
    }

    public String findInjector(String type, String propertyType) {
        return find(getProvidersMap(), type, propertyType);
    }

    public String findProvider(String type, String propertyType) {
        return find(getProvidersMap(), type, propertyType);
    }

    public Map<String, String> findInjectorsFor(String targetClass) {
        return getInjectorsMap().getOrDefault(targetClass, Collections.emptyMap());
    }

    public Map<String, String> findProvidersFor(String targetClass) {
        return getProvidersMap().getOrDefault(targetClass, Collections.emptyMap());
    }

    private static String find(Map<String, Map<String, String>> map, String type, String propertyType) {
        Map<String, String> maybePresentMap = map.getOrDefault(type, map.get(PUBLIC));
        return maybePresentMap == null ? null : maybePresentMap.get(propertyType);
    }

    private static void putConverter(
        Map<String, Map<String, String>> map, String type, String propType, String name
    ) {
        type = Test2.isBasicType(type) ? PUBLIC : type;
        map.computeIfAbsent(type, k -> new HashMap<>(2)).put(propType, name);
    }

    private final static String PUBLIC = "public";

    public void setField(VariableElement field, Map<String, DeclareGeneric> genericMap) {
        setField(field);
        String declareType = Element2.getFieldDeclareType(field);
        TypeElement nameable = (TypeElement) field.getEnclosingElement();
        String declareClassname = Element2.getQualifiedName(nameable);
        setDeclareType(declareType);
        setActualType(Generic2.findActual(genericMap, declareClassname, declareType));
    }

    public void setSetter(ExecutableElement setter, Map<String, DeclareGeneric> genericMap) {
        String declareType = Element2.getSetterDeclareType(setter);
        setters.add(toMethod(declareType, setter, genericMap));
    }

    public void setGetter(ExecutableElement getter, Map<String, DeclareGeneric> genericMap) {
        String declareType = Element2.getGetterDeclareType(getter);
        getters.add(toMethod(declareType, getter, genericMap));
    }

    private DeclareMethod toMethod(String declareType, ExecutableElement method, Map<String, DeclareGeneric> generics) {
        TypeElement nameable = (TypeElement) method.getEnclosingElement();
        String declareClassname = Element2.getQualifiedName(nameable);
        return new DeclareMethod(method,
            declareType,
            Generic2.findActual(generics, declareClassname, declareType),
            true);
    }

    @Override
    public void onCompleted() {
        // TODO 将字段、getter、setter 对应
    }
}
