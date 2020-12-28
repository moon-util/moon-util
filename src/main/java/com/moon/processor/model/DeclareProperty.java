package com.moon.processor.model;

import com.moon.mapper.processing.BasicMethod;
import com.moon.mapper.processing.ElemUtils;
import com.moon.mapper.processing.GenericModel;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Generic2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moon.mapper.processing.GenericUtils.findActualType;
import static com.moon.mapper.processing.GenericUtils.findActualTypeOrDeclare;

/**
 * @author benshaoye
 */
public class DeclareProperty {

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

    private final Map<String, Map<String, String>> injectorsMap = new HashMap<>();
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

    public Map<String, Map<String, String>> getInjectorsMap() { return injectorsMap; }

    public Map<String, Map<String, String>> getProvidersMap() { return providersMap; }

    public void addInjector(String fromType, String injectedType, String injectorMethodName) {
        putConverter(getInjectorsMap(), fromType, injectedType, injectorMethodName);
    }

    public void addProvider(String toType, String providedType, String providerMethodName) {
        putConverter(getInjectorsMap(), toType, providedType, providerMethodName);
    }

    public String findInjector(String type, String propertyType) {
        return find(getProvidersMap(), type, propertyType);
    }

    public String findProvider(String type, String propertyType) {
        return find(getProvidersMap(), type, propertyType);
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
        return new DeclareMethod(method, declareType, Generic2.findActual(generics, declareClassname, declareType),
            true);
    }
}
