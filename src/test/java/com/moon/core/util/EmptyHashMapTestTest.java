package com.moon.core.util;

import com.moon.core.lang.reflect.ConstructorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class EmptyHashMapTestTest {

    @Test
    void testGetInstance() {
        Assertions.assertThrows(Throwable.class, () ->
            ConstructorUtil.newInstance(EmptyHashMap.class, true));
        Assertions.assertThrows(Throwable.class, () ->
            ConstructorUtil.newInstance(EmptyHashMap.class, false));
        Assertions.assertTrue(EmptyHashMap.DEFAULT.isEmpty());
    }
}