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
        String value = getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        switch (value) {
            case "true":
            case "TRUE":
            case "yes":
            case "YES":
            case "1":
            case "on":
            case "ON":
            case "enable":
            case "enabled":
            case "ENABLE":
            case "ENABLED":
                return true;
            case "false":
            case "FALSE":
            case "no":
            case "NO":
            case "0":
            case "off":
            case "OFF":
            case "disable":
            case "disabled":
            case "DISABLE":
            case "DISABLED":
                return false;
            default:
                return defaultValue;
        }
    }

    public static int getIntValue(String name, int defaultValue) {
        return IntUtil.defaultIfInvalid(getProperty(name), defaultValue);
    }
}
