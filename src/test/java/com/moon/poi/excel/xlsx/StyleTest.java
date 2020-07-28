package com.moon.poi.excel.xlsx;

import com.moon.poi.excel.Renderer;
import com.moon.poi.excel.annotation.style.DefinitionStyle;
import com.moon.poi.excel.table.TableParser;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
public class StyleTest extends TableParser{

    @Data
    @DefinitionStyle(classname = "employeeName", align = HorizontalAlignment.CENTER)
    public static class Employee {

        private String name;

        private String address;
    }

    @Test
    void testParseStyle() throws Exception {
        Renderer renderer = parseConfiguration(Employee.class);
    }
}
