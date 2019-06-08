package com.moon.core.util.runner.core;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.moon.core.lang.reflect.MethodUtil.*;

/**
 * @author benshaoye
 */
final class InvokeArgs0 extends InvokeArgs {

    static class BaseInvoker implements AsInvoker {

        final String methodName;

        private Method method;
        private Class declareClass;

        BaseInvoker(String methodName) {this.methodName = methodName;}

        public String getMethodName() { return methodName; }

        public Method getMethod(Object data) {
            if (declareClass == null || !declareClass.isInstance(data)) {
                Class type = Objects.requireNonNull(data).getClass();
                method = memberArgs0(type, methodName);
                declareClass = type;
            }
            return method;
        }

        @Override
        public String toString() { return methodName; }
    }

    static class NonMember extends BaseInvoker {

        NonMember(String methodName) { super(methodName); }

        @Override
        public Object run(Object data) { return invoke(true, getMethod(data), data); }
    }

    enum NonDefault implements AsInvoker {
        clone,
        toString,
        hashCode,
        getClass;

        private final Method method;

        NonDefault() { method = memberArgs0(Object.class, name()); }

        @Override
        public Object run(Object data) { return invoke(true, method, data); }

        static AsInvoker get(String name) {
            try {
                return valueOf(name);
            } catch (Throwable t) {
                return null;
            }
        }
    }

    static class NonStatic implements AsInvoker {

        final Method method;

        NonStatic(Method method) {this.method = method;}

        @Override
        public Object run(Object data) { return invoke(true, method, null); }

        @Override
        public String toString() { return method.getName(); }
    }

    static AsInvoker memberArgs0(String name) {
        AsInvoker invoker = NonDefault.get(name);
        return invoker == null ? new NonMember(name) : invoker;
    }

    static Method memberArgs0(Class source, String name) {
        List<Method> ms = getPublicMemberMethods(source, name);
        return filterEmptyArgs(ms, source, name);
    }

    static Method staticArgs0(Class source, String name) {
        List<Method> ms = getPublicStaticMethods(source, name);
        return filterEmptyArgs(ms, source, name);
    }


    final static AsRunner parse(AsValuer prev, String name, boolean isStatic) {
        if (isStatic) {
            Class srcType = ((DataLoader) prev).getValue();
            return new NonStatic(staticArgs0(srcType, name));
        } else {
            // 成员方法
            return new GetLink(prev, memberArgs0(name));
        }
    }
}
