package com.moon.core.util.runner.core;

import com.moon.core.lang.reflect.MethodUtil;

/**
 * @author benshaoye
 */
class InvokeNone extends InvokeBase {

    InvokeNone(String methodName) { super(methodName); }

    @Override
    public Object run(Object data) { return MethodUtil.invoke(true, getMethod(data), data); }
}
