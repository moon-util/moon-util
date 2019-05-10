package com.moon.core.util.runner.core;

import com.moon.core.lang.reflect.MethodUtil;

import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
class InvokeNoneEnsure implements AsInvoker {
    final Method method;

    InvokeNoneEnsure(Method method) { this.method = requireNonNull(method); }

    @Override
    public Object run(Object data) { return MethodUtil.invokeStatic(method); }
}