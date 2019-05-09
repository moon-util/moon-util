package com.moon.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
public class ArrayUtilTest {

    @Test
    void testSplice() {
        boolean[] arr = {false, true, false, true};
        assertEquals(JoinerUtil.join(arr, ","), "false,true,false,true");
        boolean[] newArr = ArrayUtil.splice(arr, 1, 1, true, true);
        assertEquals(JoinerUtil.join(newArr, ","), "false,true,true,false,true");

        int[] ints = {1, 2, 3, 4, 5};
        assertEquals(JoinerUtil.join(ints, ","), "1,2,3,4,5");
        int[] newInts = ArrayUtil.splice(ints, 1, 4, 11, 11, 11, 11);
        assertEquals(JoinerUtil.join(newInts, ","), "1,11,11,11,11");
        newInts = ArrayUtil.splice(ints, 1, 1, 11, 11, 11, 11);
        assertEquals(JoinerUtil.join(newInts, ","), "1,11,11,11,11,3,4,5");

        Object[] objects = {1, 2, 3, 4, 5};
        assertEquals(JoinerUtil.join(objects, ","), "1,2,3,4,5");
        Object[] newObjects = ArrayUtil.splice(objects, 1, 4, 11, 11, 11, 11);
        assertEquals(JoinerUtil.join(newObjects, ","), "1,11,11,11,11");
    }
}
