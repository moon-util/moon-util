package com.moon.core.util;

import org.junit.jupiter.api.Test;

import static java.lang.System.out;

/**
 * @author benshaoye
 */
class OSUtilTestTest {

    @Test
    void testIsLinux() {
        out.println(OSUtil.onLinux());
    }

    @Test
    void testIsMacOSX() {
        out.println(OSUtil.onMacOS());
    }

    @Test
    void testIsWindows() {
        out.println(OSUtil.onWindows());
    }

    @Test
    void testIsWindowsXP() {
        out.println(OSUtil.onWindowsXP());
    }

    @Test
    void testIsWindows2003() {
        out.println(OSUtil.onWindows2003());
    }

    @Test
    void testIsWindowsVonta() {
        out.println(OSUtil.onWindowsVista());
    }

    @Test
    void testIsWindowsWin7() {
        out.println(OSUtil.onWindows7());
    }

    @Test
    void testIsWindowsWin8() {
        out.println(OSUtil.onWindows8());
    }

    @Test
    void testIsWindowsWin10() {
        out.println(OSUtil.onWindows10());
    }

    @Test
    void testExecuteMatches() {
        String name = OSUtil.get(() -> "1", () -> "2", () -> "3", () -> "4");
        out.println(name);
    }

    @Test
    void testWhenLinux() {
        OSUtil.ifOnLinux(() -> out.println("linux"));
        OSUtil.ifOnMacOS(() -> out.println("max"));
        OSUtil.ifOnWindows(() -> out.println("windows"));
    }

    @Test
    void testWhenMacOSX() {
        OSUtil.ifOnLinux(() -> out.println("linux"));
        OSUtil.ifOnMacOS(() -> out.println("max"));
        OSUtil.ifOnWindows(() -> out.println("windows"));
    }

    @Test
    void testWhenWindows() {
        OSUtil.ifOnLinux(() -> out.println("linux"));
        OSUtil.ifOnMacOS(() -> out.println("max"));
        OSUtil.ifOnWindows(() -> out.println("windows"));
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