package com.moon.core.util;

import java.util.UUID;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * identifier ID 生成器
 *
 * @author benshaoye
 */
public final class IDUtil {
    private IDUtil() {
        noInstanceError();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
