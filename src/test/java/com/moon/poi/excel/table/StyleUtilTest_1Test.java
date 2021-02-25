package com.moon.poi.excel.table;

import com.moon.core.lang.SystemUtil;
import com.moon.core.lang.reflect.FieldUtil;
import com.moon.core.lang.reflect.MethodUtil;
import com.moon.core.util.FilterUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.poi.excel.ExcelUtil;
import com.moon.poi.excel.annotation.SheetColumn;
import com.moon.poi.excel.annotation.style.DefinitionStyle;
import lombok.Data;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class StyleUtilTest_1Test extends TableParser {


    @Data
    @DefinitionStyle(classname = "employeeName", align = HorizontalAlignment.CENTER)
    public static class Employee_0 {

        private String name;

        private String address;
    }

    @Data
    @DefinitionStyle(classname = "employeeName", align = HorizontalAlignment.CENTER)
    public static class Employee_1 {

        @SheetColumn("姓名")
        private String name;

        @SheetColumn("地址")
        private String address;
    }

    @Data
    @DefinitionStyle(align = HorizontalAlignment.CENTER)
    public static class Employee_2 {

        @SheetColumn("姓名")
        private String name;

        @SheetColumn(value = "地址")
        private String address;
    }

    @Data
    @DefinitionStyle(align = HorizontalAlignment.CENTER)
    public static class Employee_3 {

        @SheetColumn("姓名")
        private String name;

        @SheetColumn(value = "地址", style = "address")
        private String address;
    }

    @Test
    void testParseStyle() throws Exception {
        TableRenderer renderer = parseOnly(Employee_0.class);
        TableCol[] cols = (TableCol[]) FieldUtil.getValue("columns", renderer, true);
        for (TableCol col : cols) {
            assertNull(FieldUtil.getValue("defaultClassname", col, true));
        }


        renderer = parseOnly(Employee_1.class);
        cols = (TableCol[]) FieldUtil.getValue("columns", renderer, true);
        for (TableCol col : cols) {
            assertNull(FieldUtil.getValue("defaultClassname", col, true));
        }


        renderer = parseOnly(Employee_2.class);
        cols = (TableCol[]) FieldUtil.getValue("columns", renderer, true);
        for (TableCol col : cols) {
            assertEquals(StyleUtil.classnameOfEmpty(Employee_2.class),
                FieldUtil.getValue("defaultClassname", col, true));
        }


        renderer = parseOnly(Employee_3.class);
        cols = (TableCol[]) FieldUtil.getValue("columns", renderer, true);
        for (TableCol col : cols) {
            String name = FieldUtil.getValue("name", col, true).toString();
            switch (name) {
                case "name":
                    assertEquals(StyleUtil.classnameOfEmpty(Employee_3.class),
                        FieldUtil.getValue("defaultClassname", col, true));
                    break;
                case "address":
                    String classname = MethodUtil.invokeStatic(true,
                        "classnameOf",
                        StyleUtil.class,
                        Employee_3.class,
                        "address").toString();
                    assertEquals(classname, FieldUtil.getValue("defaultClassname", col, true));
                    break;
                default:
            }
        }
    }


    @Data
    public static class Employee_4 {

        @DefinitionStyle(fillPattern = FillPatternType.SOLID_FOREGROUND,
            foregroundColor = 49,
            border = {BorderStyle.NONE, BorderStyle.NONE, BorderStyle.DOUBLE})
        @SheetColumn("姓名")
        private String name = RandomStringUtil.nextChinese(3);

        @SheetColumn(value = "地址", style = "address")
        private String address = RandomStringUtil.nextChinese(3);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testExportOnWin() throws Exception {

    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testExportOnMac() throws Exception {
        doExport(SystemUtil.getUserHome());
    }

    void doExport(String dir) {
        ExcelUtil.xlsx().sheet(sheetFactory -> {
            sheetFactory.table(tableFactory -> {
                List<Employee_4> list = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    list.add(new Employee_4());
                }
                tableFactory.render(list);
            });
        }).write(new File(dir, "export-score.xlsx"));
    }

    @Test
    void testGetTableColClassname() throws Exception {
        TableRenderer renderer = parseOnly(Employee_4.class);
        TableCol[] cols = (TableCol[]) FieldUtil.getValue("columns", renderer, true);
        TableCol nameCol = FilterUtil.nullableFind(cols, col -> {
            return FieldUtil.getValue("name", col, true).equals("name");
        });
        Field nameField = FieldUtil.getDeclaredField(Employee_4.class, "name");
        Method method = MethodUtil.getDeclaredMethod(StyleUtil.class, "scoped", Object.class);
        assertNotNull(method);
    }
}