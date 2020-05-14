package com.moon.more.excel.parse;

import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class CoreParserTestTest extends ParseUtil {

    @Test
    void testDoParseAsCol() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new Top());
            });
        }).write2Filepath("D:/test.xlsx");
    }

    @Test
    void testDoParseAsCol1() {
        parse(Top.class);
    }

    public static class Top {

        @TableColumn
        private String name = RandomStringUtil.nextChinese(2, 5);

        @TableColumnFlatten
        List<Middle> middleList;

        public Top() {
            List<Middle> middleList = new ArrayList<>();
            this.middleList = middleList;
            for (int i = 0; i < RandomUtil.nextInt(5, 10); i++) {
                middleList.add(new Middle(i));
            }
        }
    }

    public static class Middle {

        @TableColumn
        private List<String> names;

        @TableColumnFlatten
        private Bottom bottom;

        @TableColumn
        private String value = "Value: 12";

        List<Bottom> bottomList;

        public Middle(int index) {
            this.bottom = new Bottom(index);
            Class cls = getClass();
            List<String> names = new ArrayList<>();
            this.names = names;
            for (int i = 0; i < RandomUtil.nextInt(5, 10); i++) {
                names.add("【" + (i + 1) + "】Name is: " + cls.getSimpleName() + RandomStringUtil.nextUpper(2));
            }
        }
    }

    public static class Bottom {

        @TableColumn
        String name;

        @TableColumn
        int age = 24;

        // @TableColumn
        List<String> values;

        public Bottom(int index) {
            this.name = (index + 1) + "Bottom: 张三";
        }
    }

    @Test
    void testDoParseAsCol4Middle() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new Middle(0));
            });
        }).write2Filepath("D:/test.xlsx");
    }

    @Test
    void testDuplicateList() {
        assertThrows(Exception.class, () -> {
            try {
                parse(DuplicateIterated.class);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                System.out.println();
                throw e;
            }
        });
        assertThrows(Exception.class, () -> {
            try {
                parse(Duplicated.class);
            } catch (RuntimeException e) {

                System.out.println(e.getMessage());
                System.out.println();
                throw e;
            }
        });
    }

    public static class Duplicated {

        @TableColumn
        List<String> names;

        @TableColumnFlatten
        Flatted flatted;
    }

    private static class Flatted {

        @TableColumn
        List<String> values;
    }

    public static class DuplicateIterated {

        @TableColumn
        List<String> names;

        @TableColumn
        List<String> values;
    }

    @Test
    void testCopyArray() throws Exception {
        String[] empty = new String[0];
        String[] values = new String[4];
        List<String> list = ListUtil.newArrayList("1", "1", "3", "4");
        String[] copied = list.toArray(values);
        assertSame(values, copied);

        copied = list.toArray(empty);
        assertArrayEquals(values, copied);
    }

    @Test
    void testExportBody() {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory
                    .renderBody(new TestExportBody("admin"), new TestExportBody("user"), new TestExportBody("default"),
                        new TestExportBody("test"));
            });
        }).write2Filepath("D:/test.xlsx");
    }

    public static class TestExportBody {

        @TableColumn
        private String username = "admin";
        @TableColumn
        private String password = "123456";
        @TableColumn
        private List<Integer> scores;

        public TestExportBody(String name) {
            this.username = name;
            this.scores = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                scores.add(i + 1);
            }
        }
    }

    @Test
    void testExportBody2() {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new TestExportBody2("test2"));
            });
        }).write2Filepath("D:/test.xlsx");
    }

    public static class TestExportBody2 {

        @TableColumn
        private String username = "admin";
        @TableColumn
        private String password = "123456";
        @TableColumnFlatten
        private List<TestExportBody3> scores;

        public TestExportBody2(String name) {
            this.username = name;
            this.scores = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                scores.add(new TestExportBody3("admin3" + RandomStringUtil.nextUpper(2) + (i + 1)));
            }
        }
    }

    public static class TestExportBody3 {

        @TableColumn
        private String username = "admin";
        @TableColumn
        private String password = "123456";
        @TableColumnFlatten
        private List<TestExportBody4> scores;

        public TestExportBody3(String name) {
            this.username = name;
            this.scores = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                scores.add(new TestExportBody4("Body4" + RandomStringUtil.nextLower(2) + (i + 1)));
            }
            scores.add(null);
        }
    }

    @ToString
    public static class TestExportBody4 {

        @TableColumn
        private String username = "admin";
        @TableColumn
        private String password = "123456";
        @TableColumn
        private List<Integer> scores;

        public TestExportBody4(String name) {
            this.username = name;
            this.scores = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                scores.add(i + 1);
            }
            scores.add(null);
        }
    }
}