package com.moon.more.excel.table;

import com.moon.core.lang.IntUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author benshaoye
 */
class TableParserTestTest {

    @Test
    void testParseHasGroup() throws Exception {
        // TableParser.parseConfiguration(Employee.class);
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<Student> students = ListUtil.newArrayList(new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student());

                tableFactory.renderHead(Employee.class);
            });
        }).write2Filepath("/Users/moonsky/group-header.xlsx");

    }

    public static class Employee {

        @TableColumn(value = {"部门"}, order = 1)
        private String department;

        private EmpBasicInfo basicInfo;

        @TableColumnGroup({"学生信息"})
        private Student student;

        @TableColumnGroup(value = {"基本信息"}, order = -1)
        public EmpBasicInfo getBasicInfo() {
            return basicInfo;
        }
    }

    public static class Score {

        @TableColumn({"语文"})
        private String chinese;
        @TableColumn({"数学"})
        private String math;
        @TableColumn({"英语"})
        private String english;
    }

    public static class EmpBasicInfo {


        @TableColumn({"姓名"})
        private String name;


        @TableColumn({"性别"})
        private String sex;


        @TableColumn(value = {"年龄"}, order = 2)
        private int age;


        @TableColumn(value = {"地址"}, order = 2)
        private String address;

        @TableColumnGroup(value = "各科成绩", order = 1)
        private Score score;
    }

    @Test
    void testExportStudentScore() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<Student> students = ListUtil.newArrayList(new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student(),
                    new Student());

                tableFactory.renderList(students);
            });
        }).write2Filepath("/Users/moonsky/score.xlsx");
    }

    public static class Student {

        @TableColumn({"基本信息", "姓名", "名字"})
        private String name = "张三";

        @TableColumn({"基本信息", "性别"})
        private String sex = RandomUtil.nextBoolean() ? "男" : "女";

        @TableColumn({"基本信息", "年龄"})
        private int age = RandomUtil.nextInt(15, 18);

        @TableColumn({"基本信息", "家庭住址"})
        private String address = "北京市新发地菜市场";

        @TableColumn({"成绩", "语文"})
        private int chineseScore = RandomUtil.nextInt(60, 95);

        @TableColumn({"成绩", "英语"})
        private int englishScore = RandomUtil.nextInt(60, 95);

        @TableColumn({"成绩", "数学"})
        private int mathScore = RandomUtil.nextInt(60, 95);

        @TableColumn({"成绩", "物理"})
        private int physicalScore = RandomUtil.nextInt(60, 95);

        @TableColumn({"成绩", "滑雪"})
        private int skiScore = RandomUtil.nextInt(60, 95);

        @TableColumn({"总分"})
        public int getTotalScore() {
            return IntUtil.sum(chineseScore, englishScore, mathScore, physicalScore, skiScore);
        }
    }
}