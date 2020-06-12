package com.moon.more.excel;

import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class TableFactoryTestTest {

    public static class Employee {

        private String name = "张三";

        private int age = 25;

        private String address = "杭州西湖";

    }

    @Test
    void testRenderBody() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<Employee> list = ListUtil.newArrayList(new Employee(), new Employee(), new Employee());

                tableFactory.renderBody(list);
            });
        }).write2Filepath("/Users/moonsky/test1.xlsx");
    }
}