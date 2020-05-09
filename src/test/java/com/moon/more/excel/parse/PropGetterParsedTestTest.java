package com.moon.more.excel.parse;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.ExcelUtil;
import com.moon.more.excel.annotation.TableColumn;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Cell;
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

    @ToString
    public static class EmployeeDetail {

        @TableColumn
        private String name = "张三" + RandomStringUtil.nextDigit(4);

        @TableColumn
        private String sex = RandomUtil.nextBoolean() ? "男" : "女";

        @TableColumn
        private int age = RandomUtil.nextInt(10, 20);

        @TableColumn
        private String schoolName = "北大（" + RandomUtil.nextInt(5) + "）中";
    }

    @Test
    void testGenerateDetailExcel() {
        PropertiesGroupGet get = ParseUtil.parseGetter(EmployeeDetail.class);
        System.out.println();
        get.forEach(definedGet -> {
            System.out.println(definedGet.getName());
            System.out.println(definedGet.getPropertyType());
            System.out.println(ArrayUtil.stringify(definedGet.getHeadLabels()));
        });
    }


    @Test
    void testEmployeeDetailExport() throws Exception {
        List<EmployeeDetail> details = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            details.add(new EmployeeDetail());
        }
        PropertiesGroupGet detailGet = ParseUtil.parseGetter(EmployeeDetail.class);
        // details.forEach(emp -> {
        //     StringBuilder data = new StringBuilder();
        //     detailGet.forEach(get -> {
        //         data.append(get.getName()).append(": ").append(get.getValue(emp)).append("; ");
        //     });
        //     System.out.println(data);
        // });
    }

    @Test
    void testName() {
        PropertiesGroup<PropertyGet> parsed = ParseUtil.parseGetter(UserDetail.class);
        System.out.println();
        parsed.columns.forEach(col -> {
            System.out.println(Arrays.toString(col.getColumn().value()));
            System.out.println(col.getName());
        });
    }

    public static class UserDetail {

        @TableColumn({"基本信息", "姓名"})
        private String name = "张三" + RandomStringUtil.nextDigit(4);

        @TableColumn({"基本信息", "性别"})
        private String sex = RandomUtil.nextBoolean() ? "男" : "女";

        @TableColumn({"基本信息", "年龄"})
        private int age = RandomUtil.nextInt(10, 20);

        @TableColumn({"学历信息", "毕业院校"})
        private String schoolName = RandomUtil.nextBoolean() ? "男" : "女";

        @TableColumn({"学历信息", "毕业时间"})
        private Date datetime = new Date();

        @TableColumn({"学历信息", "学历"})
        private String education = RandomStringUtil.nextDigit(4);

        private String description = RandomStringUtil.nextDigit(4);
    }
}