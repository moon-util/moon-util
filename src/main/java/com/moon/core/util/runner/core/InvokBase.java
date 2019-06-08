package com.moon.core.util.runner.core;

import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
public class InvokBase {
    final String methodName;

    private Method method;
    private Class declaringClass;

    protected InvokBase(String methodName) {
        this.methodName = requireNonNull(methodName);
    }

    protected InvokBase(Method method) {
        this(method.getName());
        this.method = method;
    }

    public final String getMethodName() { return methodName; }
}
