package com.moon.core.enums;

import com.moon.core.models.KeyValue;
import com.moon.core.net.enums.StatusCode;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Test;

import java.util.Collection;

/**
 * @author benshaoye
 */
class EnumDescriptorTestTest {

    @Test
    void testGetName() {
        Collection<KeyValue> maped = IteratorUtil.map(ListUtil.ofArrayList(StatusCode.values()), KeyValue::of);

        maped.forEach(System.out::println);

        maped = IteratorUtil.map(ListUtil.ofArrayList(ArraysEnum.values()), KeyValue::of);

        maped.forEach(System.out::println);
    }
}