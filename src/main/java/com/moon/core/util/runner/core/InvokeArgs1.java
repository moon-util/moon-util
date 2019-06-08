package com.moon.core.util.runner.core;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.reflect.MethodUtil;

import java.lang.reflect.Method;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.reflect.MethodUtil.getPublicStaticMethods;

/**
 * @author benshaoye
 */
final class InvokeArgs1 {

    private InvokeArgs1() { noInstanceError(); }

    static class OneStatic implements AsInvoker {

        final Class sourceType;
        final String methodName;
        final AsRunner valuer;

        private Method method;

        OneStatic(Class sourceType, String methodName, AsRunner valuer) {
            this.sourceType = sourceType;
            this.methodName = methodName;
            this.valuer = valuer;
        }

        @Override
        public Object run(Object data) {
            data = valuer.run(data);
            Class type = data.getClass();
            // return MethodUtil.invokeStatic(getMethod(data, type), createParams(data, type));
            throw new UnsupportedOperationException();
        }

        Method getMethod(Object data, Class type) {

            return method;
        }
    }

    static class OneEnsure implements AsInvoker {

        final Method method;

        OneEnsure(Method method) { this.method = method; }

        @Override
        public Object run(Object data) {
            return null;
        }
    }

    static Method staticArgs0(Class type, String name, AsRunner paramValuer) {
        List<Method> ms = getPublicStaticMethods(type, name);
        Method method = null;
        switch (ms.size()) {
            case 1:
                if ((method = ms.get(0)).getParameterCount() == 1) {
                    return method;
                } else {
                    String msg = StringUtil.concat("Can not find method of:", type.getName(), "#(",
                        JoinerUtil.join(method.getParameterTypes(), ","), ")");
                    throw new IllegalArgumentException(msg);
                }
        }
        return null;
    }

    final static AsRunner parse(
        AsRunner paramValuer, AsValuer prev, String name, boolean isStatic
    ) {
        if (isStatic) {
            // 静态方法
            Class sourceType = ((DataLoader) prev).getValue();
            return InvokeOneEnsure.of(paramValuer, sourceType, name);
        } else {
            // 成员方法
            return new InvokeOne(prev, paramValuer, name);
        }
    }
}
