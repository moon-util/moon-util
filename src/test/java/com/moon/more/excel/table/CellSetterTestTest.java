package com.moon.more.excel.table;

import com.moon.more.excel.annotation.style.DefinitionStyle;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
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