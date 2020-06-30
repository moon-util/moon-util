package com.moon.core.util.validator;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.DateUtil;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author moonsky
 */
class CollectValidatorTestTest {

    public static class Employee {

        String name;
    }

    @Test
    void testOf() {
        List<String> collect = ListUtil
            .newArrayList("1111", "22222", "333333", "4444444", "55555555", "dsfgsdfgasdf", "sdfbhdfhnrththn");
        Assertions.assertThrows(Exception.class, () -> {
            CollectValidator.of(collect).setImmediate(true).requireCountOf(3, str -> str.length() < 2);
        });
        Assertions.assertTrue(CollectValidator.of(collect).require(list -> list.size() == 7).isValid());
        assertFalse(CollectValidator.of(collect).require(list -> list.size() == 7).isInvalid());
        assertFalse(CollectValidator.of(collect).requireAtLeastOf(5, str -> str.length() > 3).isInvalid());
        Assertions.assertTrue(CollectValidator.of(collect).requireAtMostOf(5, str -> str.length() > 3).isInvalid());
        CollectValidator va = CollectValidator.of(collect);
        Assertions.assertTrue(va == va.current());

    }

    @Test
    void testCertValid() {
        ThrowUtil.ignoreThrowsRun(() -> doCertValid());
    }

    void doCertValid() {
        String path = "D:/cert-nos.txt";
        IteratorUtil.forEachLines(new File(path), str -> {
            if (str.trim().length() == 18) {
                testOut(str.trim());
            }
        });
        testOut("610525199401201946");
    }

    void testOut(final String certNo) {
        String year = certNo.substring(6, 10);
        final String certYearMonth = certNo.substring(6, 14);
        final int targetYear = DateUtil.getYear(DateUtil.parse(certYearMonth, "yyyyMMdd"));
        final int nowYear = LocalDate.now().getYear();
        final int age = nowYear - targetYear;
        final int sex = (certNo.charAt(16) - 48) % 2;
        if ((sex == 0 && age > 43) || (sex == 1 && age > 53)) {
            assertTrue(checkCertNoAgeDiffGender(certNo));
            // System.out.println("对不起，您的年龄（男53岁及以上，女43岁及以上）不符合条件。certNo：{}，sex：{}，age：{}", certNo, sex, age);
        } else {
            assertFalse(checkCertNoAgeDiffGender(certNo));
            // System.out.println("成功：{}", certNo);
        }
    }

    private static boolean checkCertNoAgeDiffGender(String certNo) {
        final String certYearMonth = certNo.substring(6, 10);
        int targetYear = Integer.valueOf(certYearMonth);
        final int nowYear = java.time.LocalDate.now().getYear();
        final int age = nowYear - targetYear;
        final int sex = (certNo.charAt(16) - 48) % 2;
        return (sex == 0 && age > 43) || (sex == 1 && age > 53);
    }

    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    @Test
    void testName() {
        System.out.println(HASH_BITS);
    }
}