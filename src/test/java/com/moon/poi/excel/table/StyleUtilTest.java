package com.moon.poi.excel.table;

import com.moon.core.lang.IntUtil;
import com.moon.core.lang.reflect.FieldUtil;
import com.moon.poi.excel.Renderer;
import com.moon.poi.excel.annotation.SheetColumn;
import com.moon.poi.excel.annotation.SheetColumnGroup;
import com.moon.poi.excel.annotation.RowRecord;
import com.moon.poi.excel.annotation.style.DefinitionStyle;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author benshaoye
 */
@DefinitionStyle(classname = "definitionOnStyleUtilNo1")
@DefinitionStyle(classname = "definitionOnStyleUtilNo2")
public class StyleUtilTest extends TableParser {

    @DefinitionStyle(classname = "notDefStyle")
    @DefinitionStyle()
    private String notDefStyle;

    @Test
    void testGetTableColClassname() throws Exception {
    }

    @RowRecord(importStyles = {StyleUtilTest.class})
    @DefinitionStyle(classname = "defaultStyleOnDeptNo1")
    @DefinitionStyle(classname = "defaultStyleOnDeptNo2")
    public static class Department {

        @SheetColumn
        @DefinitionStyle
        private String name;

        @SheetColumn
        @DefinitionStyle
        private String address;

        @SheetColumn
        private int age;

        @SheetColumn
        private boolean male;

        @SheetColumn
        @SheetColumnGroup
        private Detail detail;
    }

    @DefinitionStyle(classname = "defaultName")
    public static class Detail {

        @SheetColumn
        @DefinitionStyle
        private String name;

        @SheetColumn
        @DefinitionStyle
        private String address;

        @SheetColumn
        private int age;

        @SheetColumn
        private boolean male;
    }

    @Test
    void testGetParsedAnnotations() throws Exception {
        Renderer renderer = parseOnly(Department.class);
        System.out.println();
    }

    @DefinitionStyle
    private String name;
    @DefinitionStyle
    @DefinitionStyle
    private String age;

    @Test
    void testGetAnnotations() throws Exception {
        Class type = getClass();
        Field field = FieldUtil.getDeclaredField(type, "name");
        DefinitionStyle style = field.getAnnotation(DefinitionStyle.class);
        assertNotNull(style);
        DefinitionStyle[] styles = field.getAnnotationsByType(DefinitionStyle.class);
        assertEquals(styles.length, 1);
        field = FieldUtil.getDeclaredField(type, "age");
        styles = field.getAnnotationsByType(DefinitionStyle.class);
        assertEquals(styles.length, 2);
    }

    public StyleUtilTest() {
    }

    @Test
    void testCollectStyleMap() throws Exception {
        int hash = System.identityHashCode(getClass());
        System.out.println(hash);
        System.out.println(IntUtil.toString(hash, 36));
        System.out.println(IntUtil.toString(hash, 62));

        DefinitionStyle[] styles = getClass().getAnnotationsByType(DefinitionStyle.class);
        assertNotNull(styles);
    }
}
