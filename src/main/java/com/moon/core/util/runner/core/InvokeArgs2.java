package com.moon.core.util.runner.core;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public class InvokeArgs2 extends InvokeAbstract {

    private InvokeArgs2() { noInstanceError(); }

    final static AsRunner parse(
        AsValuer prev, String name, boolean isStatic, AsRunner firstParam, AsRunner secondParam
    ) {
        if (isStatic) {
            // 静态方法
            Class sourceType = ((DataLoader) prev).getValue();
            return InvokeOneEnsure.of(firstParam, sourceType, name);
        } else {
            // 成员方法
            return new InvokeOne(prev, firstParam, name);
        }
    }
}
