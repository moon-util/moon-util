package com.moon.mapping;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * @author moonsky
 */
class BeanMappingTestTest {

    @Test
    void testCopyForward() throws Exception {
        BeanMapping mapping = null;
        mapping.copyForward(null, null);
        mapping.copyBackward(null, null);
        mapping.clone(null);
        mapping.toMap(null,new HashMap<>());
    }
}