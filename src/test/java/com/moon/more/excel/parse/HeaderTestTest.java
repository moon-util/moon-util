package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author benshaoye
 */
class HeaderTestTest {

    public static class UserDetails {

        @DataColumnFlatten({"基本信息", "列表"})
        private BasicInfo info;
        @DataColumnFlatten({"基本信息", "列表"})
        private StudentScore studentScore;
        @DataColumn({"基本信息", "姓名"})
        private String name;
        @DataColumn({"基本信息", "性别"})
        private String sex;
        @DataColumn({"基本信息", "年龄"})
        private int age;
        @DataColumn({"成绩", "语文"})
        private String chinese;
        @DataColumn({"成绩", "数学"})
        private String math;
        @DataColumn({"成绩", "理综"})
        private int natural;
        @DataColumn({"成绩", "英语"})
        private int english;
    }

    @Test
    void testParseHeadIfPriorityOrder() {
        ParsedDetail<DefinedGet> getter = ParseUtil.parseGetter(UserDetails.class);
        Assertions.assertEquals(3, getter.getMaxRowsLength());
    }

    public static class BasicInfo {

        @DataColumn("姓名")
        private String name;
        @DataColumn("性别")
        private String sex;
        @DataColumn("年龄")
        private int age;
    }

    public static class StudentScore {

        @DataColumnFlatten()
        private BasicInfo info;

        @DataColumn
        private String mathScore;

        @DataColumn
        private String englishScore;

        public BasicInfo getInfo() {
            return info;
        }
    }

    @Test
    void testName() {
        ParsedDetail<DefinedGet> getter = ParseUtil.parseGetter(StudentScore.class);
    }

    @Test
    void testTestTreeMap() {
        Map<Header.HeaderCell, Object> map = new TreeMap<>();

        map.put(new Header.HeaderCell("微信", 0), this);
        map.put(new Header.HeaderCell("电脑", 2), this);
        map.put(new Header.HeaderCell("鼠标", 100), this);
        map.put(new Header.HeaderCell("打印机", 20), this);
        map.put(new Header.HeaderCell("面膜", 50), this);
        map.put(new Header.HeaderCell("微信", 2), this);
        map.put(new Header.HeaderCell("微信", 1), this);
        map.put(new Header.HeaderCell("微信", -1), this);

        map.forEach((k, v) -> {
            System.out.println(k);
        });
    }
}