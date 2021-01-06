package com.moon.processor.model;

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
    private final Map<String, DeclareMethod> setters = new HashMap<>();

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

    public Map<String, DeclareMethod> getSetters() { return setters; }

    public Map<String, DeclareMapping> getFieldMappings() { return fieldMappings; }

    public Map<String, DeclareMapping> getGetterMappings() { return getterMappings; }

    public Map<String, DeclareMapping> getSetterMappings() { return setterMappings; }

    public Map<String, Map<String, String>> getInjectorsMap() { return injectorsMap; }

    public Map<String, Map<String, String>> getProvidersMap() { return providersMap; }

    public String getFinalActualType() {
        DeclareMethod method = getGetter();
        if (method != null) {
            return method.getActualType();
        }
        method = getSetter();
        if (method != null) {
            return method.getActualType();
        }
        return null;
    }

    public void addInjector(String fromType, String injectedType, String injectorMethodName) {
        putConverter(getInjectorsMap(), fromType, injectedType, injectorMethodName);
    }

    public void addProvider(String toType, String providedType, String providerMethodName) {
        putConverter(getProvidersMap(), toType, providedType, providerMethodName);
    }

    public DeclareMapping getForwardMapping(TypeElement targetClass) {
        return getForwardMapping(Element2.getQualifiedName(targetClass));
    }

    public DeclareMapping getBackwardMapping(TypeElement targetClass) {
        return getBackwardMapping(Element2.getQualifiedName(targetClass));
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
        if (mapping == null) { mapping = atMethod.get(PUBLIC); }
        if (mapping == null) { mapping = atField.get(targetClass); }
        return mapping == null ? atField.getOrDefault(PUBLIC, DeclareMapping.DFT) : mapping;
    }

    public void addFieldMapping(DeclareMapping mapping) { putMapping(getFieldMappings(), mapping); }

    public void addSetterMapping(DeclareMapping mapping) { putMapping(getSetterMappings(), mapping); }

    public void addGetterMapping(DeclareMapping mapping) { putMapping(getGetterMappings(), mapping); }

    private static void putMapping(Map<String, DeclareMapping> mappings, DeclareMapping mapping) {
        String mappingClass = Test2.isBasicType(mapping.getTargetCls()) ? PUBLIC : mapping.getTargetCls();
        mappings.put(mappingClass, mapping);
    }

    public String findInjector(String type, String propertyType) { return find(getInjectorsMap(), type, propertyType); }

    public String findProvider(String type, String propertyType) { return find(getProvidersMap(), type, propertyType); }

    public Map<String, String> findInjectorsFor(TypeElement targetClass) {
        return findInjectorsFor(Element2.getQualifiedName(targetClass));
    }

    public Map<String, String> findProvidersFor(TypeElement targetClass) {
        return findProvidersFor(Element2.getQualifiedName(targetClass));
    }

    public Map<String, String> findInjectorsFor(String targetClass) {
        return convertersFor(getInjectorsMap(), targetClass);
    }

    public Map<String, String> findProvidersFor(String targetClass) {
        return convertersFor(getProvidersMap(), targetClass);
    }

    private static String find(Map<String, Map<String, String>> map, String type, String propertyType) {
        return convertersFor(map, type).get(propertyType);
    }

    private static Map<String, String> convertersFor(Map<String, Map<String, String>> map, String type) {
        Map<String, String> maybePresentMap = map.get(type);
        if (maybePresentMap == null) {
            maybePresentMap = new HashMap<>();
        }
        map.getOrDefault(PUBLIC, Collections.emptyMap()).forEach(maybePresentMap::putIfAbsent);
        return maybePresentMap;
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
        setActualType(Generic2.mappingToActual(genericMap, declareClassname, declareType));
    }

    public void setSetter(ExecutableElement setter, Map<String, DeclareGeneric> genericMap) {
        DeclareMethod method = toMethod(Element2.getSetterDeclareType(setter), setter, genericMap);
        setters.put(method.getActualType(), method);
    }

    public void setGetter(ExecutableElement getter, Map<String, DeclareGeneric> genericMap) {
        String declareType = Element2.getGetterDeclareType(getter);
        getters.add(toMethod(declareType, getter, genericMap));
    }

    private DeclareMethod toMethod(String declareType, ExecutableElement method, Map<String, DeclareGeneric> generics) {
        TypeElement nameable = (TypeElement) method.getEnclosingElement();
        String declareClassname = Element2.getQualifiedName(nameable);
        String actualType = Generic2.mappingToActual(generics, declareClassname, declareType);
        return new DeclareMethod(method, declareType, actualType, true, false);
    }

    @Override
    public void onCompleted() {
        DeclareMethod setter;
        VariableElement elementField = getField();
        List<DeclareMethod> getters = getGetters();
        Map<String, DeclareMethod> settersMap = getSetters();

        // 当不存在 getter/setter 时，检查和设置 lombok getter/setter
        if (getters.isEmpty() && Test2.hasLombokGetter(elementField)) {
            String getterName = Element2.getLombokGetterName(elementField);
            getters.add(new DeclareMethod(null, getterName, getDeclareType(), getActualType(), false, true));
        }
        if (settersMap.isEmpty() && Test2.hasLombokSetter(elementField)) {
            String setterName = Element2.getLombokSetterName(elementField);
            DeclareMethod m = new DeclareMethod(null, setterName, getDeclareType(), getActualType(), false, true);
            settersMap.put(getActualType(), m);
        }

        // 执行映射
        if (!getters.isEmpty()) {
            DeclareMethod getter = getters.get(0);
            this.setGetter(getter);
            setter = settersMap.get(getter.getActualType());
            if (setter != null) {
                this.setSetter(setter);
                return;
            }
        }
        // if missing matches setter method
        // 当 setter 重载时，不同 jdk 以及不同版本对默认处理方式不一样
        // 故这里自定义了默认 setter 处理方式
        setter = settersMap.get(getActualType());
        if (setter == null) {
            setter = filterSetterMethod(settersMap);
        }
        this.setSetter(setter);
    }

    private static DeclareMethod filterSetterMethod(Map<String, DeclareMethod> settersMap) {
        if (settersMap.isEmpty()) {
            return null;
        }
        DeclareMethod setter = findMethod(settersMap, "boolean,byte,short,char,int,long,float,double", false);
        if (setter != null) {
            return setter;
        }
        setter = findMethod(settersMap, "Boolean,Byte,Short,Character,Integer,Long,Float,Double", true);
        if (setter != null) {
            return setter;
        }
        return new TreeMap<>(settersMap).firstEntry().getValue();
    }

    private static DeclareMethod findMethod(Map<String, DeclareMethod> settersMap, String classes, boolean lang) {
        String[] types = classes.split(",");
        if (lang) {
            types = Arrays.stream(types).map(type -> "java.lang." + type).toArray(String[]::new);
        }
        for (String type : types) {
            DeclareMethod setter = settersMap.get(type);
            if (type != null) {
                return setter;
            }
        }
        return null;
    }
}
