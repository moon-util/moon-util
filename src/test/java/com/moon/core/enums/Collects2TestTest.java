package com.moon.core.enums;

import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class Collects2TestTest {

    @Test
    void testName() {
        System.out.println(ListUtil.newArrayList(Lists.values()));
        System.out.println(ListUtil.newArrayList(Sets.values()));
        System.out.println(ListUtil.newArrayList(Queues.values()));
    }
}