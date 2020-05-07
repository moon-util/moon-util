package com.moon.more.excel;

import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableListable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class SheetFactoryTestTest {

    public static class BasicInfo {

        @TableColumn
        private String name = "张三";

        @TableColumn
        private String sex = String.valueOf(RandomUtil.nextBoolean());

        @TableColumn
        private int age = RandomUtil.nextInt(14, 28);
    }

    public static class MemberDetail {

        @TableColumnFlatten
        private BasicInfo info = new BasicInfo();

        @TableColumn
        private String address = RandomStringUtil.nextChinese(10, 24);

        @TableColumn
        private String school = RandomStringUtil.nextUpper(10);

        @TableColumn
        private String[] namesArr = {"1", "2", "3"};

        @TableColumn
        private List<String> namesList = new ArrayList(Arrays.asList(namesArr));
    }

    @Test
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
        }).write2Filepath("D:/test.xlsx");
    }
}