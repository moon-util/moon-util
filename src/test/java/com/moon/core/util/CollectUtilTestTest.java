package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author moonsky
 */
class CollectUtilTestTest {

    @Test
    void testIsEmpty() {
        List<String> data = ListUtil.newList("111", "222", "333", "id", "name");
        CollectUtil.orderBy(new ArrayList<>(), data, it -> it, "id", "name");
    }

    @Test
    void testSize() {
    }

    @Test
    void testSizeByObject() {
    }

    @Test
    void testIsNotEmpty() {
    }

    @Test
    void testAdd() {
    }

    @Test
    void testAddAll() {
    }

    @Test
    void testAddIfNonNull() {
    }

    @Test
    void testMap() {
    }

    @Test
    void testConcat() {
    }

    @Test
    void testToSet() {
    }

    @Test
    void testToList() {
    }

    @Test
    void testToArray() {
        List<String> list = new ArrayList<>();
        Object[] arr = CollectUtil.toArray(list, Object[]::new);
    }

    @Test
    void testContains() {
    }

    @Test
    void testContainsAny() {
    }

    @Test
    void testContainsAll() {
    }

    @Test
    void testMatchAny() {
    }

    @Test
    void testMatchAll() {
    }

    @Test
    void testRequireNotEmpty() {
    }

}