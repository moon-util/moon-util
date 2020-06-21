package com.moon.more.excel.table;

import com.moon.more.excel.annotation.style.DefinitionStyle;
import org.junit.jupiter.api.Test;

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
}