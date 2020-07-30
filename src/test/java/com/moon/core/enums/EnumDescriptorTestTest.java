package com.moon.core.enums;

import com.moon.core.model.KeyValue;
import com.moon.core.net.enums.StatusCode;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Test;

import java.util.Collection;

/**
 * @author moonsky
 */
class EnumDescriptorTestTest {

    @Test
    void testGetName() {
        Collection<KeyValue> maped = IteratorUtil.map(ListUtil.newList(StatusCode.values()), KeyValue::of);

        maped.forEach(System.out::println);

        maped = IteratorUtil.map(ListUtil.newList(Arrays2.values()), KeyValue::of);

        maped.forEach(System.out::println);
    }
}