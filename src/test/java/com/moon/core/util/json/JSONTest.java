package com.moon.core.util.json;

import com.moon.core.util.Console;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author benshaoye
 */
public class JSONTest {
    Object data;

    @Test
    void testStringify() {
        data = new HashMap() {{
            put("name", "电报");
            put("name1", "电报");
            put("name2", "电报");
            put("name3", "电报");
            put("name4", "电报");
            put("name5", new ArrayList() {{
                add(1);
                add(2);
                add(3);
                add(4);
                add(null);
                add(6);
                add(7);
            }});
            put("age", 25);
            put("isMan", true);
            put("isWomen", false);
            put("desc", null);
            put("address", "用户猜测");
        }};
        Console.out.println(JSON.stringify(data));
    }
}
