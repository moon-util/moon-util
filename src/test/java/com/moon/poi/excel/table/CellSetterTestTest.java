package com.moon.poi.excel.table;

import com.moon.poi.excel.annotation.style.DefinitionStyle;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
@DefinitionStyle
@DefinitionStyle
class CellSetterTestTest {

    @Test
    void testAnnotationEquals() {
        DefinitionStyle[] styles = CellSetterTestTest.class.getAnnotationsByType(DefinitionStyle.class);
        assertEquals(styles[0].hashCode(), styles[1].hashCode());
        assertEquals(styles[0], styles[1]);
    }

    @Test
    void testJodaFormat() throws Exception {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println(formatter.print(new Date().getTime()));

    }
}