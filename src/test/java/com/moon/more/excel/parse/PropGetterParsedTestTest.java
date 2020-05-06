package com.moon.more.excel.parse;

import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.DataColumn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author benshaoye
 */
class PropGetterParsedTestTest {

    @BeforeEach
    void setUp() { }

    public static class EmployeeDetail {

        @DataColumn
        private String name = "张三" + RandomStringUtil.nextDigit(4);

        @DataColumn
        private String sex = RandomUtil.nextBoolean() ? "男" : "女";

        @DataColumn
        private int age = RandomUtil.nextInt(10, 20);

        @DataColumn
        private String schoolName = "北大（" + RandomUtil.nextInt(5) + "）中";
    }

    @Test
    void testEmployeeDetailExport() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            List<EmployeeDetail> details = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                details.add(new EmployeeDetail());
            }
            sheetFactory.renderList(details);
        });
    }

    @Test
    void testName() {
        ParsedDetail<DefinedGet> parsed = ParseUtil.parseGetter(UserDetail.class);
        System.out.println();
        parsed.columns.forEach(col -> {
            System.out.println(Arrays.toString(col.getColumn().value()));
            System.out.println(col.getName());
        });
    }

    public static class UserDetail {

        @DataColumn({"基本信息", "姓名"})
        private String name = "张三" + RandomStringUtil.nextDigit(4);

        @DataColumn({"基本信息", "性别"})
        private String sex = RandomUtil.nextBoolean() ? "男" : "女";

        @DataColumn({"基本信息", "年龄"})
        private int age = RandomUtil.nextInt(10, 20);

        @DataColumn({"学历信息", "毕业院校"})
        private String schoolName = RandomUtil.nextBoolean() ? "男" : "女";

        @DataColumn({"学历信息", "毕业时间"})
        private Date datetime = new Date();

        @DataColumn({"学历信息", "学历"})
        private String education = RandomStringUtil.nextDigit(4);

        private String description = RandomStringUtil.nextDigit(4);
    }
}