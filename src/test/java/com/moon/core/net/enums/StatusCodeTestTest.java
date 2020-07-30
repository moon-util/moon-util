package com.moon.core.net.enums;

import com.moon.core.model.KeyValue;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class StatusCodeTestTest {

    @Test
    void testCodeOf() {
        IteratorUtil.map(ListUtil.newList(StatusCode.values()), KeyValue::of).forEach(kv -> {
            Assertions.assertFalse(kv.getValue().matches("\\d+"));
        });
    }
}