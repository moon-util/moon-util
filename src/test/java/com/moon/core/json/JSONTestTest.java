package com.moon.core.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class JSONTestTest {

    @Test
    void testParse() throws Exception {
        String json = "{\"title\":\"json在线解析（简版） -JSON在线解析\",\"json.url\":\"https://www.sojson.com/simple_json.html\",\"keywords\":\"json在线解析\",\"功能\":[\"JSON美化\",\"JSON数据类型显示\",\"JSON数组显示角标\",\"高亮显示\",\"错误提示\",{\"备注\":[\"www.sojson.com\",\"json.la\"]}],\"加入我们\":{\"qq群\":\"259217951\"}}";
        JSON parsedJson = JSON.parse(json);
        System.out.println();
    }
}