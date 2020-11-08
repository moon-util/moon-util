package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

import static com.moon.mapping.processing.ElementUtils.capitalize;

/**
 * @author moonsky
 */
class PropertyDetail {

    /**
     * 字段名
     */
    private final String name;

    /**
     * this 指向的类，总是指向最终子类
     * <p>
     * 如 A extends B，不论是 A 或者是 B 中的属性，这总是指向 A
     */
    private final TypeElement thisElem;

    /**
     * 所在的声明类，{@link #thisElem}中的例子中，
     * A 中的属性这里就指向 A，B 中的属性这里就指向 B
     */
    private final TypeElement declareElem;
    /**
     * 字段
     */
    private VariableElement field;
    /**
     * 字段声明类型（可能是泛型）{@link #actualType}
     */
    private String declareType;
    /**
     * 字段实际类型，{@link #declareType}
     */
    private String actualType;
    /**
     * 与字段名、字段类型对应一致的 setter 方法
     */
    private MethodDetail setter;
    /**
     * setter 方法重载，与{@link #setter}同名但类型不同的方法
     */
    private List<MethodDetail> setters;
    /**
     * 与字段名、字段类型对应一致的 getter 方法
     */
    private MethodDetail getter;
    /**
     * getter 方法重载，与{@link #getter}同名但类型不同的方法
     */
    private List<MethodDetail> getters;

    PropertyDetail(String name, TypeElement thisElem, TypeElement declareElem) {
        this.name = name;
        this.thisElem = thisElem;
        this.declareElem = declareElem;
    }

    /*
    sets
     */

    void setVariableElem(VariableElement field, Map<String, GenericModel> generics) {
        this.field = field;
        String type = field.asType().toString();
        this.declareType = type;
        GenericModel model = generics.get(type);
        this.actualType = model == null ? null : model.getSimpleFinalType();
    }

    void setSetter(ExecutableElement elem, Map<String, GenericModel> generics) {
        String declareType = elem.getParameters().get(0).asType().toString();
        ensureSetter().add((new MethodDetail(elem, declareType, generics)));
    }

    void setGetter(ExecutableElement elem, Map<String, GenericModel> generics) {
        String declareType = elem.getReturnType().toString();
        ensureGetter().add((new MethodDetail(elem, declareType, generics)));
    }

    /*
    getter & setter
     */

    String getName() { return name; }

    public TypeElement getThisElem() { return thisElem; }

    public TypeElement getDeclareElem() { return declareElem; }

    VariableElement getField() { return field; }

    String getDeclareType() { return declareType; }

    String getActualType() { return actualType; }

    MethodDetail getSetter() { return setter; }

    List<MethodDetail> getSetters() { return setters; }

    private List<MethodDetail> ensureSetter() { return getSetters() == null ? (this.setters = new ArrayList<>()) : getSetters(); }

    MethodDetail getGetter() { return getter; }

    List<MethodDetail> getGetters() { return getters; }

    private List<MethodDetail> ensureGetter() { return getGetters() == null ? (this.getters = new ArrayList<>()) : getGetters(); }

    /*
    custom
     */

    public boolean hasSetterMethod() {
        return hasPublicDefaultSetter() || hasLombokSetter();
    }

    public String gotSetterName() {
        if (hasPublicDefaultSetter()) {
            return getSetter().getElem().getSimpleName().toString();
        }
        if (hasLombokSetter()) {
            return "set" + capitalize(getName());
        }
        return null;
    }

    public String gotSetterType() {
        if (hasPublicDefaultSetter()) {
            return getSetter().getFactActualType();
        }
        if (hasLombokSetter()) {
            return getFactActualType();
        }
        return null;
    }

    public String gotWrappedSetterType() {
        TypeMirror typeMirror = null;
        if (wasPrimitiveSetter()) {
            if (hasPublicDefaultSetter()) {
                MethodDetail setter = getSetter();
                typeMirror = setter.getElem().getParameters().get(0).asType();
            }
            if (hasLombokSetter()) {
                typeMirror = getField().asType();
            }
        }
        if (typeMirror == null) {
            return gotSetterType();
        }
        if (typeMirror.getKind().isPrimitive()) {
            return toWrapperType(typeMirror.getKind());
        }
        return typeMirror.toString();
    }

    private static String toWrapperType(TypeKind kind) {
        switch (kind) {
            case INT:
                return Integer.class.getName();
            case CHAR:
                return Character.class.getName();
            default:
                return capitalize(kind.name().toLowerCase());
        }
    }

    public TypeMirror gotSetterTypeElem() {
        if (hasPublicDefaultGetter()) {
            return getSetter().getElem().getParameters().get(0).asType();
        }
        if (hasLombokGetter()) {
            return getField().asType();
        }
        return null;
    }

    public boolean wasPrimitiveSetter() {
        if (hasPublicDefaultSetter()) {
            MethodDetail setter = getSetter();
            return setter.getElem().getParameters().get(0).asType().getKind().isPrimitive();
        }
        if (hasLombokSetter()) {
            VariableElement var = getField();
            return var != null && var.asType().getKind().isPrimitive();
        }
        return false;
    }

    /**
     * 是否有 getter
     */
    public boolean hasGetterMethod() {
        return hasPublicDefaultGetter() || hasLombokGetter();
    }

    /**
     * getter method name
     */
    public String gotGetterName() {
        if (hasPublicDefaultGetter()) {
            return getGetter().getElem().getSimpleName().toString();
        }
        if (hasLombokGetter()) {
            VariableElement var = getField();
            if (var.asType().getKind() == TypeKind.BOOLEAN) {
                return "is" + capitalize(getName());
            }
            return "get" + capitalize(getName());
        }
        return null;
    }

    /**
     * getter return type
     */
    public String gotGetterType() {
        if (hasPublicDefaultGetter()) {
            return getGetter().getFactActualType();
        }
        if (hasLombokGetter()) {
            return getFactActualType();
        }
        return null;
    }

    public String gotWrappedGetterType() {
        TypeMirror typeMirror = null;
        if (wasPrimitiveGetter()) {
            if (hasPublicDefaultGetter()) {
                MethodDetail setter = getGetter();
                typeMirror = setter.getElem().getReturnType();
            }
            if (hasLombokGetter()) {
                typeMirror = getField().asType();
            }
        }
        if (typeMirror == null) {
            return gotGetterType();
        }
        if (typeMirror.getKind().isPrimitive()) {
            return toWrapperType(typeMirror.getKind());
        }
        return typeMirror.toString();
    }

    public TypeMirror gotGetterTypeElem() {
        if (hasPublicDefaultGetter()) {
            return getGetter().getElem().getReturnType();
        }
        if (hasLombokGetter()) {
            return getField().asType();
        }
        return null;
    }

    /**
     * 是否是基本数据类型 getter
     */
    public boolean wasPrimitiveGetter() {
        if (hasPublicDefaultGetter()) {
            return getGetter().getElem().getReturnType().getKind().isPrimitive();
        }
        if (hasLombokGetter()) {
            VariableElement var = getField();
            return var != null && var.asType().getKind().isPrimitive();
        }
        return false;
    }

    /**
     * 实际字段类型: 有泛型返回泛型对应的实际类型，否则返回声明的字段类型
     */
    private String getFactActualType() {
        return getActualType() == null ? getDeclareType() : getActualType();
    }

    /**
     * 主动声明的 setter 方法
     */
    private boolean hasPublicDefaultSetter() {
        MethodDetail setter = getSetter();
        return setter != null && DetectUtils.isPublic(setter.getElem());
    }

    /**
     * lombok 生成的 setter 方法
     */
    private boolean hasLombokSetter() {
        return DetectUtils.hasLombokSetter(getField());
    }

    /**
     * 主动声明的 public getter 方法
     */
    private boolean hasPublicDefaultGetter() {
        MethodDetail getter = getGetter();
        return getter != null && DetectUtils.isPublic(getter.getElem());
    }

    /**
     * lombok 生成的 getter 方法
     */
    private boolean hasLombokGetter() {
        return DetectUtils.hasLombokGetter(getField());
    }

    void onParseCompleted() {
        List<MethodDetail> getters = getGetters();
        if (getters != null) {
            Iterator<MethodDetail> gettersIterator = getters.iterator();
            while (gettersIterator.hasNext()) {
                MethodDetail detail = gettersIterator.next();
                if (Objects.equals(detail.getFactActualType(), getFactActualType())) {
                    this.getter = detail;
                    gettersIterator.remove();
                    break;
                }
            }
        }
        List<MethodDetail> setters = getSetters();
        if (setters != null) {
            Iterator<MethodDetail> settersIterator = setters.iterator();
            while (settersIterator.hasNext()) {
                MethodDetail detail = settersIterator.next();
                if (Objects.equals(detail.getFactActualType(), getFactActualType())) {
                    this.setter = detail;
                    settersIterator.remove();
                    break;
                }
            }
        }
    }
}
