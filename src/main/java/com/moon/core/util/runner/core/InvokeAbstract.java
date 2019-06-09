package com.moon.core.util.runner.core;

import com.moon.core.util.TypeUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.moon.core.lang.StringUtil.concat;
import static com.moon.core.lang.reflect.MethodUtil.*;

/**
 * @author benshaoye
 */
abstract class InvokeAbstract {

    static List<Method> memberMethods(Class source, String name) {
        return getPublicMemberMethods(source, name);
    }

    static List<Method> staticMethods(Class source, String name) {
        return getPublicStaticMethods(source, name);
    }

    static Method memberArgs0(Class source, String name) {
        List<Method> ms = memberMethods(source, name);
        return filterArgs0(ms, source, name);
    }

    static Method staticArgs0(Class source, String name) {
        List<Method> ms = staticMethods(source, name);
        return filterArgs0(ms, source, name);
    }

    static String stringify(Method method) {
        return concat(method.getDeclaringClass().getName(), "#", method.getName(), "()");
    }

    static AsRunner doThrow(String msg) { throw new IllegalArgumentException(msg); }

    /**
     * 解析没有参数调用的静态方法
     *
     * @return
     */
    static Method filterArgs0(List<Method> methods, Class source, String name) {
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

    private enum PrimitiveType {
        BYTE(byte.class),
        SHORT(short.class),
        INT(int.class),
        LONG(long.class),
        FLOAT(float.class),
        DOUBLE(double.class);

        private final String text;
        private final Class classType;

        PrimitiveType(Class type) {
            text = name().toLowerCase();
            classType = type;
            TYPES.put(type, this);
        }
    }

    private final static Map<Class, PrimitiveType> TYPES = new HashMap<>();

    final static int MAX_ORDINAL = PrimitiveType.values().length;

    static int getOrdinal(Class type) {
        PrimitiveType cached = TYPES.get(type);
        return cached == null ? -1 : cached.ordinal();
    }

    /*
     * -------------------------------------------------------------------------
     * static classes
     * -------------------------------------------------------------------------
     */

    static AsRunner onlyStatic(Method method) {
        return new OnlyStaticArgs0(method);
    }

    static AsRunner onlyStatic(Method method, AsRunner first) {
        return new OnlyStaticArgs1(method, first);
    }

    static AsRunner onlyStatic(Method method, AsRunner first, AsRunner second) {
        return new OnlyStaticArgs2(method, first, second);
    }

    static AsRunner onlyStatic(Method method, AsRunner first, AsRunner second, AsRunner third) {
        return new OnlyStaticArgs3(method, first, second, third);
    }

    static AsRunner onlyStatic(Method method, AsRunner... runners) {
        return new OnlyStaticArgsN(method, runners);
    }

    static class OnlyStaticArgs0 implements AsInvoker {

        final Method method;

        OnlyStaticArgs0(Method method) {this.method = method;}

        @Override
        public Object run(Object data) { return invoke(true, method, null); }

        @Override
        public final String toString() { return stringify(method); }
    }

    static class OnlyStaticArgs1 extends OnlyStaticArgs0 {

        final AsRunner firstValuer;

        OnlyStaticArgs1(Method method, AsRunner firstValuer) {
            super(method);
            this.firstValuer = firstValuer;
        }

        @Override
        public Object run(Object data) {
            // int[] params = {TypeUtil.cast().toIntValue(firstValuer.run(data))};
            // return invoke(true, method, null, params);
            // return invoke(true, method, null, new Object[]{firstValuer.run(data)});
            return invoke(true, method, null, firstValuer.run(data));
        }
    }

    static class OnlyStaticArgs2 extends OnlyStaticArgs1 {

        final AsRunner secondValuer;

        OnlyStaticArgs2(Method method, AsRunner firstValuer, AsRunner secondValuer) {
            super(method, firstValuer);
            this.secondValuer = secondValuer;
        }

        @Override
        public Object run(Object data) {
            Object first = firstValuer.run(data);
            Object second = secondValuer.run(data);
            return invoke(true, method, null, first, second);
        }
    }

    static class OnlyStaticArgs3 extends OnlyStaticArgs2 {

        final AsRunner thirdValuer;

        OnlyStaticArgs3(Method method, AsRunner firstValuer, AsRunner secondValuer, AsRunner thirdValuer) {
            super(method, firstValuer, secondValuer);
            this.thirdValuer = thirdValuer;
        }

        @Override
        public Object run(Object data) {
            Object first = firstValuer.run(data);
            Object second = secondValuer.run(data);
            Object third = secondValuer.run(data);
            return invoke(true, method, null, first, second, third);
        }
    }

    static class OnlyStaticArgsN extends OnlyStaticArgs0 {

        final AsRunner[] params;

        OnlyStaticArgsN(Method method, AsRunner... params) {
            super(method);
            this.params = params;
        }

        @Override
        public Object run(Object data) {
            AsRunner[] params = this.params;
            int length = params.length;
            Object[] parameters = new Object[length];
            for (int i = 0; i < length; i++) {
                parameters[i] = params[i].run(data);
            }
            return invoke(true, method, null, parameters);
        }
    }
}
