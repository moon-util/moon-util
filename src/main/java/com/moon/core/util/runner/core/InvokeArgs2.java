package com.moon.core.util.runner.core;

import java.lang.reflect.Method;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
final class InvokeArgs2 extends InvokeAbstract {

    private InvokeArgs2() { noInstanceError(); }

    static AsRunner staticCall2(Class source, String name, AsRunner no1, AsRunner no2) {
        List<Method> ms = staticMethods(source, name);
        switch (ms.size()) {
            case 0:
                return ParseUtil.doThrow(source, name);
            case 1:
                return ensure(ms.get(0), no1, no2);
            default:
                return doThrowNull();
        }
    }

    final static AsRunner parse(
        AsValuer prev, String name, boolean isStatic, AsRunner no1, AsRunner no2
    ) {
        if (isStatic) {
            // 静态方法
            Class type = ((DataClass) prev).getValue();
            return staticCall2(type, name, no1, no2);
        } else if (prev.isConst()) {
            return doThrowNull();
        } else {
            // 成员方法
            return doThrowNull();
        }
    }
}
