package com.moon.core.util.runner.core;

import com.moon.core.lang.reflect.MethodUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.moon.core.lang.SupportUtil.matchOne;
import static com.moon.core.lang.reflect.MethodUtil.getPublicStaticMethods;
import static com.moon.core.util.runner.core.ParseInvoker.NONE_PARAM;

/**
 * @author benshaoye
 */
final class Invoke {

    /**
     * 无参方法
     */
    static class NonEnsure extends InvokeBase {

        protected NonEnsure(String methodName) { super(methodName); }

        @Override
        public Object run(Object data) { return MethodUtil.invoke(true, getMethod(data), data); }
    }

    static class NonStatic implements AsInvoker {

        final Method method;

        NonStatic(Method method) {this.method = method;}

        @Override
        public Object run(Object data) { return MethodUtil.invoke(true, method, data); }
    }

    static Method getStaticEmptyParam(Class source, String name) {
        List<Method> methods = MethodUtil.getPublicStaticMethods(source, name);
        for (Method method : methods) {
            if (method.getParameterCount() == 0) {
                return method;
            }
        }
        Method searchedMethod = null;
        Class<?>[] currentParam;
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


    final static AsRunner parseNon(AsValuer prev, String name, boolean isStatic) {
        if (isStatic) {
            // 静态方法
            return new InvokeNoneEnsure(
                matchOne(getPublicStaticMethods(((DataLoader) prev).getValue(), name), NONE_PARAM));
        } else {
            // 成员方法
            return new GetLink(prev, new InvokeNone(name));
        }
    }
}
