package com.moon.more.excel;

import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.parse.ParseUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author benshaoye
 */
class SheetFactoryTestTest extends ParseUtil {

    public static class BasicInfo {

        @TableColumn
        private String name = "张三";

        @TableColumn
        private String sex = String.valueOf(RandomUtil.nextBoolean());

        @TableColumn
        private int age = RandomUtil.nextInt(14, 28);
    }

    @TableIndexer(startingAt = 5)
    public static class MemberDetail {

        @TableIndexer
        @TableColumn
        private String value;

        @TableColumnFlatten
        private BasicInfo info = new BasicInfo();

        @TableColumn
        private String address = RandomStringUtil.nextChinese(10, 24);

        @TableColumn
        private String school = RandomStringUtil.nextUpper(10);

        @TableIndexer(startingAt = 501, step = 3)
        @TableColumn
        private String[] namesArr = {"1", "2", "3"};

        @TableColumn
        private List<String> namesList = new ArrayList(Arrays.asList(namesArr));
    }

    @Test
    @Disabled
    void testTable() throws Exception {
        List<MemberDetail> details = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            details.add(new MemberDetail());
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
}