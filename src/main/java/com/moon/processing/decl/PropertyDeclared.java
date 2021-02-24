package com.moon.processing.decl;

import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author benshaoye
 */
public class PropertyDeclared {

    private final TypeElement thisElement;

    private final TypeDeclared typeDeclared;

    private final String thisClassname;

    private final String name;

    private final Map<String, GenericDeclared> thisGenericMap;

    private PropertyFieldDeclared fieldDeclared;
    /**
     * 由于 java 方法重载规则， getter 方法只有一个，所以不考虑重载的情况
     * <p>
     * 暂时也不考虑 getXxx(int) 的情况，根据个人经验，从未遇到过这种情况
     * <p>
     * 所以如果实际应用中即使真的出现 getXxx(int) 的 getter，请考虑修改成其他方案
     */
    private PropertyMethodDeclared getter;
    /**
     * 从{@link #typedSetterMap}最终选用的 setter 方法
     *
     * <pre>
     * 1. 如果存在 getter，setter 参数类型与 getter 返回值类型一致
     * 2. 若不存在 getter，存在字段，setter 参数类型与字段类型一致
     * 3. 若没有与字段类型一致的 setter 方法就按照一定规则取一个默认的 setter 方法
     * </pre>
     */
    private PropertyMethodDeclared setter;
    /**
     * setter 方法，要求 setter 方法参数类型和 getter 方法返回值类型一致
     * <p>
     * 若没有 getter 方法，且存在 setter 重载时，setter 方法取用优先规则为:
     * 1. 与字段声明类型一致
     * 2. 按特定顺序排序后的第一个方法（这种排序规则受 jdk 版本可能受 jvm 版本的影响，所以这里自定义顺序）
     */
    private Map<String, PropertyMethodDeclared> typedSetterMap = new LinkedHashMap<>();

    public PropertyDeclared(
        TypeElement thisElement, TypeDeclared typeDeclared, String name, Map<String, GenericDeclared> thisGenericMap
    ) {
        this.thisGenericMap = thisGenericMap;
        this.typeDeclared = typeDeclared;
        this.thisElement = thisElement;
        this.name = name;
        this.thisClassname = Element2.getQualifiedName(thisElement);
    }

    public void withFieldDeclared(VariableElement fieldElement) {
        TypeElement enclosingElement = ((TypeElement) fieldElement.getEnclosingElement());
        setFieldDeclaredIfAbsent(new PropertyFieldDeclared(thisElement,
            enclosingElement,
            fieldElement,
            thisGenericMap));
    }

    public void setFieldDeclaredIfAbsent(PropertyFieldDeclared fieldDeclared) {
        if (this.fieldDeclared == null) {
            this.fieldDeclared = fieldDeclared;
        }
    }

    public void withGetterMethodDeclared(ExecutableElement getterElement) {
        if (this.getter != null) {
            return;
        }
        TypeElement enclosingElement = (TypeElement) getterElement.getEnclosingElement();
        this.getter = new PropertyMethodDeclared(thisElement,
            enclosingElement,
            getterElement,
            getName(),
            typeDeclared,
            thisGenericMap);
    }

    public void withSetterMethodDeclared(
        ExecutableElement setterElement, String actualType
    ) {
        String simplifySetterType = Generic2.typeSimplify(actualType);
        PropertyMethodDeclared setter = typedSetterMap.get(simplifySetterType);
        if (setter != null) {
            return;
        }
        typedSetterMap.put(simplifySetterType,
            new PropertyMethodDeclared(thisElement,
                (TypeElement) setterElement.getEnclosingElement(),
                setterElement,
                getName(),
                typeDeclared,
                thisGenericMap));
    }

    public String getName() { return name; }

    public boolean isWriteable(String setterActualClass) { return typedSetterMap.containsKey(setterActualClass); }

    public boolean isWriteable() { return setter != null; }

    public boolean isReadable() { return getter != null; }

    public TypeElement getThisElement() { return thisElement; }

    public String getThisClassname() { return thisClassname; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public PropertyFieldDeclared getFieldDeclared() { return fieldDeclared; }

    public PropertyMethodDeclared getGetterMethod() { return getter; }

    public String getReffedGetterScript(String instanceName) {
        return instanceName + "." + getGetterMethod().getMethodName() + "()";
    }

    public PropertyMethodDeclared getSetterMethod() { return setter; }

    public String getRefferedSetterScript(String instanceName, String parameterName) {
        return instanceName + "." + getSetterMethod().getMethodName() + "(" + parameterName + ")";
    }

    public Map<String, PropertyMethodDeclared> getTypedSetterMap() { return typedSetterMap; }

    @Override
    public boolean equals(Object o) { return o == this; }

    @Override
    public int hashCode() { return System.identityHashCode(this); }

    public void onCompleted() {
        computeGetterMethod();
        computeSetterMethod();
    }

    private void computeSetterMethod() {
        FieldDeclared field = this.fieldDeclared;
        if (getter == null) {
            if (field != null && Test2.hasLombokSetter(field.getFieldElement())) {
                PropertyMethodDeclared setter = PropertyMethodDeclared.ofLombokSetterGenerated(thisElement,
                    field.getFieldElement(),
                    typeDeclared,
                    thisGenericMap);
                ParameterDeclared parameter = setter.getParameterAt(0);
                typedSetterMap.put(parameter.getSimplifyActualType(), setter);
                this.setter = setter;
            } else {
                this.setter = filterSetterMethod(typedSetterMap);
            }
        } else {
            PropertyMethodDeclared setter = typedSetterMap.get(getter.getReturnActualType());
            if (setter == null && field != null && Test2.hasLombokSetter(field.getFieldElement())) {
                setter = PropertyMethodDeclared.ofLombokSetterGenerated(thisElement,
                    field.getFieldElement(),
                    typeDeclared,
                    thisGenericMap);
                ParameterDeclared parameter = setter.getParameterAt(0);
                typedSetterMap.put(parameter.getSimplifyActualType(), setter);
            }
            this.setter = setter;
        }
    }

    private void computeGetterMethod() {
        if (getter == null) {
            FieldDeclared field = this.fieldDeclared;
            if (field != null && Test2.hasLombokGetter(field.getFieldElement())) {
                this.getter = PropertyMethodDeclared.ofLombokGetterGenerated(thisElement,
                    field.getFieldElement(),
                    typeDeclared,
                    thisGenericMap);
            }
        }
    }

    private static PropertyMethodDeclared filterSetterMethod(Map<String, PropertyMethodDeclared> settersMap) {
        if (settersMap.isEmpty()) {
            return null;
        }
        PropertyMethodDeclared setter = findMethod(settersMap, "boolean,byte,short,char,int,long,float,double", false);
        if (setter != null) {
            return setter;
        }
        setter = findMethod(settersMap, "Boolean,Byte,Short,Character,Integer,Long,Float,Double", true);
        if (setter != null) {
            return setter;
        }
        return new TreeMap<>(settersMap).firstEntry().getValue();
    }

    private static PropertyMethodDeclared findMethod(
        Map<String, PropertyMethodDeclared> settersMap, String classes, boolean langPackage
    ) {
        String[] types = classes.split(",");
        if (langPackage) {
            types = Arrays.stream(types).map(type -> "java.lang." + type).toArray(String[]::new);
        }
        for (String type : types) {
            PropertyMethodDeclared setter = settersMap.get(type);
            if (type != null) {
                return setter;
            }
        }
        return null;
    }

    public String getActualType() {
        if (getter != null) {
            return getter.getReturnActualType();
        }
        if (setter != null) {
            return setter.getReturnActualType();
        }
        return null;
    }
}
