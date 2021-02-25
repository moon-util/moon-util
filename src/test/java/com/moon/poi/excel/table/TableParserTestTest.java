package com.moon.poi.excel.table;

import com.moon.core.lang.IntUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomUtil;
import com.moon.poi.excel.ExcelUtil;
import com.moon.poi.excel.annotation.SheetColumn;
import com.moon.poi.excel.annotation.SheetColumnGroup;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.List;

/**
 * @author moonsky
 */
class TableParserTestTest {

    @ParameterizedTest
    @ValueSource(strings = "D:/")
    @EnabledOnOs(OS.WINDOWS)
    void testParseHasGroupOnWindows(String dir) throws Exception {
        testParseHasGroup(dir);
    }

    @ParameterizedTest
    @ValueSource(strings = "/Users/moonsky")
    @EnabledOnOs({OS.MAC})
    void testParseHasGroupOnMac(String dir) throws Exception {
        testParseHasGroup(dir);
    }

    void testParseHasGroup(String dir) throws Exception {
        // TableParser.parseConfiguration(Employee.class);
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<Student> students = ListUtil.newList(new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student());

                tableFactory.render(Employee.class);
            });
        }).write(new File(dir, "group-header.xlsx"));

    }

    public static class Employee {

        // @TableColumnOffset(2)
        @SheetColumn(value = {"部门"}, order = 1, offset = 1)
        private String department;

        private EmpBasicInfo basicInfo;

        @SheetColumn({"学生信息"})
        @SheetColumnGroup()
        private Student student;

        @SheetColumn({"基本信息"})
        @SheetColumnGroup
        public EmpBasicInfo getBasicInfo() {
            return basicInfo;
        }
    }

    public static class Score {

        @SheetColumn({"语文"})
        private String chinese;

        @SheetColumn({"数学"})
        private String math;

        @SheetColumn({"英语"})
        private String english;
    }

    public static class EmpBasicInfo {


        @SheetColumn({"姓名"})
        private String name;


        @SheetColumn({"性别"})
        private String sex;


        @SheetColumn(value = {"年龄"}, order = 2)
        private int age;


        @SheetColumn(value = {"地址"}, order = 2)
        private String address;

        @SheetColumnGroup
        private Score score;
    }

    @ParameterizedTest
    @ValueSource(strings = "D:/")
    @EnabledOnOs(OS.WINDOWS)
    void testExportStudentScoreOnWindows(String dir) throws Exception {
        testExportStudentScore(dir);
    }

    @ParameterizedTest
    @ValueSource(strings = "/Users/moonsky")
    @EnabledOnOs({OS.MAC})
    void testExportStudentScoreOnMac(String dir) throws Exception {
        testExportStudentScore(dir);
    }

    void testExportStudentScore(String dir) throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<Student> students = ListUtil.newList(new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student());

                tableFactory.render(students);
            });
        }).write(new File(dir, "score.xlsx"));
    }

    public static class Student {

        @SheetColumn({"基本信息", "姓名", "名字"})
        private String name = "张三";

        @SheetColumn({"基本信息", "性别"})
        private String sex = RandomUtil.nextBoolean() ? "男" : "女";

        @SheetColumn({"基本信息", "年龄"})
        private int age = RandomUtil.nextInt(15, 18);

        @SheetColumn({"基本信息", "家庭住址"})
        private String address = "北京市新发地菜市场";

        @SheetColumn({"成绩", "语文"})
        private int chineseScore = RandomUtil.nextInt(60, 95);

        // @TableColumnOffset(1)
        @SheetColumn(value = {"成绩", "英语"}, offset = 1)
        private int englishScore = RandomUtil.nextInt(60, 95);

        @SheetColumn({"成绩", "数学"})
        private int mathScore = RandomUtil.nextInt(60, 95);

        @SheetColumn({"成绩", "物理"})
        private int physicalScore = RandomUtil.nextInt(60, 95);

        @SheetColumn({"成绩", "滑雪"})
        private int skiScore = RandomUtil.nextInt(60, 95);

        // @TableColumnOffset(2)
        @SheetColumn(value = {"总分"}, offset = 2)
        public int getTotalScore() {
            return IntUtil.sum(chineseScore, englishScore, mathScore, physicalScore, skiScore);
        }
    }
}