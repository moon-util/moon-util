package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * @author moonsky
 */
class SetUtilTestTest {

    @Test
    void testOfHashSet() {
        SetUtil.add(new HashSet<>(), "name");
    }
}