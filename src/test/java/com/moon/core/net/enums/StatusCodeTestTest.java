package com.moon.core.net.enums;

import com.moon.core.models.KeyValue;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class StatusCodeTestTest {

    @Test
    void testCodeOf() {
        IteratorUtil.map(ListUtil.ofArrayList(StatusCode.values()), KeyValue::of).forEach(kv -> {
            Assertions.assertFalse(kv.getValue().matches("\\d+"));
        });
    }
}