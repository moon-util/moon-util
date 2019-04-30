package com.moon.core.util;

import com.moon.core.util.require.Requires;
import com.moon.core.util.console.ConsoleControl;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author benshaoye
 */
@ConsoleControl(lowestLevel = Console.Level.WARN)
class ConsoleTestTest {

    static final Requires REQUIRES = Requires.of();

    @Test
    void testOf() {
//        REQUIRES.requireEquals(1, 1);
    }

    @Test
    void testPrintStackTrace() {
        Console.out.printStackTrace();

    }

    @Test
    void testName() {
        Console.out.println("aaaaaaaaaaaaaaaa");
        Console.out.println("bbbbbbbbbbbbbbbbbbb");
        Console.out.println("ccccccccccccccccccccccc");

        Console.out.println(new File("/d:/invoice/").exists());
        Console.out.println(System.getProperty("consoleBasePath"));

        File file = new File("/d:/invoice/name/temp");
        Console.out.println(file.exists());
        File parent = file;
        Console.out.println((parent = parent.getParentFile()).exists());
        Console.out.println((parent = parent.getParentFile()).exists());
        Console.out.println((parent = parent.getParentFile()).exists());
        Console.out.println(parent);
        Console.out.println(parent.getParentFile());
        Console.out.println(file.mkdirs());
        Console.out.println(getClass().getSimpleName());
    }

}