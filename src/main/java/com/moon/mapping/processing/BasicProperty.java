package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.moon.mapping.processing.GenericUtils.findActualType;
import static com.moon.mapping.processing.StringUtils.capitalize;

/**
 * @author benshaoye
 */
final class BasicProperty extends BaseProperty<BasicMethod> {

    private final TypeElement thisElement;

    private VariableElement field;

    BasicProperty(String name, TypeElement enclosingElement, TypeElement thisElement) {
        super(name, enclosingElement);
        this.thisElement = thisElement;
    }

    public VariableElement getField() { return field; }

    @Override
    public String getThisClassname() {
        return ElementUtils.getQualifiedName(thisElement);
    }

    public void setField(VariableElement field, Map<String, GenericModel> genericMap) {
        this.field = field;
        String declareType = ElementUtils.getFieldDeclareType(field);
        setDeclareType(declareType);
        setActualType(findActualType(genericMap, declareType));
    }

    public void setSetter(ExecutableElement setter, Map<String, GenericModel> genericMap) {
        String declareType = ElementUtils.getSetterDeclareType(setter);
        addSetterMethod(toMethod(declareType, setter, genericMap));
    }

    public void setGetter(ExecutableElement getter, Map<String, GenericModel> genericMap) {
        String declareType = ElementUtils.getGetterDeclareType(getter);
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
        return ifHasSetter(() -> {
            return getSetter().getMethodName();
        }, () -> "set" + capitalize(getName()));
    }

    @Override
    public String getSetterActualType() {
        return ifHasSetter(BaseTypeGetter::getActualType);
    }

    @Override
    public String getSetterDeclareType() {
        return ifHasSetter(BaseTypeGetter::getDeclareType);
    }

    @Override
    public String getSetterFinalType() {
        return ifHasSetter(BaseTypeGetter::getComputedType);
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
            return StringUtils.toWrappedType(typeMirror.getKind().toString().toLowerCase());
        }
        return typeMirror.toString();
    }

    public TypeMirror getSetterTypeMirror() {
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
        TypeMirror type = getSetterTypeMirror();
        return type != null && type.getKind().isPrimitive();
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

    @Override
    public String getGetterActualType() {
        return ifHasGetter(BaseTypeGetter::getActualType);
    }

    @Override
    public String getGetterDeclareType() {
        return ifHasGetter(BaseTypeGetter::getDeclareType);
    }

    /**
     * getter return type
     */
    @Override
    public String getGetterFinalType() {
        return ifHasGetter(BaseTypeGetter::getComputedType);
    }

    @Override
    public String getWrappedGetterType() {
        TypeMirror typeMirror = null;
        if (isPrimitiveGetter()) {
            typeMirror = getGetterTypeMirror();
        }
        if (typeMirror == null) {
            return getGetterFinalType();
        }
        if (typeMirror.getKind().isPrimitive()) {
            return StringUtils.toWrappedType(typeMirror.getKind().toString().toLowerCase());
        }
        return typeMirror.toString();
    }

    public TypeMirror getGetterTypeMirror() {
        return ifHasGetter(() -> getGetter().getElem().getReturnType(), () -> getField().asType());
    }

    /**
     * 是否是基本数据类型 getter
     */
    @Override
    public boolean isPrimitiveGetter() {
        TypeMirror type = getGetterTypeMirror();
        return type != null && type.getKind().isPrimitive();
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

    private <T> T ifHasSetter(Supplier<T> dftGetter, Supplier<T> lombokGetter) {
        if (hasPublicDefaultSetter()) { return dftGetter.get(); }
        if (hasLombokSetter()) { return lombokGetter.get(); }
        return null;
    }

    private <T> T ifHasSetter(Function<BaseTypeGetter, T> getter) {
        if (hasPublicDefaultSetter()) { return getter.apply(getSetter()); }
        if (hasLombokSetter()) { return getter.apply(this); }
        return null;
    }

    private <T> T ifHasGetter(Supplier<T> dftGetter, Supplier<T> lombokGetter) {
        if (hasPublicDefaultGetter()) { return dftGetter.get(); }
        if (hasLombokGetter()) { return lombokGetter.get(); }
        return null;
    }

    private <T> T ifHasGetter(Function<BaseTypeGetter, T> getter) {
        if (hasPublicDefaultGetter()) { return getter.apply(getGetter()); }
        if (hasLombokGetter()) { return getter.apply(this); }
        return null;
    }

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
        List<BasicMethod> setterArr = ensureSetterArr();
        if (getterArr.isEmpty()) {
            if (setterArr.size() == 1) {
                this.setSetter(setterArr.remove(0));
            }
        } else {
            BasicMethod getter = getterArr.remove(0);
            setGetter(getter);
            Iterator<BasicMethod> setterItr = setterArr.iterator();
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
