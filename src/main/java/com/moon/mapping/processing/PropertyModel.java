package com.moon.mapping.processing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import static com.moon.mapping.processing.ProcessingUtil.isPublic;

/**
 * @author moonsky
 */
final class PropertyModel {

    private final String name;
    private VariableElement property;
    private ExecutableElement getter;
    private ExecutableElement setter;

    PropertyModel(String name) { this.name = name; }

    void setProperty(VariableElement property) { this.property = property; }

    void setGetter(ExecutableElement getter) { this.getter = getter; }

    void setSetter(ExecutableElement setter) { this.setter = setter; }

    public ExecutableElement getGetter() { return getter; }

    public ExecutableElement getSetter() { return setter; }

    public VariableElement getProperty() { return property; }

    public boolean hasPublicSetterMethod() {
        return hasDefaultSetterMethod() || hasLombokSetterMethod();
    }

    public boolean hasPublicGetterMethod() {
        return hasDefaultGetterMethod() || hasLombokGetterMethod();
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
    static String capitalize(String name) {
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
}
