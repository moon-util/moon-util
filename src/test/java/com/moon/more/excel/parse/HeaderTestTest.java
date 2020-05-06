package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Detail<DefinedGet> getter = ParseUtil.parseGetter(UserDetails.class);
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
        Detail<DefinedGet> getter = ParseUtil.parseGetter(StudentScore.class);
    }

    @Test
    void testTestTreeMap() {
    }
}