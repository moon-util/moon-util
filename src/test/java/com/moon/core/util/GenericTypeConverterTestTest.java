package com.moon.core.util;

import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author benshaoye
 */
class GenericTypeConverterTestTest {

    static final Requires REQUIRES = Requires.of();

    @Test
    void testRegister() {
    }

    @Test
    void testRegisterIfAbsent() {
    }

    @Test
    void testToType() {
    }

    @Test
    void testToBooleanValue() {
    }

    @Test
    void testToBoolean() {
    }

    @Test
    void testToCharValue() {
    }

    @Test
    void testToCharacter() {
    }

    @Test
    void testToByteValue() {
    }

    @Test
    void testToByte() {
    }

    @Test
    void testToShortValue() {
    }

    @Test
    void testToShort() {
    }

    @Test
    void testToIntValue() {
    }

    @Test
    void testToInteger() {
    }

    @Test
    void testToLongValue() {
    }

    @Test
    void testToLong() {
    }

    @Test
    void testToFloatValue() {
    }

    @Test
    void testToFloat() {
    }

    @Test
    void testToDoubleValue() {
    }

    @Test
    void testToDouble() {
    }

    @Test
    void testToBigInteger() {
    }

    @Test
    void testToBigDecimal() {
    }

    @Test
    void testToDate() {
    }

    @Test
    void testToSqlDate() {
    }

    @Test
    void testToTimestamp() {
    }

    @Test
    void testToTime() {
    }

    @Test
    void testToCalendar() {
    }

    @Test
    void testToString() {
    }

    @Test
    void testToStringBuilder() {
    }

    @Test
    void testToStringBuffer() {
    }

    @Test
    void testToEnum() {
    }

    @Test
    void testToBean() {
    }

    @Test
    void testToArray() {
        type = Array.class;
        res = TypeUtil.cast().toArray(0, type);
        REQUIRES.requireTrue(res instanceof Object[]);
        REQUIRES.requireTrue(res.getClass().isArray());
        REQUIRES.requireTrue(res.getClass().getComponentType() == Object.class);
    }

    @Test
    void testToTypeArray() {
    }

    @Test
    void testToMap() {
    }

    @Test
    void testToList() {

        type = ArrayList.class;
        res = TypeUtil.cast().toCollection(0, type);
        REQUIRES.requireInstanceOf(res, type);
        REQUIRES.requireEq(CollectUtil.sizeByObject(res), 1);
        REQUIRES.requireTrue(SetUtil.contains((ArrayList) res, 0));
        REQUIRES.requireEquals(ListUtil.getByObject(res, 0), 0);

        type = LinkedList.class;
        res = TypeUtil.cast().toCollection(0, type);
        REQUIRES.requireInstanceOf(res, type);
        REQUIRES.requireEq(CollectUtil.sizeByObject(res), 1);
        REQUIRES.requireTrue(SetUtil.contains((LinkedList) res, 0));
        REQUIRES.requireEquals(ListUtil.getByObject(res, 0), 0);

        type = List.class;
        res = TypeUtil.cast().toCollection(0, type);
        REQUIRES.requireInstanceOf(res, type);
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(CollectUtil.sizeByObject(res), 1);
        REQUIRES.requireTrue(SetUtil.contains((ArrayList) res, 0));
        REQUIRES.requireEquals(ListUtil.getByObject(res, 0), 0);
    }

    Object res, data;
    Class type;

    @Test
    void testToCollection() {
        type = Collection.class;
        res = TypeUtil.cast().toCollection(0, type);
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(CollectUtil.sizeByObject(res), 1);
        REQUIRES.requireTrue(SetUtil.contains((ArrayList) res, 0));

        type = HashSet.class;
        res = TypeUtil.cast().toCollection(0, type);
        REQUIRES.requireInstanceOf(res, type);
        REQUIRES.requireEq(CollectUtil.sizeByObject(res), 1);
        REQUIRES.requireTrue(SetUtil.contains((HashSet) res, 0));
    }
}