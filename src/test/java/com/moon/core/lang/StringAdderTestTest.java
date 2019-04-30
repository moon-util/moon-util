package com.moon.core.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class StringAdderTestTest {

    @Test
    void testOf() {
    }

    @Test
    void testAdd() {
        Assertions.assertEquals(StringJoiner.of("|").add(30).length(), 2);
        Assertions.assertEquals(StringJoiner.of("|").add(30).get(), "30");
        Assertions.assertEquals(StringJoiner.of("|").add(true).get(), "true");
        Assertions.assertEquals(StringJoiner.of("|").add(false).get(), "false");
    }

    @Test
    void testJoin() {
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        Assertions.assertEquals(StringJoiner.of(",").join(ints).get(), "1,2,3,4,5,6,7,8,9");
        Assertions.assertEquals(StringJoiner.of(",", "123", "456").join(ints).get(), "1231,2,3,4,5,6,7,8,9456");
        Assertions.assertEquals(StringJoiner.of(",", "123", "456").get(), "123456");

        String[] arr = {"aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh", "ii"};
        Assertions.assertEquals(StringJoiner.of(",", "123|", "|456").join(arr).get(), "123|aa,bb,cc,dd,ee,ff,gg,hh,ii|456");
        String[] arr1 = {"aa", "bb", "cc", null, "ee", "ff", null, "hh", "ii"};
        Assertions.assertEquals(StringJoiner.of(",", "123|", "|456").join(arr1).get(), "123|aa,bb,cc,null,ee,ff,null,hh,ii|456");
    }

    @Test
    void testMerge() {
    }

    @Test
    void testReset() {
    }

    @Test
    void testSetStringifier() {
    }

    @Test
    void testSetStringifier1() {
    }

    @Test
    void testSetPrefix() {
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        StringJoiner adder = StringJoiner.of(",", "(", ")");
        adder.join(ints);
        Assertions.assertEquals(adder.get(), "(1,2,3,4,5,6,7,8,9)");
        adder.setPrefix("{");
        Assertions.assertEquals(adder.get(), "{1,2,3,4,5,6,7,8,9)");
    }

    @Test
    void testSetSuffix() {
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        StringJoiner adder = StringJoiner.of(",", "(", ")");
        adder.join(ints);
        Assertions.assertEquals(adder.get(), "(1,2,3,4,5,6,7,8,9)");

        Assertions.assertEquals(adder.setPrefix("{").setSuffix("]").get(), "{1,2,3,4,5,6,7,8,9]");
    }

    @Test
    void testSkipNulls() {
        String[] arr1 = {"aa", "bb", "cc", null, "ee", "ff", null, "hh", "ii"};
        StringJoiner adder = StringJoiner.of(",", "123|", "|456").skipNulls().join(arr1);
        Assertions.assertEquals(adder.get(), "123|aa,bb,cc,ee,ff,hh,ii|456");
    }

    @Test
    void testRequireNonNull() {
        String[] arr1 = {"aa", "bb", "cc", null, "ee", "ff", null, "hh", "ii"};
        Assertions.assertThrows(NullPointerException.class, () -> {
            StringJoiner adder = StringJoiner.of(",", "123|", "|456").requireNonNull().join(arr1);
            Assertions.assertEquals(adder.get(), "123|aa,bb,cc,ee,ff,hh,ii|456");
        });
    }

    @Test
    void testUseForNull() {
        String[] arr1 = {"aa", "bb", "cc", null, "ee", "ff", null, "hh", "ii"};
        StringJoiner adder = StringJoiner.of(",", "123|", "|456").useForNull("159357").join(arr1);
        Assertions.assertEquals(adder.get(), "123|aa,bb,cc,159357,ee,ff,159357,hh,ii|456");
    }

    @Test
    void testSetDelimiter() {
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        String[] arr1 = {"aa", "bb", "cc", null, "ee", "ff", null, "hh", "ii"};
        StringJoiner adder = StringJoiner.of(",", "123|", "|456")
            .useForNull("159357").join(arr1).setDelimiter("|").join(ints);
        Assertions.assertEquals(adder.get(), "123|aa,bb,cc,159357,ee,ff,159357,hh,ii|1|2|3|4|5|6|7|8|9|456");
    }

    @Test
    void testAddDelimiter() {
        String[] arr1 = {"aa", "bb", "cc", null, "ee", "ff", null, "hh", "ii"};
        StringJoiner adder = StringJoiner.of(",", "123|", "|456").useForNull("159357").appendDelimiter().join(arr1);
        Assertions.assertEquals(adder.get(), "123|,aa,bb,cc,159357,ee,ff,159357,hh,ii|456");
        adder.appendDelimiter();
        Assertions.assertEquals(adder.get(), "123|,aa,bb,cc,159357,ee,ff,159357,hh,ii,|456");
    }

    @Test
    void testAppendTo() {
    }

    @Test
    void testToString() {
    }

    @Test
    void testLength() {
    }

    @Test
    void testCharAt() {
    }

    @Test
    void testSubSequence() {
    }

    @Test
    void testGet() {
    }

    @Test
    void testGetValue() {
    }

    @Test
    void testAppend() {
    }
}