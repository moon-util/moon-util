package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class MathUtil {
    private MathUtil() {
        noInstanceError();
    }

    public final static String round(Number number, int precision) {
        return ThrowUtil.rejectAccessError();
    }

    public final static Number ceil(Number number, int precision) {
        return ThrowUtil.rejectAccessError();
    }

    public final static Number floor(Number number, int precision) {
        return ThrowUtil.rejectAccessError();
    }
}
