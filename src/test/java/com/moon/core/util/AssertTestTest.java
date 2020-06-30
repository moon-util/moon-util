package com.moon.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class AssertTestTest {

    static void assertThrowing(Executable executable) {
        assertThrows(Exception.class, executable);
    }

    @Test
    void testNonNull() {
    }

    @Test
    void testHasText() {
        assertThrowing(() -> Assert.hasText(""));
        assertThrowing(() -> Assert.hasText("   "));
        Assert.hasText("a");
        Assert.hasText("  a ");
    }

    @Test
    void testNonBlank() {
    }

    @Test
    void testNonEmpty() {
    }

    @Test
    void testRequireTrue() {
    }

    @Test
    void testRequireFalse() {
    }

    @Test
    void testRequireEquals() {
    }

    @Test
    void testRequireEq() {
    }

    @Test
    void testThrowVal() {
    }

    @Test
    void testRequireBefore() {
    }

    @Test
    void testRequireAfter() {
    }
}