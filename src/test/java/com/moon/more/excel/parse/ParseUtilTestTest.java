package com.moon.more.excel.parse;

import com.moon.core.util.RandomStringUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class ParseUtilTestTest {

    public static class UserNo2 {

        private List<UserNo1> userNo1List;
    }

    public static class UserNo1 {

        private List<String> keywords;

        private String name;
    }

    @Test
    void testParseListable2() throws Exception {
        MarkColumnGroup g1 = (MarkColumnGroup) ParseUtil.parse(No1.class);
        assertEquals(0, g1.getColumns().length);
        assertThrows(Exception.class, () -> ParseUtil.parse(No2.class));
        MarkColumnGroup g3 = (MarkColumnGroup) ParseUtil.parse(No3.class);
        assertEquals(1, g3.getColumns().length);
    }

    public static class No3 {

        @TableColumn
        List<String> list1;
        List<String> list2;
    }

    public static class No2 {

        @TableColumn
        List<String> list1;
        @TableColumn
        List<String> list2;
    }

    public static class No1 {

        List<String> list1;
        List<String> list2;
    }

    @Test
    @Disabled
    void testNestedFlatten() throws Exception {

        List<Third> details = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            details.add(new Third());
        }
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.row(rowFactory -> {
                rowFactory.next("1");
                rowFactory.next("2");
                rowFactory.next("3");
            });
            sheetFactory.table(tableFactory -> {
                tableFactory.renderBody(details);
            });
            sheetFactory.row(factory -> {
                Date date = new Date();
                factory.next(date);
                factory.next(date, 3);
            });
        }).write2Filepath("D:/test.xlsx");

    }

    public static class Third {

        @TableColumnFlatten
        private Second second = new Second();

        @TableColumn
        private long value;

        @TableIndexer
        @TableColumn
        private long score;
    }

    public static class Second {

        @TableColumnFlatten
        private First first = new First();

        @TableIndexer
        @TableColumn
        private int length = 128;

        @TableColumn
        private String address = RandomStringUtil.nextChinese(4, 12);
    }

    public static class First {

        private String name = RandomStringUtil.nextChinese(4);
        private String age = RandomStringUtil.nextDigit(2);
        private String sex = RandomStringUtil.nextDigit(1);
    }


    public static class StuffEntity {

        @TableColumn
        private List<String> names;
    }

    public static class BuildingEntity {

        @TableColumn
        private List names;
    }

    @Test
    void testParseListable() throws Exception {
        MarkColumnGroup group = (MarkColumnGroup) ParseUtil.parse(StuffEntity.class);
        group = (MarkColumnGroup) ParseUtil.parse(BuildingEntity.class);
        System.out.println();
    }
}