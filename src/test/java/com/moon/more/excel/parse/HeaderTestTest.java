package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class HeaderTestTest {

    public static class UserDetails {

        @TableColumnFlatten({"基本信息", "列表"})
        private BasicInfo info;
        @TableColumnFlatten({"基本信息", "列表"})
        private StudentScore studentScore;
        @TableColumn({"基本信息", "姓名"})
        private String name;
        @TableColumn({"基本信息", "性别"})
        private String sex;
        @TableColumn({"基本信息", "年龄"})
        private int age;
        @TableColumn({"成绩", "语文"})
        private String chinese;
        @TableColumn({"成绩", "数学"})
        private String math;
        @TableColumn({"成绩", "理综"})
        private int natural;
        @TableColumn({"成绩", "英语"})
        private int english;
    }

    @Test
    void testParseHeadIfPriorityOrder() {
        PropertiesGroup<PropertyGet> getter = ParseUtil.parseGetter(UserDetails.class);
    }

    public static class BasicInfo {

        @TableColumn("姓名")
        private String name;
        @TableColumn("性别")
        private String sex;
        @TableColumn("年龄")
        private int age;
    }

    public static class StudentScore {

        @TableColumnFlatten()
        private BasicInfo info;

        @TableColumn
        private String mathScore;

        @TableColumn
        private String englishScore;

        public BasicInfo getInfo() {
            return info;
        }
    }

    @Test
    void testName() {
        PropertiesGroup<PropertyGet> getter = ParseUtil.parseGetter(StudentScore.class);
    }

    @Test
    void testTestTreeMap() {
    }
}