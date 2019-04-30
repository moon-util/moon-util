package com.moon.core.util.require;

import com.moon.core.util.Console;
import com.moon.core.util.console.ConsoleControl;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
@ConsoleControl(lowestLevel = Console.Level.DEBUG)
class RequiresTestTest {

    static final Requires REQUIRES = Requires.ofPrintln();

    @Test
    void testOf() {
        REQUIRES.requireNotInstanceOf(REQUIRES, Requires.class);
        REQUIRES.getConsole().error("=====================");
    }

    @Test
    void testOfThrow() {
    }

    @Test
    void testOfPrintln() {
    }

    @Test
    void testOfError() {
    }

    @Test
    void testAssertion() {
    }
}