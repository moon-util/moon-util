package com.moon.more.excel.parse;

import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
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
                middleList.add(new Middle());
            }
        }
    }

    public static class Middle {

        @TableColumn
        private List<String> names;

        @TableColumnFlatten
        private Bottom bottom = new Bottom();

        @TableColumn
        private String value = "Value: 12";

        List<Bottom> bottomList;

        public Middle() {
            List<String> names = new ArrayList<>();
            this.names = names;
            for (int i = 0; i < RandomUtil.nextInt(5, 10); i++) {
                names.add("【" + (i + 1) + "】Name is: " + RandomStringUtil.nextLetter(4, 6));
            }
        }
    }

    public static class Bottom {

        @TableColumn
        String name = "Name: 张三";

        @TableColumn
        int age = 24;

        // @TableColumn
        List<String> values;
    }

    @Test
    void testDoParseAsCol4Middle() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(new Middle());
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
}