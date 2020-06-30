package com.moon.core.lang;

import com.moon.core.util.ResourceUtil;

import java.io.InputStream;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class SystemUtil {
    private SystemUtil() {
        noInstanceError();
    }

    public final static boolean resourceExists(String path) {
        return ResourceUtil.resourceExists(path);
    }

    public final static InputStream getResourceAsInputStream(String path) {
        return ResourceUtil.getResourceAsInputStream(path);
    }

    public final static long now() {
        return System.currentTimeMillis();
    }
}
