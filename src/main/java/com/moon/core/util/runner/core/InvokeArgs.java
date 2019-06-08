package com.moon.core.util.runner.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
class InvokeArgs {


    /**
     * 解析没有参数调用的静态方法
     *
     * @return
     */
    static Method filterEmptyArgs(List<Method> methods, Class source, String name) {
        for (Method method : methods) {
            if (method.getParameterCount() == 0) {
                return method;
            }
        }
        Class<?>[] currentParam;
        Method searchedMethod = null;
        Class searchedType = null, currentType;
        for (Method method : methods) {
            if (method.isVarArgs() && (currentParam = method.getParameterTypes()).length == 1) {
                currentType = currentParam[0];
                if (searchedType == null || searchedType.isAssignableFrom(currentType)) {
                    searchedType = currentType;
                    searchedMethod = method;
                } else if (!currentType.isAssignableFrom(searchedType)) {
                    String message = "Can not find method of: " + source + "#" + name + "()";
                    throw new IllegalArgumentException(message);
                }
            }
        }
        return Objects.requireNonNull(searchedMethod);
    }
}
