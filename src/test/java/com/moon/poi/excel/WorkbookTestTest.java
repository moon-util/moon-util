package com.moon.poi.excel;

import com.moon.poi.excel.annotation.SheetColumn;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author moonsky
 */
class WorkbookTestTest {

    @Data
    public static class EmployeeEntity {

        @SheetColumn({"姓名"})
        private String name;
        @SheetColumn({"年龄"})
        private int age;
        @SheetColumn({"班级"})
        private String classname;

        public EmployeeEntity() {
            this.name = "张三";
            this.age = 28;
            this.classname = "五年舞伴";
        }
    }

    @Test
    void testExportExcel() throws Exception {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<EmployeeEntity> entities = new ArrayList<>();
                entities.add(new EmployeeEntity());
                entities.add(new EmployeeEntity());
                entities.add(new EmployeeEntity());
                entities.add(new EmployeeEntity());

                tableFactory.render(entities);
            });
        }).write("D:/employee.xlsx");
    }
}