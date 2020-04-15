package com.moon.more.excel.parse;

import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.annotation.DataColumn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

/**
 * @author benshaoye
 */
class PropGetterParsedTestTest {

    @BeforeEach
    void setUp() { }

    @Test
    void testName() {
        ParsedDetail<DefinedGet> parsed = ParseUtil.parseGetter(UserDetail.class);
        System.out.println();
        parsed.columns.forEach(col -> {
            System.out.println(Arrays.toString(col.getColumn().value()));
            System.out.println(col.getPropertyName());
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