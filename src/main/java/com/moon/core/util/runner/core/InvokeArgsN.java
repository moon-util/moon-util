package com.moon.core.util.runner.core;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author benshaoye
 */
final class InvokeArgsN extends InvokeAbstract {

    static AsRunner staticCallN(Class type, String name, AsRunner... params) {
        List<Method> ms = staticMethods(type, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(type, name);
            case 1:
                return ensure(ms.get(0), params);
            default:
                return doThrowNull();
        }
    }

    final static AsRunner parse(
        AsValuer prev, String name, boolean isStatic, AsRunner... params
    ) {
        if (isStatic) {
            // 静态方法
            Class type = ((DataClass) prev).getValue();
            return staticCallN(type, name, params);
        } else if (prev.isConst()) {
            return doThrowNull();
        } else {
            // 成员方法
            return doThrowNull();
        }
    }
}
