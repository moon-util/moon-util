package com.moon.core.util.runner.core;

import com.moon.core.enums.ArraysEnum;
import com.moon.core.enums.Casters;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import com.moon.core.util.converter.TypeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.IntFunction;

import static com.moon.core.lang.reflect.MethodUtil.invoke;
import static com.moon.core.util.runner.core.DataNull.NULL;

/**
 * @author benshaoye
 */
class InvokeEnsure {

    protected final static Object[] EMPTY_OBJECTS = ArraysEnum.OBJECTS.empty();

    static abstract class BaseEnsure implements AsInvoker {

        final AsRunner src;
        final Method method;

        protected BaseEnsure(Method method, AsRunner src) {
            this.src = src == null ? NULL : src;
            this.method = method;
        }

        @Override
        public boolean isStaticInvoker() { return src == NULL; }

        @Override
        public boolean isMemberInvoker() { return src != NULL; }

        Object[] toParams(Object... params) { return params; }

        Object[] getParams(Object data) { return EMPTY_OBJECTS; }

        Method getMethod(Object srcData) { return method; }

        @Override
        public final Object run(Object data) {
            Object srcData = src.run(data);
            return invoke(true, getMethod(srcData), srcData, getParams(data));
        }
    }

    /*
     * -------------------------------------------------------------------------
     * member classes
     * only : 代表指定类里面只有一个指定名称方法
     * Args0 : 代表在运行时以多少个参数运行
     * -------------------------------------------------------------------------
     */

    static class EnsureArgs0 extends BaseEnsure {

        EnsureArgs0(Method method, AsRunner src) { super(method, src); }

        final static AsRunner static0(Method method) { return new EnsureArgs0(method, null); }
    }

    static class EnsureArgs1 extends EnsureArgs0 {

        final AsRunner no1;

        EnsureArgs1(Method method, AsRunner src, AsRunner no1) {
            super(method, src);
            this.no1 = no1;
        }

        final static AsRunner static1(Method method, AsRunner no1) { return new EnsureArgs1(method, null, no1); }

        @Override
        public Object[] getParams(Object data) { return toParams(no1.run(data)); }
    }


    static class EnsureArgs2 extends EnsureArgs1 {

        final AsRunner no2;

        EnsureArgs2(Method method, AsRunner src, AsRunner no1, AsRunner no2) {
            super(method, src, no1);
            this.no2 = no2;
        }

        final static AsRunner static2(Method method, AsRunner no1, AsRunner no2) {
            return new EnsureArgs2(method, null, no1, no2);
        }

        @Override
        public Object[] getParams(Object data) { return toParams(no1.run(data), no2.run(data)); }
    }

    static class EnsureArgs3 extends EnsureArgs2 {

        final AsRunner no3;

        EnsureArgs3(Method method, AsRunner src, AsRunner no1, AsRunner no2, AsRunner no3) {
            super(method, src, no1, no2);
            this.no3 = no3;
        }

        final static AsRunner static3(Method method, AsRunner no1, AsRunner no2, AsRunner no3) {
            return new EnsureArgs3(method, null, no1, no2, no3);
        }

        @Override
        public Object[] getParams(Object data) { return toParams(no1.run(data), no2.run(data), no3.run(data)); }
    }

    static class EnsureArgsN extends EnsureArgs0 {

        final AsRunner[] params;

        EnsureArgsN(Method method, AsRunner src, AsRunner... params) {
            super(method, src);
            this.params = params;
        }

        final static AsRunner staticN(Method method, AsRunner... params) {
            return new EnsureArgsN(method, null, params);
        }

        @Override
        public Object[] getParams(Object data) {
            AsRunner[] params = this.params;
            int length = params.length;
            Object[] parameters = new Object[length];
            for (int i = 0; i < length; i++) {
                parameters[i] = params[i].run(data);
            }
            return parameters;
        }
    }

    /*
     * ---------------------------------------------------
     * var args classes
     * ---------------------------------------------------
     */

    static class EnsureVars1 extends BaseEnsure {

        final TypeConverter converter;
        final IntFunction creator;
        final AsRunner[] lasts;

        EnsureVars1(Method method, AsRunner src, AsRunner... lasts) {
            super(method, src);
            Class target = varArgsComponentType(method);
            final Casters caster = Casters.getOrNull(target);
            converter = to(caster, target);
            creator = toArr(caster, target);
            this.lasts = lasts;
        }

        static EnsureVars1 static1(Method method, AsRunner... lasts) {
            return new EnsureVars1(method, null, lasts);
        }

        Object getVarArg(Object data, AsRunner[] lasts) {
            int length = lasts.length;
            Object param1 = creator.apply(length), arg;
            for (int i = 0; i < length; i++) {
                arg = lasts[i].run(data);
                Array.set(param1, i, converter.convert(arg));
            }
            return param1;
        }

        Object getThisVarArg(Object data) { return getVarArg(data, this.lasts); }

        @Override
        public Object[] getParams(Object data) { return toParams(getThisVarArg(data)); }
    }

    static class EnsureVars2 extends EnsureVars1 {

        final AsRunner no1;

        EnsureVars2(Method method, AsRunner src, AsRunner no1, AsRunner... lasts) {
            super(method, src, lasts);
            this.no1 = no1;
        }

        static EnsureVars1 static2(Method method, AsRunner no1, AsRunner... lasts) {
            return new EnsureVars2(method, null, no1, lasts);
        }

        @Override
        public Object[] getParams(Object data) { return toParams(no1.run(data), getThisVarArg(data)); }
    }

    static class EnsureVars3 extends EnsureVars2 {

        final AsRunner no2;

        EnsureVars3(Method method, AsRunner src, AsRunner no1, AsRunner no2, AsRunner... lasts) {
            super(method, src, no1, lasts);
            this.no2 = no2;
        }

        static EnsureVars1 static3(Method method, AsRunner no1, AsRunner no2, AsRunner... lasts) {
            return new EnsureVars3(method, null, no1, no2, lasts);
        }

        @Override
        public Object[] getParams(Object data) { return toParams(no1.run(data), no2.run(data), getThisVarArg(data)); }
    }

    static class EnsureVarsN extends EnsureVars1 {

        final AsRunner[] pres;
        final AsRunner[] subs;
        final int paramsLen;
        final Object emptySubs;

        EnsureVarsN(Method method, AsRunner src, AsRunner... params) {
            super(method, src);
            Class target = varArgsComponentType(method);
            int count = method.getParameterCount();
            int prevN = count - 1;
            int paramsLen = params.length;
            if (paramsLen < prevN) {
                ParseUtil.doThrow(method);
                pres = null;
                subs = null;
                emptySubs = null;
            } else if (paramsLen == prevN) {
                pres = params;
                subs = null;
                emptySubs = Array.newInstance(target, 0);
            } else {
                emptySubs = null;
                pres = Arrays.copyOfRange(params, 0, prevN);
                subs = Arrays.copyOfRange(params, prevN, paramsLen);
            }
            this.paramsLen = prevN + 1;
        }

        static EnsureVars1 staticN(Method method, AsRunner... params) {
            return new EnsureVarsN(method, null, params);
        }

        @Override
        Object getThisVarArg(Object data) { return emptySubs == null ? getVarArg(data, subs) : emptySubs; }

        @Override
        public Object[] getParams(Object data) {
            int length = this.paramsLen, prev = length - 1;
            AsRunner[] pres = this.pres;
            Object[] arr = new Object[length];
            for (int i = 0; i < prev; i++) {
                arr[i] = converter.convert(pres[i].run(data));
            }
            arr[prev] = getThisVarArg(data);
            return arr;
        }
    }

    protected final static TypeCaster CASTER = TypeUtil.of();

    /*
     * ---------------------------------------------------
     * inner classes
     * ---------------------------------------------------
     */

    private static Class varArgsComponentType(Method m) {
        Class[] types = m.getParameterTypes();
        return types[types.length - 1].getComponentType();
    }

    private static TypeConverter to(Casters caster, Class type) {
        return caster == null ? (o -> CASTER.toType(o, type)) : caster;
    }

    private static IntFunction toArr(Casters caster, Class type) {
        return caster == null ? (len -> Array.newInstance(type, len)) : (len -> caster.createArr(len));
    }

    static int length(Object[] arr) { return arr == null ? 0 : arr.length; }
}
