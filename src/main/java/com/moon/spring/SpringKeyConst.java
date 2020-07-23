package com.moon.spring;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * spring 框架中读取与 moon-util 相关的环境变量相关 key 常量
 * @author moonsky
 */
public final class SpringKeyConst {

    private SpringKeyConst() { noInstanceError(); }

    public final static class Jpa {

        public final static String IDENTIFIER = "moon.data.identifier";
    }
}
