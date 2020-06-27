package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class ValidateUtilTestTest {

    static List<String> getRandomStrings(){
        List<String> strings = ListUtil.newArrayList();
        strings.add(RandomStringUtil.nextLower(12));
        IteratorUtil.forEach(20, idx -> {
            strings.add(RandomStringUtil.nextLower(11));
        });
        return strings;
    }

    @Test
    void testOfCollect() {
        ValidateUtil.ofCollect(getRandomStrings()).preset(list -> {
            list.forEach(System.out::println);
        }).requireAtMost1(str -> str.length() == 12, "最多只能有一项长度为12").ifValid(list -> {
            // todo something
        }).forEach(validator -> {
        }).get();
    }
}