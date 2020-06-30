package com.moon.spring;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class MoonConst {

    private MoonConst() { noInstanceError(); }

    public final static class Data {

        public final static String IDENTIFIER = "moon.data.identifier";
    }
}
