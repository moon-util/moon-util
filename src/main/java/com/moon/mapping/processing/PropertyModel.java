package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * @author moonsky
 */
final class PropertyModel {

    private final String name;
    private VariableElement property;
    private ExecutableElement getter;
    private ExecutableElement setter;

    PropertyModel(String name) {
        this.name = name;
    }

    public ExecutableElement getGetter() { return getter; }

    public ExecutableElement getSetter() { return setter; }

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
            throw new UnsupportedOperationException();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public String getGetterName() {
        if (hasDefaultGetterMethod()) {
            return this.getter.getSimpleName().toString();
        } else if (hasLombokGetterMethod()) {
            throw new UnsupportedOperationException();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static boolean isPublic(ExecutableElement elem) {
        return elem != null && elem.getModifiers().contains(Modifier.PUBLIC);
    }

    private boolean hasDefaultSetterMethod() { return isPublic(getSetter()); }

    private boolean hasDefaultGetterMethod() { return isPublic(getGetter()); }

    private boolean hasLombokSetterMethod() {
        throw new UnsupportedOperationException();
    }

    private boolean hasLombokGetterMethod() {
        throw new UnsupportedOperationException();
    }
}
