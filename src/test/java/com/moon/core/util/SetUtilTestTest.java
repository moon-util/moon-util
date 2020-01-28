package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * @author benshaoye
 */
class SetUtilTestTest {

    @Test
    void testOfHashSet() {
        SetUtil.add(new HashSet<>(), "name");
    }
}