package com.moon.processor.utils;

import com.moon.accessor.annotation.Provided;
import com.moon.processor.model.DeclareMethod;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public enum Assert2 {
    ;

    static final String SIMPLE_NAME = Provided.class.getSimpleName();

    public static void assertProvidedMethodParameters(ExecutableElement method) {
        if (!method.getParameters().isEmpty()) {
            String value = String2.format("被（{}）注解的方法返回值不能有参数.", SIMPLE_NAME);
            throw new IllegalStateException(value);
        }
        if (method.getReturnType().getKind() == TypeKind.VOID) {
            String value = String2.format("被（{}）注解的方法返回值不能是 'void'.", SIMPLE_NAME);
            throw new IllegalStateException(value);
        }
    }

    public static boolean assertEffectMethod(DeclareMethod getter, DeclareMethod setter) {
        if (getter.isDefaultMethod()) {
            if (setter != null && !setter.isDefaultMethod()) {
                String msg = "无法定义 setter 方法: {}({})";
                msg = String2.format(msg, setter.getName(), setter.getDeclareType());
                throw new IllegalStateException(msg);
            }
            return false;
        }
        return true;
    }

    public static void assertNonAbstractMethod(DeclareMethod method, String getterOrSetter) {
        if (method.isAbstractMethod()) {
            String msg = "无法定义 {} 方法: {}({})";
            msg = String2.format(msg, getterOrSetter, method.getName(), method.getDeclareType());
            throw new IllegalStateException(msg);
        }
    }

    public static boolean assertAbstractMethod(DeclareMethod method, String getterOrSetter) {
        if (method.isAbstractMethod()) {
            return true;
        }
        String msg = "无法定义 {} 方法: {}({})";
        msg = String2.format(msg, getterOrSetter, method.getName(), method.getDeclareType());
        throw new IllegalStateException(msg);
    }

    /**
     * 抽象类中不允许有多余的 setter 方法，因为这样无法确定目标类型
     *
     * @param allSettersMap
     * @param declaredType
     */
    @SuppressWarnings("all")
    public static void assertNonSetters(Map<String, DeclareMethod> allSettersMap, String declaredType) {
        Map<String, DeclareMethod> settersMap = new HashMap<>(allSettersMap);
        settersMap.remove(declaredType);
        if (settersMap.isEmpty()) {
            return;
        }
        List<DeclareMethod> setters = settersMap.values().stream().filter(it -> !it.isDefaultMethod())
            .collect(Collectors.toList());
        if (setters.isEmpty()) {
            return;
        }
        throw new IllegalStateException(//
            "无法定义 setter 方法: " + setters//
                .stream()//
                .map(s -> String2.format("{}({})", s.getName(), s.getDeclareType())).collect(Collectors.joining(", ")));
    }

    /**
     * 转换器方法不能是抽象的
     *
     * @param element
     */
    public static void assertNotAbstract(ExecutableElement element) {
        if (Test2.isAny(element, Modifier.ABSTRACT)) {
            TypeElement type = (TypeElement) element.getEnclosingElement();
            String clsName = Element2.getQualifiedName(type);
            String msg = String2.format("方法 {}.{} 不能是抽象的", clsName, element);
            throw new IllegalStateException(msg);
        }
    }
}
