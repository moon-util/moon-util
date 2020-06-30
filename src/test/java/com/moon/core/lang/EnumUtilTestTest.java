package com.moon.core.lang;

import com.moon.core.util.IteratorUtil;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class EnumUtilTestTest {
    public enum TestMultiItemsEnum {
        FIRST(3), SECOND(2), THIRD(1);

        final int value;

        TestMultiItemsEnum(int value) {
            this.value = value;
        }
    }

    final static Class<TestMultiItemsEnum> multiType = TestMultiItemsEnum.class;
    final static Class<TestNullItemEnum> nonType = TestNullItemEnum.class;

    @Test
    void testValues() {
        assertArrayEquals(
            EnumUtil.values(TestMultiItemsEnum.class),
            new TestMultiItemsEnum[]{
                TestMultiItemsEnum.FIRST,
                TestMultiItemsEnum.SECOND,
                TestMultiItemsEnum.THIRD,
            });
    }

    @Test
    void testSortValues() {
        assertArrayEquals(
            EnumUtil.sortedValuesByName(TestMultiItemsEnum.class),
            new TestMultiItemsEnum[]{
                TestMultiItemsEnum.FIRST,
                TestMultiItemsEnum.SECOND,
                TestMultiItemsEnum.THIRD,
            });
        assertArrayEquals(
            EnumUtil.sortValues(TestMultiItemsEnum.class, Comparator.comparingInt(o -> o.value)),
            new TestMultiItemsEnum[]{
                TestMultiItemsEnum.THIRD,
                TestMultiItemsEnum.SECOND,
                TestMultiItemsEnum.FIRST,
            });
    }

    @Test
    void testValuesList() {
        List<TestMultiItemsEnum> list = EnumUtil.valuesList(multiType);
        TestMultiItemsEnum[] values = EnumUtil.values(multiType);
        assertEquals(list.size(), values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(list.get(i), values[i]);
        }
    }

    @Test
    void testValuesMap() {
        Map<String, TestMultiItemsEnum> valuesMap = EnumUtil.valuesMap(multiType);
        TestMultiItemsEnum[] values = EnumUtil.values(multiType);
        assertEquals(valuesMap.size(), values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(valuesMap.get(values[i].name()), values[i]);
        }
    }

    @Test
    void testCountOfValues() {
        TestMultiItemsEnum[] values = EnumUtil.values(multiType);
        assertEquals(EnumUtil.length(multiType), values.length);
    }

    @Test
    void testValueOf() {
        IteratorUtil.forEach(EnumUtil.values(multiType), value ->
            assertSame(EnumUtil.valueOf(multiType, value.name()), value));
    }

    @Test
    void testValueAt() {
        IteratorUtil.forEach(EnumUtil.values(multiType), (value, idx) ->
            assertSame(EnumUtil.valueAt(multiType, idx), value));

        assertThrows(Exception.class, () ->
            EnumUtil.valueAt(multiType, EnumUtil.length(multiType)));
    }

    @Test
    void testContains() {
        assertTrue(EnumUtil.contains(multiType, "FIRST"));
        assertFalse(EnumUtil.contains(multiType, "FIRST1"));
        assertFalse(EnumUtil.contains(multiType, ""));
        assertFalse(EnumUtil.contains(multiType, " "));
        assertFalse(EnumUtil.contains(multiType, "a"));
        assertFalse(EnumUtil.contains(multiType, " a "));
        assertFalse(EnumUtil.contains(multiType, null));
    }

    @Test
    void testToEnum() {
        IteratorUtil.forEach(multiType, (item, idx) ->
            assertSame(EnumUtil.toEnum(multiType, idx), item));
        IteratorUtil.forEach(multiType, (item, idx) ->
            assertSame(EnumUtil.toEnum(multiType, item.name()), item));
    }

    public enum TestNullItemEnum {}

    @Test
    void testFirst() {
        assertNull(EnumUtil.first(TestNullItemEnum.class));
    }

    @Test
    void testLast() {
        assertNull(EnumUtil.last(TestNullItemEnum.class));
    }
}