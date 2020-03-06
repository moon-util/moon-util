package com.moon.core.util.runner.core;

import com.moon.core.io.FileUtil;
import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.Console;
import com.moon.core.util.MapUtil;
import com.moon.core.util.runner.RunnerUtil;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class ParseCoreTestTest {

    int num;
    String str;
    Object data;
    Object res;
    AsRunner handler, handler1, runner;

    static AsRunner running(String str) {
        return ParseCore.parse(str, null);
    }

    @Test
    void testRunnerThree() {
        handler = running("true?'name':'age'");
        assertSame(DataStr.class, handler.getClass());
        assertEquals(handler.run(), "name");
        handler = running("false?'name':'age'");
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "age");
        handler = running("1>2?'name':'age'");
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "age");
        handler = running("1<=2?'name':'age'");
        assertTrue(handler instanceof DataStr);
        assertEquals(handler.run(), "name");
    }

    @Test
    void testRunnerThree0() {
        handler = running("{assertion: true, value1: 20, value2: 30}");
        data = handler.run();
        handler = running("assertion?value1:value2");
        res = handler.run(data);
        assertEquals(res, 20);

        handler = running("@BooleanUtil.toBooleanValue(20)?value1:value2");
        res = handler.run(data);
        assertEquals(res, 20);
    }

    @Test
    void testCompare() {
        handler = running("1>2");
        assertEquals(handler.run(), false);
        handler = running("1+2>2");
        assertEquals(handler.run(), true);
        handler = running("1>=2");
        assertEquals(handler.run(), false);
        handler = running("1+2>=2");
        assertEquals(handler.run(), true);
        handler = running("2>=2");
        assertEquals(handler.run(), true);
        handler = running("1+2>=3");
        assertEquals(handler.run(), true);
    }

    @Test
    void testRunningCaller() {
        handler = running("@Objects.hash('123')");
        assertEquals((Object) Objects.hash("123"), handler.run());
        handler = running("@StringUtil.concat(data)");
        data = new HashMap() {{
            put("data", new CharSequence[]{"123", "456"});
        }};
        assertEquals(StringUtil.concat("123", "456"), handler.run(data));
    }

    @Test
    void testGetTotalFiles() {
        String path = "D:\\WorkSpaces\\IDEA\\moonsky\\src\\Main\\java\\com\\moon";
        List<File> all = FileUtil.traverseDirectory(path);
        System.out.println("==============================================");
        System.out.println(all.size());
    }

    void testName() {
        handler = running("1 + 2 + @Objects.toString('10')");
        assertEquals(handler.run(), "310");
        ClassUtil.forName("com.moon.core.util.Appender");
        ClassUtil.forName("com.moon.core.util.Console");
        handler = running("@Console.out.getLowestLevel().name()");
        assertEquals(handler.run(), "PRINT");
        handler = running("@Console$Level.ASSERT.name()");
        assertEquals(handler.run(), "ASSERT");
    }

    @Test
    void testGetOpposite() {
        handler = ParseCore.parse("1+-1*5");
        assertEquals((Integer) handler.run(), (Object) (1 + -1 * 5));
        handler = ParseCore.parse("(1+-1)*5");
        assertEquals((Integer) handler.run(), (Object) ((1 + -1) * 5));
        handler = ParseCore.parse("-1*5");
        assertEquals((Integer) handler.run(), (Object) (-1 * 5));

        data = new HashMap() {{
            put(true, 20);
        }};
        handler = ParseCore.parse("-[true]*5");
        assertEquals((Integer) handler.run(data), (Object) (-100));
    }

    @Test
    void testParse() {
        handler = ParseCore.parse("1+1");
        assertEquals((Integer) handler.run(), (Object) (2));
        handler = ParseCore.parse("(1+1+205)");
        assertEquals((Integer) handler.run(), (Object) (207));
        handler = ParseCore.parse("2100-21*53+2255");
        num = 2100 - 21 * 53 + 2255;
        assertEquals((Integer) handler.run(), (Object) (num));
        handler = ParseCore.parse("40 * 48 - (1472 + 328) / 5");
        num = 40 * 48 - (1472 + 328) / 5;
        assertEquals((Integer) handler.run(), (Object) (num));

        str = "aaaaaaaaaaa";
        data = new HashMap() {{
            put(true, str);
            put(false, false);
            put(str, false);
            put(20, new HashMap() {{
                put("name", 53);
            }});
        }};

        handler = ParseCore.parse("[true]");
        assertEquals(handler.run(data), str);
    }

    @Test
    void testInvoker() {
        data = new HashMap() {{
            put(20, new HashMap() {{
                put("name", new ArrayList() {{
                    add(new Employee());
                    add(new Employee());
                }});
            }});
            put(true, 16);
            put("true", 20);
        }};

        handler = ParseCore.parse("[20].get('name').get(0).age.toString().length()");
        assertEquals(handler.run(data), 2);

        handler = ParseCore.parse(
            "[  20   ]  .name   .  get(0)  .  age  .  doubleValue(). toString(   ) . length   (  ) + [true]");
        assertEquals(handler.run(data), 20);

        handler = ParseCore.parse(
            "[  20   ]  .name   .  get(0)  .  age  .  doubleValue(). toString(   ) . length   (  ) + ['true']");
        assertEquals(handler.run(data), 24);

        handler = ParseCore.parse("([20].name.get(0).age.doubleValue().toString().length()+[true]).longValue()");
        assertEquals(handler.run(data), 20L);

        handler = ParseCore.parse("([20].name.get(0).age.doubleValue().toString().length()+['true'])");
        assertEquals(handler.run(data), 24);

        handler = ParseCore.parse("([20].name.get(0).age.doubleValue().toString().length()+true).toString().length()");
        assertEquals(handler.run(data), 5);
    }

    @Test
    void testCaller() {
        data = new HashMap() {{
            put("array", new Object[]{1, 2, 3, "asdfghj"});
        }};
        handler = ParseCore.parse("@System.currentTimeMillis()");
        res = handler.run();
        System.out.println(res);
        handler1 = ParseCore.parse("@DateUtil.now()");
        res = handler.run();
        System.out.println(res);
        System.out.println("===============================");
        res = handler.run();
        System.out.println(res);
        res = handler1.run();
        System.out.println(res);
        System.out.println("===============================");
    }

    @Test
    void testArrayStringify() {
        data = new HashMap() {{
            put("array", new Object[]{1, 2, 3, "asdfghj"});
        }};
        handler1 = ParseCore.parse("@ ArraysEnum . OBJECTS . stringify(['array']).toString()");
        res = handler1.run(data);

        System.out.println(RunnerUtil.run("@time()"));

    }

    @Test
    void testCalc() {
        res = ParseCore.parse("15+5/3").run();
        assertEquals(res, 15 + 5 / 3);
    }

    @Test
    void testLinker() {
        str = "1111111111111";
        String str1 = "aaaaaa";
        data = new HashMap() {{
            put(20, new HashMap() {{
                put("name", new ArrayList() {{
                    add(new Employee());
                    add(new Employee());
                }});
            }});
            put("fieldName", "age");
            put(str1, str1);
            put(true, str);
            put(false, false);
            put(str, false);
        }};

        handler = ParseCore.parse("[true]");
        assertEquals(handler.run(data), str);
        handler = ParseCore.parse("[aaaaaa]");
        assertEquals(handler.run(data), str1);
        handler = ParseCore.parse("!['1111111111111']");
        assertTrue((Boolean) handler.run(data));

        MapUtil.putToObject(data, true, "age");

        handler = ParseCore.parse("(!['1111111111111']+([20].name[1][fieldName].doubleValue() + '123')).length()");
        assertEquals(handler.run(data), 11);
        handler = ParseCore.parse("(!['1111111111111']+([20].name[1][fieldName].doubleValue() + '123'))");
        assertEquals(handler.run(data), "true20.0123");
        handler = ParseCore.parse("([20].name).isEmpty()");
        assertFalse((Boolean) handler.run(data));
        handler = ParseCore.parse("({}).isEmpty()");
        assertTrue((Boolean) handler.run(data));
        handler = ParseCore.parse("({:}).isEmpty()");
        assertTrue((Boolean) handler.run(data));
        handler = ParseCore.parse("({}).size()");
        assertEquals(handler.run(data), 0);
        handler = ParseCore.parse("({:}).size()");
        assertEquals(handler.run(data), 0);
    }

    public static class Employee {
        int age = 20;
    }

    @Test
    void testFnRunner() {
        str = "@map.get({20:25}, 20)";
        assertEquals(running(str).run(), (Object) 25);

        str = "@map.get(@map(23,24,25,26,68),68)";
        runner = running(str);
        assertEquals(runner.run(), (Object) null);

        str = "@list.size(@list(23,24,25,26,68))";
        runner = running(str);
        assertEquals(runner.run(), (Object) 5);

        str = "@System.getProperty('os.name')";
        runner = running(str);
        data = runner.run();
        System.out.println(data);
    }

    @Test
    void testParseExist() {
        str = "@System.exit(0)";
        assertThrows(Throwable.class, () -> running(str));
    }

    @Test
    void testInstance() {
        Object o = null;
        System.out.println(Object.class.isInstance(o));
    }

    @Test
    void testCalculator() {
        assertEquals(((Number) ParseCore.parse("349+644+72").run()).intValue(),
            (Object) (349 + 644 + 72));
    }
}