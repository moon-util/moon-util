package com.moon.core.util;

import org.junit.jupiter.api.Test;

import static java.lang.System.out;

/**
 * @author benshaoye
 * @date 2018/9/12
 */
class OSUtilTestTest {

    @Test
    void testIsLinux() {
        out.println(OSUtil.isLinux());
    }

    @Test
    void testIsMacOSX() {
        out.println(OSUtil.isMacOSX());
    }

    @Test
    void testIsWindows() {
        out.println(OSUtil.isWindows());
    }

    @Test
    void testIsWindowsXP() {
        out.println(OSUtil.isWindowsXP());
    }

    @Test
    void testIsWindows2003() {
        out.println(OSUtil.isWindows2003());
    }

    @Test
    void testIsWindowsVista() {
        out.println(OSUtil.isWindowsVista());
    }

    @Test
    void testIsWindowsWin7() {
        out.println(OSUtil.isWindows7());
    }

    @Test
    void testIsWindowsWin8() {
        out.println(OSUtil.isWindows8());
    }

    @Test
    void testIsWindowsWin10() {
        out.println(OSUtil.isWindows10());
    }

    @Test
    void testExecuteMatches() {
        String name = OSUtil.get(() -> "1", () -> "2", () -> "3", () -> "4");
        out.println(name);
    }

    @Test
    void testWhenLinux() {
        OSUtil.whenLinux(() -> out.println("linux"));
        OSUtil.whenMacOSX(() -> out.println("max"));
        OSUtil.whenWindows(() -> out.println("windows"));
    }

    @Test
    void testWhenMacOSX() {
        OSUtil.whenLinux(() -> out.println("linux"));
        OSUtil.whenMacOSX(() -> out.println("max"));
        OSUtil.whenWindows(() -> out.println("windows"));
    }

    @Test
    void testWhenWindows() {
        OSUtil.whenLinux(() -> out.println("linux"));
        OSUtil.whenMacOSX(() -> out.println("max"));
        OSUtil.whenWindows(() -> out.println("windows"));
    }

    @Test
    void testIsWindows7() {
    }

    @Test
    void testIsWindows8() {
    }

    @Test
    void testIsWindows10() {
    }
}