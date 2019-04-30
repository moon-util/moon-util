package com.moon.core.util;

import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class TypeUtilTestTest {

    static final Requires REQUIRES = Requires.of();

    @Test
    void testCast() {
        REQUIRES.requireEquals(TypeUtil.cast().toBooleanValue(0), false);
    }
}