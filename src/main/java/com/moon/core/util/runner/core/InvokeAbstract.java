package com.moon.core.util.runner.core;

import com.moon.core.util.runner.core.InvokeEnsure.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.moon.core.lang.reflect.MethodUtil.getPublicMemberMethods;
import static com.moon.core.lang.reflect.MethodUtil.getPublicStaticMethods;
import static com.moon.core.util.runner.core.DataNull.NULL;
import static java.util.Arrays.copyOfRange;

/**
 * @author benshaoye
 */
abstract class InvokeAbstract {

    static List<Method> memberMethods(Class source, String name) { return getPublicMemberMethods(source, name); }

    static List<Method> staticMethods(Class source, String name) { return getPublicStaticMethods(source, name); }

    static Method memberArgs0(Class source, String name) {
        List<Method> ms = memberMethods(source, name);
        return filterArgs0(ms, source, name);
    }

    static Method staticArgs0(Class source, String name) {
        List<Method> ms = staticMethods(source, name);
        return filterArgs0(ms, source, name);
    }

    static String stringify(Method method) {
        return stringify(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
    }

    static String stringify(Class type, String name, Class... types) {
        StringBuilder str = new StringBuilder();
        str.append(type).append('#').append(name).append('(');
        if (types != null) {
            for (int i = 0, len = types.length, end = len - 1; i < len; i++) {
                str.append(types[i]);
                if (i < end) {
                    str.append(", ");
                }
            }
        }
        return str.append(')').toString();
    }

    static AsRunner doThrowNull() { throw new NullPointerException(); }

    static boolean isAllConst(AsRunner one, AsRunner... others) {
        if (others == null) {
            return one.isConst();
        }
        if (one.isConst()) {
            for (AsRunner other : others) {
                if (!other.isConst()) {
                    return false;
                }
            }
        }
        return true;
    }

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
                    ParseUtil.doThrow(source, name);
                }
            }
        }
        return Objects.requireNonNull(searchedMethod);
    }

    enum PrimitiveType {
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

    static AsRunner ensure(Method m) { return EnsureArgs0.static0(m); }

    static AsRunner ensure(Method m, AsRunner no1) {
        if (m.isVarArgs()) {
            switch (m.getParameterCount()) {
                case 1:
                    return new EnsureVars1(m, NULL, no1);
                default:
                    return ParseUtil.doThrow(m);
            }
        }
        return EnsureArgs1.static1(m, no1);
    }

    static AsRunner ensure(Method m, AsRunner no1, AsRunner no2) {
        if (m.isVarArgs()) {
            switch (m.getParameterCount()) {
                case 1:
                    return new EnsureVars1(m, NULL, no1, no2);
                case 2:
                    return new EnsureVars2(m, NULL, no1, no2);
                default:
                    return ParseUtil.doThrow(m);
            }
        }
        return EnsureArgs2.static2(m, no1, no2);
    }

    static AsRunner ensure(Method m, AsRunner no1, AsRunner no2, AsRunner no3) {
        if (m.isVarArgs()) {
            switch (m.getParameterCount()) {
                case 1:
                    return new EnsureVars1(m, NULL, no1, no2, no3);
                case 2:
                    return new EnsureVars2(m, NULL, no1, no2, no3);
                case 3:
                    return new EnsureVars3(m, NULL, no1, no2, no3);
                default:
                    return ParseUtil.doThrow(m);
            }
        }
        return EnsureArgs3.static3(m, no1, no2, no3);
    }

    static AsRunner ensure(Method m, AsRunner... params) {
        if (m.isVarArgs()) {
            switch (m.getParameterCount()) {
                case 1: {
                    return new EnsureVars1(m, NULL, params);
                }
                case 2: {
                    AsRunner[] lasts = copyOfRange(params, 1, params.length);
                    return new EnsureVars2(m, NULL, params[0], lasts);
                }
                case 3: {
                    AsRunner[] lasts = copyOfRange(params, 2, params.length);
                    return new EnsureVars3(m, NULL, params[0], params[1], lasts);
                }
                default: {
                    return new EnsureVarsN(m, NULL, params);
                }
            }
        }
        return EnsureArgsN.staticN(m, params);
    }
}
