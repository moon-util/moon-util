package com.moon.runner.core;

import com.moon.core.util.MapUtil;
import com.moon.runner.RunnerUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class ParseDelimitersTestTest {

    static String[] DELIMITERS = {"{{", "}}"};

    static AsRunner running(String expression) {
        return running(expression, DELIMITERS);
    }

    static AsRunner running(String expression, String[] delimiters) {
        return ParseDelimiters.parse(expression, delimiters);
    }

    Object data;
    String str, str1;
    String[] delimiters;
    AsRunner handler, handler1, runner, runner1;

    @Test
    void testParse() {
        str = "本草纲目";
        handler = running(str);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), str);

        str = "本草纲目{{'好的'}}";
        handler = running(str);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的");

        str = "本草纲目{{'好的'}}  {{123}}";
        handler = running(str);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的  123");

        str = "本草纲目{{'好的'}}  {{123}}  ";
        handler = running(str);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的  123  ");

        str = "本草纲目{{'好的'}}  {{123}}  电脑 {{1+2+3+5+6}}";
        handler = running(str);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的  123  电脑 17");
    }

    @Test
    void testParseGetter() {
        data = new HashMap() {{
            put("name", 456);
        }};
        str = "本草纲目{{'好的'}}  {{123}}  电脑 {{1+2+3+5+6}} {{name}}";
        str1 = "本草纲目{{'好的'}}  {{123}}{{['true']}}  电脑 {{1+2+3+5+6}} {{name}}";
        handler = running(str);
        handler1 = running(str1);
        assertTrue(handler instanceof AsGetter);
        assertEquals(handler.run(data), "本草纲目好的  123  电脑 17 456");

        MapUtil.putToObjectMap(data, "name", "就问你嗨不嗨");
        MapUtil.putToObjectMap(data, "true", "你是不是傻");

        assertEquals(handler.run(data), "本草纲目好的  123  电脑 17 就问你嗨不嗨");
        assertEquals(handler1.run(data), "本草纲目好的  123你是不是傻  电脑 17 就问你嗨不嗨");
    }

    @Test
    void testParse1() {
        delimiters = new String[]{"${", "}}"};
        str = "本草纲目";
        handler = running(str, delimiters);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), str);

        str = "本草纲目${'好的'}}";
        handler = running(str, delimiters);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的");

        str = "本草纲目${'好的'}}  ${123}}";
        handler = running(str, delimiters);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的  123");

        str = "本草纲目${'好的'}}  ${123}}  ";
        handler = running(str, delimiters);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的  123  ");

        str = "本草纲目${'好的'}}  ${123}}  电脑 ${1+2+3+5+6}}";
        handler = running(str, delimiters);
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "本草纲目好的  123  电脑 17");
    }

    @Test
    void testParseGetter1() {
        delimiters = new String[]{"${", "}}"};
        data = new HashMap() {{
            put("name", 456);
        }};
        str = "本草纲目${'好的'}}  ${123}}  电脑 ${1+2+3+5+6}} ${name}}";
        str1 = "本草纲目${'好的'}}  ${123}}${['true']}}  电脑 ${1+2+3+5+6}} ${name}}";
        handler = running(str, delimiters);
        handler1 = running(str1, delimiters);
        assertTrue(handler instanceof AsGetter);
        assertEquals(handler.run(data), "本草纲目好的  123  电脑 17 456");

        MapUtil.putToObjectMap(data, "name", "就问你嗨不嗨");
        MapUtil.putToObjectMap(data, "true", "你是不是傻");
        MapUtil.putToObjectMap(data, true, "回长沙");

        assertEquals(handler.run(data), "本草纲目好的  123  电脑 17 就问你嗨不嗨");
        assertEquals(handler1.run(data), "本草纲目好的  123你是不是傻  电脑 17 就问你嗨不嗨");

        str1 = "本草纲目${'好的'}}  ${123}}${['true']}} ${[true]+true}}  电脑 ${1+2+3+5+6}} ${name}}";
        handler1 = running(str1, delimiters);
        assertEquals(handler1.run(data), "本草纲目好的  123你是不是傻 回长沙true  电脑 17 就问你嗨不嗨");

        str1 = "本草纲目${'好的'}}  ${123}}${['true']}} ${[true]+true+name}}  电脑 ${1+2+3+5+6}} ${name}}";
        handler1 = running(str1, delimiters);
        str = "本草纲目好的  123你是不是傻 回长沙true就问你嗨不嗨  电脑 17 就问你嗨不嗨";
        assertEquals(handler1.run(data), str);
    }

    @Test
    void testDelimiters() {
        delimiters = new String[]{"$[", "]"};
        data = RunnerUtil.run("{address:'北京',time:5}");
        str = "今天我要去 $[address] 上班，下午 $[time] 点下班";
        runner = running(str, delimiters);
        assertEquals(runner.run(data), "今天我要去 北京 上班，下午 5 点下班");

        System.out.println(RunnerUtil.run("{1,2,3,4,5,}.add(1)"));
    }
}