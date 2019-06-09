package com.moon.core.util.runner.core;

import java.lang.reflect.Method;
import java.util.Objects;

import static com.moon.core.lang.reflect.MethodUtil.*;

/**
 * @author benshaoye
 */
final class InvokeArgs0 extends InvokeAbstract {

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
        public String toString() { return method == null ? methodName : stringify(method); }
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

        @Override
        public String toString() { return stringify(method); }
    }

    static AsInvoker memberArgs0(String name) {
        AsInvoker invoker = NonDefault.get(name);
        return invoker == null ? new NonMember(name) : invoker;
    }

    final static AsRunner parse(AsValuer prev, String name, boolean isStatic) {
        if (isStatic) {
            Class srcType = ((DataLoader) prev).getValue();
            return onlyStatic(staticArgs0(srcType, name));
        } else {
            // 成员方法
            return new GetLink(prev, memberArgs0(name));
        }
    }
}
