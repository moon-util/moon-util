package com.moon.processor.model;

import com.moon.processor.utils.Test2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DeclareProperty {

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

    public DeclareProperty(String name) { this.name = name; }

    public String getName() { return name; }

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
}
