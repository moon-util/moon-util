package com.moon.core.util;

import com.moon.core.util.console.ConsoleControl;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author benshaoye
 */
@ConsoleControl(lowestLevel = Console.Level.WARN)
class ConsoleTestTest {

    @Test
    void testOf() {
//        REQUIRES.requireEquals(1, 1);
    }

    @Test
    void testPrintStackTrace() {

    }

    @Test
    void testName() {
        System.out.println("aaaaaaaaaaaaaaaa");
        System.out.println("bbbbbbbbbbbbbbbbbbb");
        System.out.println("ccccccccccccccccccccccc");

        System.out.println(new File("/d:/invoice/").exists());
        System.out.println(System.getProperty("consoleBasePath"));

        File file = new File("/d:/invoice/name/temp");
        System.out.println(file.exists());
        File parent = file;
        System.out.println((parent = parent.getParentFile()).exists());
        System.out.println((parent = parent.getParentFile()).exists());
        System.out.println((parent = parent.getParentFile()).exists());
        System.out.println(parent);
        System.out.println(parent.getParentFile());
        System.out.println(file.mkdirs());
        System.out.println(getClass().getSimpleName());
    }

}