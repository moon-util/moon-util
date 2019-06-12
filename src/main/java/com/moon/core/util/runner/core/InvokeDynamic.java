package com.moon.core.util.runner.core;

import com.moon.core.lang.ThrowUtil;

import java.lang.reflect.Method;

/**
 * @author benshaoye
 */
class InvokeDynamic {

    private InvokeDynamic() { ThrowUtil.noInstanceError(); }

    static abstract class BaseDynamic implements AsInvoker {

        final Method[] ms;
        final AsValuer src;

        Method method;

        protected BaseDynamic(Method[] ms, AsValuer src) {
            this.src = src;
            this.ms = ms;
        }
    }
}
