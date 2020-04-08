package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class TableImplTestTest {

    @Test
    void testPut() {
        Table table = new TableImpl();
        assertEquals(0, table.size());
        assertTrue(table.isEmpty());
        table.put(1, 1, 1);
        assertEquals(1, table.size());
        assertFalse(table.isEmpty());
        table.keys().forEach(key -> {
            assertEquals(1, key);
            Map row = table.get(key);
            assertSame(HashMap.class, row.getClass());
            assertTrue(row.containsKey(1));
            assertTrue(row.containsValue(1));
            assertEquals(1,row.size());
        });
        assertEquals(1, table.sizeOfRows());
        assertEquals(1, table.maxSizeOfColumns());
        assertEquals(1, table.minSizeOfColumns());
        assertTrue(table.contains(1));
        assertTrue(table.contains(1, 1));
        assertTrue(table.containsValue(1));
    }

    @Test
    void testGet() {
    }

    @Test
    void testPutAll() {
    }

    @Test
    void testTestGet() {
    }

    @Test
    void testTestPutAll() {
    }

    @Test
    void testRemove() {
    }

    @Test
    void testTestRemove() {
    }

    @Test
    void testContainsValue() {
    }

    @Test
    void testKeys() {
    }

    @Test
    void testRows() {
    }

    @Test
    void testRowsEntrySet() {
    }

    @Test
    void testClear() {
    }

    @Test
    void testSizeOfRows() {
    }

    @Test
    void testMaxSizeOfColumns() {
    }

    @Test
    void testMinSizeOfColumns() {
    }

    @Test
    void testSize() {
    }

    @Test
    void testSandbox() {
    }
}