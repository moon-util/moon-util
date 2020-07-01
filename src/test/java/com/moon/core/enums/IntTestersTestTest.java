package com.moon.core.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class IntTestersTestTest {

    @Test
    void testValues() throws Exception {
        System.out.println("\u4E00");
        System.out.println("\u4E00".length());
        System.out.println("\u4E00".codePointAt(0));
        System.out.println("\u9Faf");
        System.out.println("\u9Faf".length());
        System.out.println("\u9Faf".codePointAt(0));
        System.out.println("\u9FFF");
        System.out.println("\u9FFF".length());
        System.out.println("\u9FFF".codePointAt(0));

        System.out.println(IntTesters.CHINESE.test('片'));
    }
}