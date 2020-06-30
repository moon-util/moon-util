package com.moon.core.util;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class IteratorUtilTestTest {

    @Test
    void testForEach() {
        IteratorUtil.forEach(20, System.out::println);
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        IteratorUtil.forEach(ints, System.out::println);

        String[] arr = {"111", "222", "333", "444", "555"};
        IteratorUtil.forEach(arr, System.out::println);
        IteratorUtil.forEach(arr, (str, index) ->
            System.out.println(String.format("index: %d, value: %s", index, str)));
    }
}