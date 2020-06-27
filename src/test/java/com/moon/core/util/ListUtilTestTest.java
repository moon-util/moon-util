package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class ListUtilTestTest {

    List l, l1, list, list1;
    Object one, one1, item, item1;

    @Test
    void testOfArrayList() {
        assertTrue(ListUtil.newArrayList() instanceof ArrayList);
    }

    @Test
    void testOfLinkedList() {
        assertTrue(ListUtil.newLinkedList() instanceof LinkedList);
    }

    @Test
    void testNullIfEmpty() {
    }

    @Test
    void testEmptyIfNull() {
        assertNotNull(ListUtil.emptyIfNull(null));
        assertNotNull(ListUtil.emptyIfNull(new ArrayList()));
        assertNotNull(ListUtil.emptyIfNull(new LinkedList<>()));

        assertEquals(ListUtil.emptyIfNull(new LinkedList()).size(), 0);
        assertEquals(ListUtil.emptyIfNull(new ArrayList()).size(), 0);
        assertEquals(ListUtil.emptyIfNull(null).size(), 0);

        list = new ArrayList();
        list.add(one = "");
        assertSame(ListUtil.emptyIfNull(list), list);
    }

    @Test
    void testNullableGetFirst() {
        assertNull(ListUtil.nullableGetFirst(null));
        assertNull(ListUtil.nullableGetFirst(new ArrayList<>()));
        assertNull(ListUtil.nullableGetFirst(new LinkedList()));

        list = new ArrayList();
        list.add(one = "");
        assertSame(ListUtil.nullableGetFirst(list), one);
    }

    @Test
    void testRequireGetFirst() {
        assertThrows(Exception.class, () -> ListUtil.requireGetFirst(null));
        assertThrows(Exception.class, () -> ListUtil.requireGetFirst(new ArrayList<>()));
        assertThrows(Exception.class, () -> ListUtil.requireGetFirst(new LinkedList()));

        list = new ArrayList();
        list.add(one = "");
        assertDoesNotThrow(() -> ListUtil.requireGetFirst(list));

        String message = "fasdiohfdjkfgnasdkl;gfl";
        assertThrows(Exception.class, () -> ListUtil.requireGetFirst(null, message), message);
    }

    @Test
    void testNullableGetLast() {
    }

    @Test
    void testRequireGetLast() {
    }

    @Test
    void testNullableGet() {
    }

    @Test
    void testRequireGet() {
    }

    @Test
    void testNullableShift() {
    }

    @Test
    void testRequireShift() {
    }

    @Test
    void testNullablePop() {
    }

    @Test
    void testRequirePop() {
    }

    @Test
    void testGetOrDefault() {
    }

    @Test
    void testGetLastOrDefault() {
    }

    @Test
    void testGetFirstOrDefault() {
    }

    @Test
    void testGet() {
    }

    @Test
    void testGetByObject() {
    }

    @Test
    void testConcat() {
    }

    @Test
    void testUnique() {
    }

    @Test
    void testRemoveRepeats() {
    }
}