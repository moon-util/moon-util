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
    private String propertyTypename;
    private String setterTypename;
    private String getterTypename;

    PropertyModel(String name) { this(name, null); }

    PropertyModel(String name, TypeElement thisType) {
        this.name = name;
        this.thisType = thisType;
    }

    void setProperty(VariableElement property) { this.property = property; }

    void setGetter(ExecutableElement getter) { this.getter = getter; }

    void setSetter(ExecutableElement setter) { this.setter = setter; }

    public void setSetterTypename(String setterTypename) { this.setterTypename = setterTypename; }

    public void setPropertyTypename(String propertyTypename) {
        this.propertyTypename = propertyTypename;
    }

    public void setGetterTypename(String getterTypename) {
        this.getterTypename = getterTypename;
    }

    public ExecutableElement getGetter() { return getter; }

    public ExecutableElement getSetter() { return setter; }

    public VariableElement getProperty() { return property; }

    public String getSetterTypename() { return setterTypename; }

    public String getGetterTypename() { return getterTypename; }

    public String getPropertyTypename() { return propertyTypename; }

    public boolean isTypeof(Object obj) { return this.thisType == obj; }

    public boolean hasProperty() { return this.property != null; }

    /**
     * 是否存在 public setter
     *
     * @return
     */
    public boolean hasPublicSetterMethod() {
        return hasDefaultSetterMethod() || hasLombokSetterMethod();
    }

    /**
     * 是否存在 public getter
     *
     * @return
     */
    public boolean hasPublicGetterMethod() {
        return hasDefaultGetterMethod() || hasLombokGetterMethod();
    }

    /**
     * setter 参数类型是否是基本数据类型
     */
    public boolean isPrimitiveSetterMethod() {
        if (hasPublicSetterMethod()) {
            return wasDefaultPrimitiveSetterMethod();
        } else if (hasLombokSetterMethod()) {
            return wasLombokPrimitiveSetterMethod();
        }
        return false;
    }

    /**
     * getter 返回值是否是基本数据类型
     */
    public boolean isPrimitiveGetterMethod() {
        if (hasPublicGetterMethod()) {
            return wasDefaultPrimitiveGetterMethod();
        } else if (hasLombokGetterMethod()) {
            return wasLombokPrimitiveGetterMethod();
        }
        return false;
    }

    /**
     * 返回声明的属性类型，可能是实际类，也可能是泛型
     */
    public String getDeclaredPropertyType() {
        if (hasProperty()) {
            return this.getProperty().asType().toString();
        } else {
            return null;
        }
    }

    /**
     * 返回属性实际类型
     */
    public String getActualPropertyType() {
        return getPropertyTypename() == null ? getDeclaredPropertyType() : getPropertyTypename();
    }

    /**
     * 返回 getter 方法返回值类型，可能是实际类，也可能是泛型
     */
    public String getDeclaredGetterType() {
        if (hasPublicGetterMethod()) {
            return getGetter().getReturnType().toString();
        }
        return null;
    }

    /**
     * getter 实际返回值类型
     */
    public String getActualGetterType() {
        if (getGetterTypename() != null) {
            return getGetterTypename();
        } else if (hasPublicGetterMethod()) {
            return getGetter().getReturnType().toString();
        } else if (hasLombokGetterMethod()) {
            return getActualPropertyType();
        }
        return null;
    }

    /**
     * 获取声明在 setter 方法上的方法参数类型，可能是实际类，也可能是泛型
     */
    public String getDeclaredSetterType() {
        if (hasPublicSetterMethod()) {
            VariableElement var = getSetter().getParameters().get(0);
            return var.asType().toString();
        } else {
            return null;
        }
    }

    /**
     * setter 参数实际类型
     */
    public String getActualSetterType() {
        if (getSetterTypename() != null) {
            return getSetterTypename();
        } else if (hasPublicSetterMethod()) {
            VariableElement var = getSetter().getParameters().get(0);
            return var.asType().toString();
        } else if (hasLombokSetterMethod()) {
            return getActualPropertyType();
        }
        return null;
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
        if (mirror.getKind().isPrimitive()) {
            return toWrapperType(mirror.getKind());
        }
        return mirror.toString();
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

    private boolean wasDefaultPrimitiveGetterMethod() {
        return getGetter().getReturnType().getKind().isPrimitive();
    }

    private boolean wasLombokPrimitiveSetterMethod() {
        try {
            return getProperty().asType().getKind().isPrimitive();
        } catch (NullPointerException e) {
            String type = getSetter().getEnclosingElement().toString();
            throw new IllegalStateException(name + "\t ~~ " + getProperty() + "\t >> " + type, e);
        }
    }

    private boolean wasLombokPrimitiveGetterMethod() {
        try {
            return getProperty().asType().getKind().isPrimitive();
        } catch (NullPointerException e) {
            String type = getGetter().getEnclosingElement().toString();
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
