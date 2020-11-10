package com.moon.mapping.processing;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.moon.mapping.processing.GenericUtils.findActualType;
import static com.moon.mapping.processing.StringUtils.capitalize;

/**
 * @author benshaoye
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
final class BasicProperty extends BaseProperty<BasicMethod> {

    private final TypeElement thisElement;

    private VariableElement field;

    BasicProperty(String name, TypeElement enclosingElement, TypeElement thisElement) {
        super(name, enclosingElement);
        this.thisElement = thisElement;
    }

    public VariableElement getField() { return field; }

    public void setField(VariableElement field, Map<String, GenericModel> genericMap) {
        this.field = field;
        String type = field.asType().toString();
        setDeclareType(type);
        setActualType(findActualType(genericMap, type));
    }

    public void setSetter(ExecutableElement setter, Map<String, GenericModel> genericMap) {
        String declareType = setter.getParameters().get(0).asType().toString();
        addSetterMethod(toMethod(declareType, setter, genericMap));
    }

    public void setGetter(ExecutableElement getter, Map<String, GenericModel> genericMap) {
        String declareType = getter.getReturnType().toString();
        addGetterMethod(toMethod(declareType, getter, genericMap));
    }

    private BasicMethod toMethod(String declareType, ExecutableElement method, Map<String, GenericModel> generics) {
        return new BasicMethod(method, declareType, findActualType(generics, declareType), true);
    }

    /*
    custom
     */

    @Override
    public boolean hasSetterMethod() { return hasPublicDefaultSetter() || hasLombokSetter(); }

    @Override
    public String getSetterName() {
        if (hasPublicDefaultSetter()) {
            return getSetter().getMethodName();
        }
        if (hasLombokSetter()) {
            return "set" + capitalize(getName());
        }
        return null;
    }

    @Override
    public String getSetterFinalType() {
        if (hasPublicDefaultSetter()) {
            return getGetter().getComputedType();
        }
        if (hasLombokSetter()) {
            return getComputedType();
        }
        return null;
    }

    @Override
    public String getWrappedSetterType() {
        TypeMirror typeMirror = null;
        if (isPrimitiveSetter()) {
            if (hasPublicDefaultSetter()) {
                typeMirror = getSetter().getElem().getParameters().get(0).asType();
            }
            if (hasLombokSetter()) {
                typeMirror = getField().asType();
            }
        }
        if (typeMirror == null) {
            return getSetterFinalType();
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

    public TypeMirror getSetterTypeElem() {
        if (hasPublicDefaultGetter()) {
            return getSetter().getElem().getParameters().get(0).asType();
        }
        if (hasLombokGetter()) {
            return getField().asType();
        }
        return null;
    }

    @Override
    public boolean isPrimitiveSetter() {
        if (hasPublicDefaultSetter()) {
            return getSetter().getElem().getParameters().get(0).asType().getKind().isPrimitive();
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
    @Override
    public boolean hasGetterMethod() { return hasPublicDefaultGetter() || hasLombokGetter(); }

    /**
     * getter method name
     */
    @Override
    public String getGetterName() {
        if (hasPublicDefaultGetter()) {
            return getGetter().getMethodName();
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
    @Override
    public String getGetterFinalType() {
        if (hasPublicDefaultGetter()) {
            return getGetter().getComputedType();
        }
        if (hasLombokGetter()) {
            return getComputedType();
        }
        return null;
    }

    @Override
    public String getWrappedGetterType() {
        TypeMirror typeMirror = null;
        if (isPrimitiveGetter()) {
            if (hasPublicDefaultGetter()) {
                BasicMethod setter = getGetter();
                typeMirror = setter.getElem().getReturnType();
            }
            if (hasLombokGetter()) {
                typeMirror = getField().asType();
            }
        }
        if (typeMirror == null) {
            return getGetterFinalType();
        }
        if (typeMirror.getKind().isPrimitive()) {
            return toWrapperType(typeMirror.getKind());
        }
        return typeMirror.toString();
    }

    public TypeMirror getGetterTypeElem() {
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
    @Override
    public boolean isPrimitiveGetter() {
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
     * 主动声明的 setter 方法
     */
    private boolean hasPublicDefaultSetter() {
        BasicMethod setter = getSetter();
        return setter != null && DetectUtils.isPublic(setter.getElem());
    }

    /**
     * lombok 生成的 setter 方法
     */
    private boolean hasLombokSetter() { return DetectUtils.hasLombokSetter(getField()); }

    /**
     * 主动声明的 public getter 方法
     */
    private boolean hasPublicDefaultGetter() {
        BasicMethod getter = getGetter();
        return getter != null && DetectUtils.isPublic(getter.getElem());
    }

    /**
     * lombok 生成的 getter 方法
     */
    private boolean hasLombokGetter() { return DetectUtils.hasLombokGetter(getField()); }

    @Override
    public void onCompleted() {
        final String propertyType = getComputedType();
        if (propertyType == null) {
            onMissingField();
        } else {
            onPresentField(propertyType);
        }
    }

    private void onMissingField() {
        List<BasicMethod> getterArr = ensureGetterArr();
        if (getterArr.isEmpty()) {
            onMissingGetter();
        } else {
            BasicMethod getter = getterArr.remove(0);
            setGetter(getter);
            Iterator<BasicMethod> setterItr = ensureSetterArr().iterator();
            while (setterItr.hasNext()) {
                BasicMethod method = setterItr.next();
                if (Objects.equals(method.getComputedType(), getter.getComputedType())) {
                    this.setSetter(method);
                    setterItr.remove();
                    break;
                }
            }
        }
    }

    private void onMissingGetter() {
    }

    private void onPresentField(final String propertyType) {
        Iterator<BasicMethod> getterItr = ensureGetterArr().iterator();
        while (getterItr.hasNext()) {
            BasicMethod method = getterItr.next();
            if (Objects.equals(method.getComputedType(), propertyType)) {
                this.setGetter(method);
                getterItr.remove();
                break;
            }
        }

        Iterator<BasicMethod> setterItr = ensureSetterArr().iterator();
        while (setterItr.hasNext()) {
            BasicMethod method = setterItr.next();
            if (Objects.equals(method.getComputedType(), propertyType)) {
                this.setSetter(method);
                setterItr.remove();
                break;
            }
        }
    }
}
