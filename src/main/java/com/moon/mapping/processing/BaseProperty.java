package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author benshaoye
 */
abstract class BaseProperty<M extends BaseMethod> implements Mappable, BaseTypeGetter, Completable {

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

    public M getSetter() { return setter; }

    public M getGetter() { return getter; }

    public List<M> ensureSetterArr() {
        return setterArr == null ? (setterArr = new ArrayList<>()) : setterArr;
    }

    public List<M> ensureGetterArr() {
        return getterArr == null ? (getterArr = new ArrayList<>()) : getterArr;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getActualType() { return actualType; }

    @Override
    public String getDeclareType() { return declareType; }

    public TypeElement getEnclosingElement() { return enclosingElement; }

    public String getEnclosingElementName() {
        return ElementUtils.getQualifiedName(getEnclosingElement());
    }

    public void addSetterMethod(M setter) { ensureSetterArr().add(setter); }

    public void addGetterMethod(M getter) { ensureGetterArr().add(getter); }
}
