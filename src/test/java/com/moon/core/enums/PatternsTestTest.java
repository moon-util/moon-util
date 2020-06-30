package com.moon.core.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class PatternsTestTest {

    @Test
    void testOfPattern() throws Exception {
        System.out.println(Patterns.CHINESE_ZIP_CODE.test("123456789"));
    }
}