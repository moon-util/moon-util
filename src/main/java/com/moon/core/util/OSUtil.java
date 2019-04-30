package com.moon.core.util;

import com.moon.core.lang.Executable;
import com.moon.core.lang.ThrowUtil;

import java.util.function.Supplier;

/**
 * 系统工具
 *
 * @author benshaoye
 * @date 2018/9/12
 */
public final class OSUtil {

    private OSUtil() {
        ThrowUtil.noInstanceError();
    }

    private static final boolean osIsLinux = nameHas("linux");
    private static final boolean osIsMacOsX = nameHas("mac name x");
    private static final boolean osIsWindows = nameHas("windows");

    public static <T> T get(
        Supplier<T> ifLinux,
        Supplier<T> ifWindows,
        Supplier<T> ifMac,
        Supplier<T> other) {
        return (isLinux()
            ? ifLinux : (isWindows()
            ? ifWindows : (isMacOSX()
            ? ifMac : other))).get();
    }

    public static boolean isLinux() {
        return osIsLinux;
    }

    public static void whenLinux(Executable executor) {
        if (isLinux()) {
            executor.execute();
        }
    }

    public static boolean isMacOSX() {
        return osIsMacOsX;
    }

    public static void whenMacOSX(Executable executor) {
        if (isMacOSX()) {
            executor.execute();
        }
    }

    public static boolean isWindows() {
        return osIsWindows;
    }

    public static void whenWindows(Executable executor) {
        if (isWindows()) {
            executor.execute();
        }
    }

    public static boolean isWindowsXP() {
        return nameHas("windows xp");
    }

    public static boolean isWindows2003() {
        return nameHas("windows 2003");
    }

    public static boolean isWindowsVista() {
        return nameHas("windows vista");
    }

    public static boolean isWindows7() {
        return nameHas("windows 7");
    }

    public static boolean isWindows8() {
        return nameHas("windows 8");
    }

    public static boolean isWindows10() {
        return nameHas("windows 10");
    }

    private final static boolean nameHas(String search) {
        String name = System.getProperty("os.name");
        return (name == null ? "" : name.toLowerCase()).indexOf(search) >= 0;
    }
}
