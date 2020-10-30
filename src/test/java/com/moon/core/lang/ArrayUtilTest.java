package com.moon.core.lang;

import com.moon.core.io.FileUtil;
import com.moon.core.util.MapUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
public class ArrayUtilTest {

    @Test
    void testName() throws Exception {
        // ValidationUtil.requireDateString("");
        // ValidationUtil.of("string");
        String path = "D:\\WorkSpaces\\github\\javascript\\json-mock\\node_modules\\codemirror\\theme";
        FileUtil.traveller().traverse(path).forEach(name -> {
            // System.out.println(name.getName());
            // String importStr = StringUtil.format("import 'codemirror/theme/{}';", name.getName());
            String onlyName = StringUtil.substrBefore(name.getName(), ".css");
            String thisName = StringUtil.format("'{}',", onlyName);
            System.out.println(thisName);

        });
    }

    @Test
    void testSplice() {
        boolean[] arr = {false, true, false, true};
        assertEquals(JoinerUtil.join(arr, ","), "false,true,false,true");
        boolean[] newArr = ArrayUtil.splice(arr, 1, 1, true, true);
        assertEquals(JoinerUtil.join(newArr, ","), "false,true,true,false,true");

        int[] ints = {1, 2, 3, 4, 5};
        assertEquals(JoinerUtil.join(ints, ","), "1,2,3,4,5");
        int[] newInts = ArrayUtil.splice(ints, 1, 4, 11, 11, 11, 11);
        assertEquals(JoinerUtil.join(newInts, ","), "1,11,11,11,11");
        newInts = ArrayUtil.splice(ints, 1, 1, 11, 11, 11, 11);
        assertEquals(JoinerUtil.join(newInts, ","), "1,11,11,11,11,3,4,5");

        Object[] objects = {1, 2, 3, 4, 5};
        assertEquals(JoinerUtil.join(objects, ","), "1,2,3,4,5");
        Object[] newObjects = ArrayUtil.splice(objects, 1, 4, 11, 11, 11, 11);
        assertEquals(JoinerUtil.join(newObjects, ","), "1,11,11,11,11");
    }

    @Test
    void testReverse() {
        Integer[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        ArrayUtil.reverse(values);
        assertEquals(JoinerUtil.join(values, ","), "9,8,7,6,5,4,3,2,1");
        values = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8};
        ArrayUtil.reverse(values);
        assertEquals(JoinerUtil.join(values, ","), "8,7,6,5,4,3,2,1");
    }

    @Test
    void testSum() throws Exception {
        int[] ints = {};

        Map<String, Object> resultMap = ArrayUtil.reduce(20, (map, idx) -> map, MapUtil.newHashMap());
    }

    @Test
    void testToString() throws Exception {
        long time = new Date().getTime();
        System.out.println(time);
        String str = LongUtil.toString(time, 62);
        System.out.println(str);
        long parsed = LongUtil.parseLong(str, 62);
        assertEquals(time, parsed);
    }
}
