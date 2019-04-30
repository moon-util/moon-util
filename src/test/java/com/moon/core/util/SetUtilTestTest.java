package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * @author benshaoye
 * @date 2018/9/12
 */
class SetUtilTestTest {

    @Test
    void testOfHashSet() {
        SetUtil.add(new HashSet<>(), "name");
    }
}