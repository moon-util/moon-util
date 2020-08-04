package com.moon.poi.excel.xlsx;

import com.moon.core.enums.Arrays2;
import com.moon.core.lang.ArrayUtil;
import com.moon.core.lang.SystemUtil;
import com.moon.core.util.CollectUtil;
import com.moon.poi.excel.Renderer;
import com.moon.poi.excel.annotation.TableColumn;
import com.moon.poi.excel.annotation.style.DefinitionStyle;
import com.moon.poi.excel.table.TableParser;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
public class StyleTest extends TableParser {


    int[] testSwitch(int length) {
        List<Integer> list = new ArrayList<>();
        switch (length) {
            default:
            case 3:
                list.add(3);
            case 2:
                list.add(2);
            case 1:
                list.add(1);
            case 0:
        }
        return Arrays2.INTS.toPrimitives(CollectUtil.toArray(list, Integer[]::new));
    }

    @Test
    void testPrintUserDir() throws Exception {
        int[] values = testSwitch(3);
        assertEquals(3,values.length);
        values = testSwitch(4);
        assertEquals(3, values.length);
        values = testSwitch(5);
        assertEquals(3, values.length);
        values = testSwitch(0);
        assertEquals(0, values.length);
        values = testSwitch(2);
        assertEquals(2, values.length);
        values = testSwitch(1);
        assertEquals(1, values.length);
    }
}
