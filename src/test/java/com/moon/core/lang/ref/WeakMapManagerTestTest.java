package com.moon.core.lang.ref;

import com.moon.core.lang.reflect.ConstructorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.WeakHashMap;

/**
 * @author moonsky
 */
class WeakMapManagerTestTest {

    @Test
    void testManage() {
        Assertions.assertThrows(Throwable.class, () -> ConstructorUtil.newInstance(WeakMapManager.class, true));
        Assertions.assertThrows(Throwable.class, () -> WeakMapManager.manage(new WeakHashMap<>()));
    }
}