package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.moon.mapping.processing.ElementUtils.capitalize;

/**
 * @author benshaoye
 */
final class BasicProperty extends BaseProperty<BasicMethod> implements Mappable {

    private VariableElement field;

    BasicProperty(String name, TypeElement enclosingElement) {
        super(name, enclosingElement);
    }

    public VariableElement getField() { return field; }

    public void setField(VariableElement field) { this.field = field; }

    /*
    custom
     */

    @Override
    public boolean hasSetterMethod() {
        return hasPublicDefaultSetter() || hasLombokSetter();
    }

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
    public boolean hasGetterMethod() {
        return hasPublicDefaultGetter() || hasLombokGetter();
    }

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
    private boolean hasLombokSetter() {
        return DetectUtils.hasLombokSetter(getField());
    }

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
    private boolean hasLombokGetter() {
        return DetectUtils.hasLombokGetter(getField());
    }
}
