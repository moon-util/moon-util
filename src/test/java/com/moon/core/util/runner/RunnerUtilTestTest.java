package com.moon.core.util.runner;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.DoubleUtil;
import com.moon.core.lang.reflect.MethodUtil;
import com.moon.core.script.ScriptUtil;
import com.moon.core.util.Console;
import com.moon.core.util.DateUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.MapUtil;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class RunnerUtilTestTest {
    Object data, res;
    Runner runner, runner1;

    @Test
    void testFunc() {
        runner = RunnerUtil.parse("@now()");
        runner1 = RunnerUtil.parse("@System.currentTimeMillis()");

        System.out.println(data = runner.run());
        System.out.println(data = runner1.run());
        System.out.println(data = System.currentTimeMillis());
        for (int i = 0; i < 1000000; i++) {
            runner.run();
            runner1.run();
            System.currentTimeMillis();
        }
        final int count = 20000;// 2000000000;
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            runner.run();
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            runner1.run();
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            System.currentTimeMillis();
        }
        // Console.out.timeEnd();
    }

    @Test
    void testFunction() {
        runner = RunnerUtil.parse("@str.indexOf('123', '2')");
        assertEquals(runner.run(), Integer.valueOf("123".indexOf("2")));
        for (int i = 0; i < 10000; i++) {
            runner.run();
            "123".indexOf("2");
        }
        final int count = 2000000000;
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            runner.run();
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            "123".indexOf("2");
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            "123".indexOf("2");
        }
        // Console.out.timeEnd();
    }

    @Test
    void testMapAndList() {
        res = RunnerUtil.run("{:}.get(true)");
        assertEquals(res, null);
        res = RunnerUtil.run("{}.isEmpty()");
        assertEquals(res, true);
        res = RunnerUtil.run("{:}.isEmpty()");
        assertEquals(res, true);

        res = RunnerUtil.run("{a:10}.isEmpty()");
        assertEquals(res, false);

        assertThrows(Throwable.class, () -> RunnerUtil.run("{a:}.isEmpty()"));
        assertThrows(Throwable.class, () -> RunnerUtil.run("{a:,}.isEmpty()"));
        assertThrows(Throwable.class, () -> RunnerUtil.run("{a}.isEmpty()"));

        res = RunnerUtil.run("{a:10}.isEmpty() + false");
        assertEquals(res, "falsefalse");

        res = RunnerUtil.run("!{:}.isEmpty()");
        assertEquals(res, false);

        res = RunnerUtil.run("!{}.isEmpty()");
        assertEquals(res, false);

        res = RunnerUtil.run("!({:}.isEmpty())");
        assertEquals(res, false);

        res = RunnerUtil.run("!({}.isEmpty())");
        assertEquals(res, false);

        res = RunnerUtil.run("!({:}).isEmpty()");
        assertEquals(res, false);

        res = RunnerUtil.run("!({}).isEmpty()");
        assertEquals(res, false);

        res = RunnerUtil.run("-20.doubleValue()");
        assertEquals(res, -20D);

        res = RunnerUtil.run("-20.doubleValue().intValue()");
        assertEquals(res, -20);

        res = RunnerUtil.run("-@DateUtil.now().intValue()");
        assertTrue(res instanceof Integer);
    }

    @Test
    void testStaticCaller() {
        runner = RunnerUtil.parse("@DateUtil.now()");

    }

    @Test
    void testRun() {
        res = RunnerUtil.run("1+1.doubleValue()");
        assertEquals(res, 2D);
        res = RunnerUtil.run("''.length()");
        assertEquals(res, 0);
        res = RunnerUtil.run("1+1.doubleValue()");
        assertEquals(res, 2D);
        res = RunnerUtil.run("'a'.length()");
        assertEquals(res, 1);
        res = RunnerUtil.run("@DateUtil.yyyy_MM+20");
        assertEquals(res, "yyyy-MM20");
        res = RunnerUtil.run("@   DateUtil.yyyy_MM+20");
        assertEquals(res, "yyyy-MM20");
    }


    @Test
    void testCalc() {
        assertEquals(RunnerUtil.run("1^1"), 1 ^ 1);
        assertEquals(RunnerUtil.run("2^1+5"), 2 ^ 1 + 5);
    }

    @Test
    void testParseRun() {
        assertEquals(RunnerUtil.parseRun("{{1+2}}"), 3);
        assertEquals(RunnerUtil.parseRun("{{'中华人民共和国'}}"), "中华人民共和国");

        data = new HashMap() {{
            put("name", 456);
        }};

        str = "本草纲目{{'好的'}}  {{   123   }}  电脑 {{1+2+3+5+6}} {{name}}";
        assertEquals(
            RunnerUtil.parseRun(str, data),
            "本草纲目好的  123  电脑 17 456"
        );

        str = "本草纲目{{'好的'}}  {{123}}  ";
        assertEquals(
            RunnerUtil.parseRun(str, data),
            "本草纲目好的  123  "
        );
    }

    @Test
    void testRunMulti() {
        Map m1 = new HashMap() {{
            put("name", 1);
        }};
        Map m2 = new HashMap() {{
            put("name", 2);
        }};

        runner = RunnerUtil.parse("name");
        data = runner.runMulti(m1, m2);
        assertEquals(data, 2);
    }

    @Test
    void testParseRunPerformance() {
        int count = 100000;
        long begin = DateUtil.now();
        for (int i = 0; i < count; i++) {
            RunnerUtil.parseRun("{{1+2}}");
        }
        long end = DateUtil.now();
        // Console.out.println(end - begin);
    }

    String str, result;
    String[] delimiters = new String[]{"${", "}}"};

    @Test
    void testParseRun1() {
        data = new HashMap() {{
            put("name", 456);
        }};

        str = "本草纲目${'好的'}}  ${123}}  电脑 ${1+2+3+5+6}} ${name}}";
        assertEquals(
            RunnerUtil.parseRun(str, delimiters, data),
            "本草纲目好的  123  电脑 17 456"
        );

        str = "本草纲目${'好的'}}  ${123}}  ";
        assertEquals(
            RunnerUtil.parseRun(str, delimiters, data),
            "本草纲目好的  123  "
        );
    }

    @Test
    void testParseRun2() {
        res = RunnerUtil.run("@Long.  parseLong(   @Objects.toString(   @DateUtil.now(   )   )  )");
        res = RunnerUtil.run("-(-@Long.parseLong(   @   Objects  . toString( @    DateUtil .  now(   ))  )   ).longValue()");
        assertTrue(res instanceof Long);
        res = RunnerUtil.run("@MapUtil.sizeByObject({key: null, null: true})");
        assertTrue(res instanceof Integer);
        assertEquals(res, 2);
        res = RunnerUtil.run("{key: null, null: true, 25.3: 25}");
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.sizeByObject(res), 3);
        assertEquals(MapUtil.getByObject(res, "key"), null);
        assertEquals(MapUtil.getByObject(res, null), true);
        assertEquals(MapUtil.getByObject(res, null), true);
        assertEquals(MapUtil.getByObject(res, "null"), null);
        assertEquals(MapUtil.getByObject(res, 25.3), 25);
    }

    @Test
    void testGetMethod() {
        res = MethodUtil.getPublicStaticMethods(Objects.class, "hash");
        // Console.out.println((Object) ListUtil.getByObject(res, 0));
    }

    @Test
    void testParseMultipleParamMethod() {
        res = ClassUtil.getClasses(1, 2.0);
        res = MethodUtil.getPublicStaticMethods(DoubleUtil.class, "requireGt", (Class[]) res);
        //res = RunnerUtil.apply("@DateUtil.parse('2018-05-09 12:35:26', 'yyyy-MM-dd HH:mm:ss')");

        assertTrue(String.class.getPackage() == Class.class.getPackage());
        data = new HashMap() {{
            put("arr", new Object[]{1, 2, 3});
        }};
        res = RunnerUtil.run("arr.length", data);
        System.out.println(res);
    }

    @Test
    void testRunningTimes() throws ScriptException {
        ScriptEngine engine = ScriptUtil.newJSEngine();
        System.out.println(engine.eval("1+1"));
        System.out.println(RunnerUtil.run("1+1"));
        Runner runner = RunnerUtil.parse("1+1");

        int count = 10000;
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            res = engine.eval("1+1");
        }
        // Console.out.timeNext();
        for (int i = 0; i < count; i++) {
            res = RunnerUtil.run("1+1");
        }
        // Console.out.timeNext();
        for (int i = 0; i < count; i++) {
            res = runner.run();
        }
        // Console.out.timeNext();
        for (int i = 0; i < count; i++) {
            res = 1 + 1;
        }
        // Console.out.timeEnd();
    }

    @Test
    void testParseToRunner() {
        Runner runner = RunnerUtil.parse("{'已有人数','需要人数','已提交','已付款','已完成'}");
        res = runner.run();
        System.out.println(res);
    }

    @Test
    void testCreateList() {
        str = "{'1111','2222','3333','4444'}";
        runner = RunnerUtil.parse(str);

        data = runner.run();
        System.out.println(data);
        System.out.println(createList());
        System.out.println("===============================");
        final int count = 1000;
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            createList();
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            runner.run();
        }
        // Console.out.timeEnd();
    }

    List<String> createList() {
        List<String> list = new ArrayList<>();

        list.add("1111");
        list.add("2222");
        list.add("3333");
        list.add("4444");

        return list;
    }

    @Test
    void testCustomCaller() {
        RunnerSettings settings = RunnerSettings.of()
            .setObjCreator(LinkedHashMap::new)
            .setArrCreator(LinkedList::new)
            .addCaller("call", Caller.class)
            .addCaller("Objects", InnerObjects.class);

        Runner runner = RunnerUtil.parse("@call.get()", settings);
        assertEquals(runner.run(), "123456789");
        runner = RunnerUtil.parse("@Objects.toString('123123')", settings);
        assertEquals(runner.run(), "--11--");
        runner = RunnerUtil.parse("@Objects.toString('123123')");
        assertEquals(runner.run(), "123123");
        runner = RunnerUtil.parse("{'已有人数','需要人数','已提交','已付款','已完成'}", settings);
        assertTrue(runner.run() instanceof LinkedList);
        assertEquals(ListUtil.sizeByObject(runner.run()), 5);
    }

    @Test
    void testJEP() {
        str = "((a+b)*(c+b))/(c+a)/b";
        data = new HashMap() {{
            put("a", 10);
            put("b", 20);
            put("c", 30);
        }};
        int a = 10, b = 20, c = 30;

        Runner runner = RunnerUtil.parse(str);
        res = runner.run(data);
        assertEquals(res, ((a + b) * (c + b)) / (c + a) / b);

        str = "43*(2 + 1.4)+2*32/(3-2.1)";
        assertEquals(RunnerUtil.run(str), 43 * (2 + 1.4) + 2 * 32 / (3 - 2.1));

        str = "1 >> 1";
        runner = RunnerUtil.parse(str);
        res = runner.run(data);
        assertEquals(res, 1 >> 1);
        assertEquals(RunnerUtil.run("1<<1"), 1 << 1);
        assertEquals(RunnerUtil.run("1>>1"), 1 >> 1);
        assertEquals(RunnerUtil.run("1>>>1"), 1 >>> 1);
        assertEquals(RunnerUtil.run("2>>>2"), 2 >>> 2);
        assertEquals(RunnerUtil.run("3>>>5"), 3 >>> 5);
        assertEquals(RunnerUtil.run("8>>>2"), 8 >>> 2);
        assertEquals(RunnerUtil.run("1==1"), 1 == 1);
        assertEquals(RunnerUtil.run("1!=1"), 1 != 1);
        assertEquals(RunnerUtil.run("1|1"), 1 | 1);
        assertEquals(RunnerUtil.run("1&1"), 1 & 1);
        assertEquals(RunnerUtil.run("1^1"), 1 ^ 1);
        assertEquals(RunnerUtil.run("3>4?1:2"), 3 > 4 ? 1 : 2);
        assertEquals(RunnerUtil.run("9+44*(8-2/1)"), 9 + 44 * (8 - 2 / 1));

        data = new HashMap() {{
            put("money", 2640);
            put("count", 50);
            put("people", 25);
            put("cat", 1);
        }};
        runner = RunnerUtil.parse("((money+count)*people/100)+50-88+cat*10");
        int money = 2640, count = 50, people = 25, cat = 1, x = 2;
        assertEquals(runner.run(data), (Object)(((money + count) * people / 100) + 50 - 88 + cat * 10));

        data = new HashMap() {{
            put("value", 7);
            put("st", "test");
            put("state", "正常");
            put("flag", true);
        }};
        int value = 7;
        String st = "test", state = "正常";
        boolean flag = true;
        runner = RunnerUtil.parse("value > 5 && st == \"test\" && state == \"正常\" && flag == true");
        assertEquals(runner.run(data), value > 5 && Objects.equals("test", st) && Objects.equals("正常", state) && flag == true);
        runner = RunnerUtil.parse("value > 5 && st =='test' && state == '正常' && flag == true");
        assertEquals(runner.run(data), value > 5 && Objects.equals("test", st) && Objects.equals("正常", state) && flag == true);
        runner = RunnerUtil.parse("1 + 2 * (4 - 3) / 2");
        assertEquals(runner.run(), (Object)(1 + 2 * (4 - 3) / 2));
        runner = RunnerUtil.parse("(10 + 20) * 3 / 5 - 6");
        assertEquals(runner.run(), (Object)((10 + 20) * 3 / 5 - 6));
        runner = RunnerUtil.parse("110 + 2 * (40 - 3) / 2");
        assertEquals(runner.run(), (Object)(110 + 2 * (40 - 3) / 2));
        runner = RunnerUtil.parse("3+(2-5)*6/3 ");
        assertEquals(runner.run(), (Object)(3 + (2 - 5) * 6 / 3));
        runner = RunnerUtil.parse("5 * ( 4.1 + 2 -6 /(8-2))");
        assertEquals(runner.run(), 5 * (4.1 + 2 - 6 / (8 - 2)));
        runner = RunnerUtil.parse("5 * ( 4.1 + 2.9 )");
        assertEquals(runner.run(), 5 * (4.1 + 2.9));
        runner = RunnerUtil.parse("14/3*2");
        assertEquals(runner.run(), (Object)(14 / 3 * 2));

    }

    @Test
    void testPerformance() {
        str = "1000+100.0*99-(600-3*15)/(((68-9)-3)*2-100)+10000%7*71";
        res = 1000 + 100.0 * 99 - (600 - 3 * 15) / (((68 - 9) - 3) * 2 - 100) + 10000 % 7 * 71;

        runner = RunnerUtil.parse(str);
        assertEquals(runner.run(), res);

        runner = RunnerUtil.parse(str);
        int count = 10000000;
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            res = runner.run();
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            res = 1000 + 100.0 * 99 - (600 - 3 * 15) / (((68 - 9) - 3) * 2 - 100) + 10000 % 7 * 71;
        }
        // Console.out.timeEnd();

        // ---------------------------------------------------------------------------------------
    }

    @Test
    void testPerformance1() {
        str = "6.7 - 100 > 39.6 ? 5 == 5 ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3";
        runner = RunnerUtil.parse(str);

        res = 6.7 - 100 > 39.6 ? 5 == 5 ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3;
        assertEquals(runner.run(), res);

        str = "6.7 - 100 > 39.6 ? (5 == 5 ? 4 + 5 : 6 - 1) : (!(100 % 3 - 39.0 < 27) ? (8 * 2 - 199) : 100 % 3)";
        runner = RunnerUtil.parse(str);
        res = 6.7 - 100 > 39.6 ? (5 == 5 ? 4 + 5 : 6 - 1) : (!(100 % 3 - 39.0 < 27) ? (8 * 2 - 199) : 100 % 3);
        assertEquals(runner.run(), res);

        final Runner er = runner;
        int count = 10000000;
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            res = 6.7 - 100 > 39.6 ? 5 == 5 ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3;
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int i = 0; i < count; i++) {
            res = er.run();
        }
        // Console.out.timeEnd();
    }

    @Test
    void testRandom() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        System.out.println(random.nextDouble());
        System.out.println(random.nextDouble(20));
        System.out.println(random.nextDouble(10, 20));
    }

    Map vars = new HashMap() {{
        put("i", 100);
        put("pi", 3.14f);
        put("d", -3.9);
        put("b", (byte) 4);
        put("bool", false);
    }};

    @Test
    void testPerformance2() {
        data = vars;
        str = "i * @Math.PI + (d * b - 199) / (1 - d * @Math.PI) - (2 + 100 - i / @Math.PI) % 99 ==i * @Math.PI + (d * b - 199) / (1 - d * @Math.PI) - (2 + 100 - i / @Math.PI) % 99";
        runner = RunnerUtil.parse(str);

        int i = 100, b = 4;
        double d = -3.9;
        res = i * Math.PI + (d * b - 199) / (1 - d * Math.PI) - (2 + 100 - i / Math.PI) % 99 == i * Math.PI + (d * b - 199) / (1 - d * Math.PI) - (2 + 100 - i / Math.PI) % 99;
        assertEquals(res, runner.run(data));

        final Runner er = runner;
        int count = 1000;
        // Console.out.time();
        for (int j = 0; j < count; j++) {
            res = 6.7 - 100 > 39.6 ? 5 == 5 ? 4 + 5 : 6 - 1 : !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3;
        }
        // Console.out.timeEnd();
        // Console.out.time();
        for (int j = 0; j < count; j++) {
            res = er.run(data);
        }
        // Console.out.timeEnd();
    }

    @Test
    void testPerformance6() {

        str = "i * @Math.PI + (d * b - 199) / (1 - d * @Math.PI) - (2 + 100 - i / @Math.PI) % 99 ==i * @Math.PI + (d * b - 199) / (1 - d * @Math.PI) - (2 + 100 - i / @Math.PI) % 99";
        runner = RunnerUtil.parse(str);
        res = runner.run(vars);

        System.out.println(res);

        int count = 100000;
        // Console.out.time();
        for (int c = 0; c < count; c++) {
            runner.run(vars);
        }
        // Console.out.timeEnd();
    }

    @Test
    void testParse0() {
        str = "1+5+i";
        runner = RunnerUtil.parse(str);
        System.out.println();
    }

    public static class Caller {
        public static final String get() {
            return "123456789";
        }
    }

    public static class InnerObjects {
        public static final String toString(Object o) {
            return "--11--";
        }
    }
}