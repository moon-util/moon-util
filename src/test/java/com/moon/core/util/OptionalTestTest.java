package com.moon.core.util;

import com.moon.core.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class OptionalTestTest extends AbstractTest {


    @Test
    void testEmpty() {
        assertNull(Optional.empty().getOrNull());
        assertThrow(() -> Optional.empty().get());
        assertTrue(() -> Optional.empty().isAbsent());
        assertFalse(() -> Optional.empty().isPresent());
        assertTrue(Optional.empty().filter(v -> v != null) == Optional.empty());
        assertTrue(Optional.empty().filter(v -> v == null) == Optional.empty());

    }

    @Test
    void testOf() {
        assertThrow(() -> Optional.of(null));
        assertDoesNotThrow(() -> Optional.ofNullable(null));

        assertFalse(Optional.of(123).filter(v -> v > 100) == Optional.empty());
        assertTrue(Optional.of(123).filter(v -> v < 100) == Optional.empty());
    }

    @Test
    void testOfNullable() {
        Object data = "123456";
        assertTrue(Objects.equals(Optional.ofNullable(data.toString()).compute(str -> str.length()), data.toString().length()));
    }

    @Test
    void testSetNullable() {
    }

    @Test
    void testSetNull() {
    }

    @Test
    void testSet() {
    }

    @Test
    void testGetOrNull() {
    }

    @Test
    void testGet() {
    }

    @Test
    void testGetOrDefault() {
    }

    @Test
    void testGetOrElse() {
    }

    @Test
    void testGetOrThrow() {
    }

    @Test
    void testIsPresent() {
    }

    @Test
    void testIsAbsent() {
    }

    @Test
    void testFilter() {
    }

    @Test
    void testElseIfAbsent() {
    }

    @Test
    void testDefaultIfAbsent() {
    }

    @Test
    void testIfPresent() {
    }

    @Test
    void testIfAbsent() {
    }

    @Test
    void testCompute() {
    }

    @Test
    void testTransform() {
        String str = "123456";
        Optional.ofNullable(str).ifPresent(value -> System.out.println(value))
            .ifAbsent(() -> System.out.println("asdfghjkl"));
    }
}