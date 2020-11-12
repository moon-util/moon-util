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
        mapping.doForward(null, null);
        mapping.doBackward(null, null);
        mapping.clone(null);
        mapping.toMap(null,new HashMap<>());
    }
}