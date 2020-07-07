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
        System.out.println(ListUtil.newList(Lists.values()));
        System.out.println(ListUtil.newList(Sets.values()));
        System.out.println(ListUtil.newList(Queues.values()));
    }
}