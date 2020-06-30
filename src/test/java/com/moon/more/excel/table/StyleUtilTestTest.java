package com.moon.more.excel.table;

import com.moon.more.excel.annotation.style.DefinitionStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author moonsky
 */
class StyleUtilTestTest {

    public static class NoneAnnotation {}

    @DefinitionStyle
    public static class SingletonAnnotation {}

    @DefinitionStyle
    @DefinitionStyle
    public static class RepeatingAnnotation {}

    @Test
    void testParseScopeStyle() throws Exception {
        DefinitionStyle[] styles0 = NoneAnnotation.class.getAnnotationsByType(DefinitionStyle.class);
        DefinitionStyle[] styles1 = SingletonAnnotation.class.getAnnotationsByType(DefinitionStyle.class);
        DefinitionStyle[] styles2 = RepeatingAnnotation.class.getAnnotationsByType(DefinitionStyle.class);
        assertNotNull(styles0);
        assertNotNull(styles1);
        assertNotNull(styles2);
        assertEquals(0, styles0.length);
        assertEquals(1, styles1.length);
        assertEquals(2, styles2.length);
    }
}