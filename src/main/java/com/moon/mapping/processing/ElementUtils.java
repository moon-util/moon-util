package com.moon.mapping.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author benshaoye
 */
abstract class ElementUtils {

    private ElementUtils() {}

    public static String getFieldDeclareType(VariableElement elem) {
        return stringifyTypeMirror(elem.asType());
    }

    public static String getGetterDeclareType(ExecutableElement elem) {
        return stringifyTypeMirror(elem.getReturnType());
    }

    public static String getSetterDeclareType(ExecutableElement elem) {
        return stringifyTypeMirror(elem.getParameters().get(0).asType());
    }

    public static String stringifyTypeMirror(TypeMirror type) {
        Element typeElem = EnvUtils.getTypes().asElement(type);
        @SuppressWarnings("all")//
        String declareType = typeElem instanceof QualifiedNameable//
                             ? ElementUtils.getQualifiedName((QualifiedNameable) typeElem) //
                             : (typeElem == null ? type.toString() : typeElem.toString());
        return declareType;
    }

    public static String getQualifiedName(QualifiedNameable elem) {
        return elem.getQualifiedName().toString();
    }

    static <T> T cast(Object obj) { return (T) obj; }

    static String toPropertyName(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        return StringUtils.decapitalize(name.substring(name.startsWith("is") ? 2 : 3));
    }

    public static String getSimpleName(Element elem) {
        return elem.getSimpleName().toString();
    }

    public static String getSimpleName(String fullName) {
        int index = fullName.lastIndexOf('.');
        return index < 0 ? fullName : fullName.substring(index + 1);
    }

    public static String getPackageName(Element element) {
        do {
            if (DetectUtils.isPackage(element)) {
                return getQualifiedName((QualifiedNameable) element);
            }
            element = element.getEnclosingElement();
        } while (true);
    }
}
