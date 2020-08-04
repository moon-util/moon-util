package com.moon.poi.excel;

import com.moon.core.util.ListUtil;
import com.moon.core.util.RandomUtil;
import com.moon.poi.excel.annotation.value.DefaultValue;
import com.moon.poi.excel.annotation.TableColumn;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

/**
 * @author moonsky
 */
class TableFactoryTestTest {

    public static class Emp {

        private String name = "张三";

        private int age = 25;

        private String address = "杭州西湖";

        public String getSchoolName() {
            return "北京大学";
        }

    }

    @Test
    @Disabled
    void testRenderBody() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<Emp> list = ListUtil.newList(new Emp(), new Emp(), new Emp());

                tableFactory.render(list);
            });
        }).write("/Users/moonsky/test1.xlsx");
    }

    public static class User {

        @TableColumn({"基本信息", "姓名"})
        private String name = "张三";

        @DefaultValue(value = "100")
        @TableColumn({"基本信息", "年龄"})
        private int age = RandomUtil.nextInt(22, 28);

        @TableColumn({"基本信息", "籍贯"})
        private String address = "杭州西湖";

        @TableColumn({"日期"})
        private Date date = new Date();

        @TableColumn("毕业院校")
        public String getSchoolName() {
            return "北京大学";
        }

        @DefaultValue("60")
        @TableColumn(value = {"成绩"})
        public String getDefaultValue() {
            return null;
        }

        @TableColumn(value = {"交通工具"})
        public String getType() {
            return null;
        }

        @TableColumn(value = {"性别"})
        public String getSex() {
            return null;
        }

    }


    @Test
    @Disabled
    void testRenderList() throws Exception {
        String str = "今天真美丽";
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<User> list = ListUtil.newList(new User(), new User(), new User());

                tableFactory.render(list);
            });
        }).write("/Users/moonsky/test1.xlsx");
    }
}