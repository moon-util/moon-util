package com.moon.mapper.processing;

import java.util.*;
import java.util.function.Consumer;

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
        type = StringUtils.isPrimitive(type) || BASIC_TYPES.contains(type) ? PUBLIC : type;
        map.computeIfAbsent(type, k -> new HashMap<>(2)).put(propType, name);
    }

    private final static String PUBLIC = "public";

    private static final Set<String> BASIC_TYPES = new HashSet<>();

    static {
        Consumer<Class<?>> consumer = cls -> BASIC_TYPES.add(cls.getCanonicalName());
        consumer.accept(Object.class);
        consumer.accept(String.class);
        consumer.accept(Integer.class);
        consumer.accept(Long.class);
        consumer.accept(Short.class);
        consumer.accept(Byte.class);
        consumer.accept(Character.class);
        consumer.accept(Boolean.class);
        consumer.accept(Float.class);
        consumer.accept(Double.class);
        consumer.accept(Number.class);
        consumer.accept(CharSequence.class);
        consumer.accept(StringBuilder.class);
        consumer.accept(StringBuffer.class);
        BASIC_TYPES.add("void");
    }
}
