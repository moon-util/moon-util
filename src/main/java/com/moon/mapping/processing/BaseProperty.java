package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
abstract class BaseProperty<M extends BaseMethod> implements Mappable, TypeGetter, Completable {

    /**
     * 字段名
     */
    private final String name;
    /**
     * 所在的声明类，如在类 A 中声明了属性 b，这个就是 A
     */
    private final TypeElement enclosingElement;
    /**
     * 字段声明类型（可能是泛型）{@link #actualType}
     */
    private String declareType;
    /**
     * 字段实际类型，{@link #declareType}
     */
    private String actualType;
    /**
     * 主要 setter
     */
    private M setter;
    /**
     * 主要 getter
     */
    private M getter;
    /**
     * 可能有重写的 setter，这里集中保存
     */
    private List<M> setterArr;
    /**
     * getter 方法不可能被重写，这里是为了和 setter 沿用统一结构
     */
    private List<M> getterArr;
    /**
     * 所有转换器
     */
    private Map<String, M> converterMap;
    /**
     * 所有转换器
     */
    private Map<String, M> providerMap;

    BaseProperty(String name, TypeElement enclosingElement) {
        this.enclosingElement = enclosingElement;
        this.name = name;
    }

    public void setDeclareType(String declareType) { this.declareType = declareType; }

    public void setActualType(String actualType) { this.actualType = actualType; }

    public void setSetter(M setter) { this.setter = setter; }

    public void setGetter(M getter) { this.getter = getter; }

    public List<M> getSetterArr() { return setterArr; }

    public List<M> getGetterArr() { return getterArr; }

    public Map<String, M> getConverterMap() { return converterMap; }

    public Map<String, M> getProviderMap() { return providerMap; }

    public M getSetter() { return setter; }

    public M getGetter() { return getter; }

    public List<M> ensureSetterArr() {
        return setterArr == null ? (setterArr = new ArrayList<>()) : setterArr;
    }

    public List<M> ensureGetterArr() {
        return getterArr == null ? (getterArr = new ArrayList<>()) : getterArr;
    }

    public Map<String, M> ensureConverterMap() {
        return converterMap == null ? (converterMap = new HashMap<>()) : converterMap;
    }

    public Map<String, M> ensureProviderMap() {
        return providerMap == null ? (providerMap = new HashMap<>()) : providerMap;
    }

    @Override
    public String findConverterMethod(String key) {
        M convert = ensureConverterMap().get(key);
        return convert == null ? null : convert.getMethodName();
    }

    @Override
    public String findProviderMethod(String key) {
        M convert = ensureProviderMap().get(key);
        return convert == null ? null : convert.getMethodName();
    }

    @Override
    public boolean isGetterDeclared() {
        M method = getGetter();
        return method != null && method.isDeclaration();
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getActualType() { return actualType; }

    @Override
    public String getDeclareType() { return declareType; }

    public TypeElement getEnclosingElement() { return enclosingElement; }

    public String getEnclosingElementName() {
        return ElemUtils.getQualifiedName(getEnclosingElement());
    }

    public void addSetterMethod(M setter) { ensureSetterArr().add(setter); }

    public void addGetterMethod(M getter) { ensureGetterArr().add(getter); }

    public void putConverterMethod(String key, M convert) { ensureConverterMap().putIfAbsent(key, convert); }

    public void putProviderMethod(String key, M convert) { ensureProviderMap().putIfAbsent(key, convert); }
}
