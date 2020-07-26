package com.moon.core.lang;

import com.moon.core.util.ResourceUtil;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class SystemUtil {

    private SystemUtil() { noInstanceError(); }

    public static boolean resourceExists(String path) { return ResourceUtil.resourceExists(path); }

    public static InputStream getResourceAsInputStream(String path) {
        return ResourceUtil.getResourceAsInputStream(path);
    }

    public static long now() { return System.currentTimeMillis(); }

    public static String getProperty(String name) { return getProperty(name, null); }

    public static String getProperty(String name, String defaultValue) {
        Objects.requireNonNull(name);

        String value = null;
        try {
            if (System.getSecurityManager() == null) {
                value = System.getProperty(name);
            } else {
                value = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty(name));
            }
        } catch (Exception ignore) {
        }

        return value == null ? defaultValue : value;
    }

    public static boolean getBooleanValue(String name, boolean defaultValue) {
        return BooleanUtil.defaultIfInvalid(getProperty(name), defaultValue);
    }

    public static int getIntValue(String name, int defaultValue) {
        return IntUtil.defaultIfInvalid(getProperty(name), defaultValue);
    }

    public static String getJvmName() { return getProperty("java.vm.name"); }

    public static String getJvmVersion() { return getProperty("java.vm.version"); }

    public static String getJvmInfo() { return getProperty("java.vm.info"); }

    public static String getJavaVersion() { return getProperty("java.version"); }

    /**
     * 返回 java 数字版本号，脂肪整数，如：
     * 1.7  ==>  7
     * 1.8  ==>  8
     * 9    ==>  9
     *
     * @return
     */
    private static int getJavaNumberVersion() {
        throw new UnsupportedOperationException();
    }
}
