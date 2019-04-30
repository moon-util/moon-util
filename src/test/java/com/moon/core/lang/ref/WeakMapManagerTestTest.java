package com.moon.core.lang.ref;

import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

import java.util.WeakHashMap;

/**
 * @author benshaoye
 */
class WeakMapManagerTestTest {
    static final Requires REQUIRES = Requires.of();

    @Test
    void testManage() {
        REQUIRES.requireThrows(() -> ConstructorUtil.newInstance(WeakMapManager.class, true));
        REQUIRES.requireThrows(() -> WeakMapManager.manage(new WeakHashMap<>()));
    }
}