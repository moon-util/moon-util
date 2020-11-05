package com.moon.mapping.processing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.moon.mapping.processing.ProcessingUtil.isPublic;

/**
 * @author moonsky
 */
final class PropertyModel {

    private final String name;
    private final TypeElement thisType;
    private VariableElement property;
    private ExecutableElement getter;
    private ExecutableElement setter;
    private String setterTypename;

    PropertyModel(String name) { this(name, null); }

    PropertyModel(String name, TypeElement thisType) {
        this.name = name;
        this.thisType = thisType;
    }

    void setProperty(VariableElement property) { this.property = property; }

    void setGetter(ExecutableElement getter) { this.getter = getter; }

    void setSetter(ExecutableElement setter) { this.setter = setter; }

    public void setSetterTypename(String setterTypename) { this.setterTypename = setterTypename; }

    public ExecutableElement getGetter() { return getter; }

    public ExecutableElement getSetter() { return setter; }

    public VariableElement getProperty() { return property; }

    public String getSetterTypename() { return setterTypename; }

    public boolean isTypeof(Object obj) { return this.thisType == obj; }

    public boolean hasPublicSetterMethod() {
        return hasDefaultSetterMethod() || hasLombokSetterMethod();
    }

    public boolean hasPublicGetterMethod() {
        return hasDefaultGetterMethod() || hasLombokGetterMethod();
    }

    public boolean isPrimitiveSetterMethod() {
        if (hasPublicSetterMethod()) {
            return wasDefaultPrimitiveSetterMethod();
        } else if (hasLombokSetterMethod()) {
            return wasLombokPrimitiveSetterMethod();
        }
        return false;
    }

    public String getDefaultSetterType() {
        VariableElement var = getSetter().getParameters().get(0);
        return var.asType().toString();
    }

    public String getEffectiveSetterType() {
        return getSetterTypename() == null ? getDefaultSetterType() : getSetterTypename();
    }

    /**
     * 返回基本数据类型的包装类
     */
    public String getWrappedSetterType() {
        TypeMirror mirror;
        if (wasDefaultPrimitiveSetterMethod()) {
            mirror = getSetter().getParameters().get(0).asType();
        } else {
            mirror = getProperty().asType();
        }
        return toWrapperType(mirror.getKind());
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

    public String getSetterName() {
        if (hasDefaultSetterMethod()) {
            return this.setter.getSimpleName().toString();
        } else if (hasLombokSetterMethod()) {
            VariableElement elem = getProperty();
            String name = elem.getSimpleName().toString();
            return "set" + capitalize(name);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getGetterName() {
        if (hasDefaultGetterMethod()) {
            return this.getter.getSimpleName().toString();
        } else if (hasLombokGetterMethod()) {
            VariableElement elem = getProperty();
            String name = elem.getSimpleName().toString();
            if (elem.asType().getKind() == TypeKind.BOOLEAN) {
                return "is" + capitalize(name);
            }
            return "get" + capitalize(name);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private boolean hasDefaultSetterMethod() { return isPublic(getSetter()); }

    private boolean hasDefaultGetterMethod() { return isPublic(getGetter()); }

    private boolean wasDefaultPrimitiveSetterMethod() {
        VariableElement setterParam = getSetter().getParameters().get(0);
        return setterParam.asType().getKind().isPrimitive();
    }

    private boolean wasLombokPrimitiveSetterMethod() {
        try {
            return getProperty().asType().getKind().isPrimitive();
        } catch (NullPointerException e) {
            String type = getSetter().getEnclosingElement().toString();
            throw new IllegalStateException(name + "\t ~~ " + getProperty() + "\t >> " + type, e);
        }
    }

    private boolean hasLombokSetterMethod() {
        if (ProcessingUtil.isImportedLombok()) {
            VariableElement var = getProperty();
            if (var == null) {
                return false;
            }
            Setter setter = var.getAnnotation(Setter.class);
            if (setter != null) {
                return setter.value() == AccessLevel.PUBLIC;
            } else {
                Element element = var.getEnclosingElement();
                return element.getAnnotation(Data.class) != null;
            }
        }
        return false;
    }

    private boolean hasLombokGetterMethod() {
        if (ProcessingUtil.isImportedLombok()) {
            VariableElement var = getProperty();
            if (var == null) {
                return false;
            }
            Getter getter = var.getAnnotation(Getter.class);
            if (getter != null) {
                return getter.value() == AccessLevel.PUBLIC;
            } else {
                Element element = var.getEnclosingElement();
                return element.getAnnotation(Data.class) != null;
            }
        }
        return false;
    }

    @SuppressWarnings("all")
    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1) {
            if (Character.isUpperCase(name.charAt(1)) &&//
                Character.isUpperCase(name.charAt(0))) {
                return name;
            }
            if (Character.isUpperCase(name.charAt(1)) &&//
                Character.isLowerCase(name.charAt(0))) {
                return name;
            }
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PropertyModel{");
        sb.append("name='").append(name).append('\'');
        sb.append(", property=").append(property);
        sb.append(", getter=").append(getter);
        sb.append(", setter=").append(setter);
        sb.append('}');
        return sb.toString();
    }
}
