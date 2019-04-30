package com.moon.core.util.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.Console;
import com.moon.core.util.ListUtil;
import com.moon.core.util.MapUtil;
import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
class ParseCurlyTestTest {

    static final Requires REQUIRES = Requires.of();

    static AsRunner running0(String expression) {
        char[] chars = expression.toCharArray();
        IntAccessor indexer = IntAccessor.of();
        int length = chars.length;
        ParseUtil.nextVal(chars, indexer, length);
        return ParseCurly.parse(chars, indexer, length, null);
    }

    String str;
    AsRunner handler;
    Object res, data;

    @Test
    void testParseSimpleList() {
        str = "  {  ,  ,}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 2);

        str = "  {  ,  (),,(null),,}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 5);

        str = "  {  'name' ,'age'  ,}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 2);
    }

    @Test
    void testParse0() {
        doEmpty();
        doList();
        doMap();
        doVarMap();
        doVarList();

        REQUIRES.requireThrows(() -> running0("{key: 'value', : (50 + 60   ), true: @DateUtil.now()+0}"));
    }

    void doVarList() {
        data = running0("{key: 'value', null: (50 + 60   ), true: @DateUtil.now()+0}").run();

        str = "{key, key,true,[true],[null]}";
        handler = running0(str);
        REQUIRES.requireThrows(() -> handler.run());
        res = handler.run(data);
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEquals(ListUtil.getByObject(res, 0), "value");
        REQUIRES.requireEquals(ListUtil.getByObject(res, 1), "value");
        REQUIRES.requireEquals(ListUtil.getByObject(res, 2), true);
        REQUIRES.requireInstanceOf(ListUtil.getByObject(res, 3), Double.class);
        REQUIRES.requireEquals(ListUtil.getByObject(res, 4), 110);
    }

    void doVarMap() {
        data = running0("{key: 'value', null: (50 + 60   ), true: @DateUtil.now()+0}").run();

        str = "{key: key}";
        handler = running0(str);
        REQUIRES.requireThrows(() -> handler.run());
        res = handler.run(data);
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEquals(MapUtil.getByObject(res, "key"), "value");

        str = "{key: 'value', null: [null]}";
        handler = running0(str);
        REQUIRES.requireThrows(() -> handler.run());
        res = handler.run(data);
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEquals(MapUtil.getByObject(res, "key"), "value");
        REQUIRES.requireEquals(MapUtil.getByObject(res, null), 110);
        REQUIRES.requireInstanceOf(MapUtil.getByObject(data, true), Double.class);
        Console.out.println(((Number) MapUtil.getByObject(data, true)).longValue());
    }

    void doMap() {
        str = "{key: 'value'}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEq(MapUtil.sizeByObject(res), 1);
        REQUIRES.requireEquals(MapUtil.getByObject(res, "key"), "value");

        str = "{key: 'value',}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEq(MapUtil.sizeByObject(res), 1);
        REQUIRES.requireEquals(MapUtil.getByObject(res, "key"), "value");

        str = "{key: 'value', null: 20}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEq(MapUtil.sizeByObject(res), 2);
        REQUIRES.requireEquals(MapUtil.getByObject(res, "key"), "value");
        REQUIRES.requireEquals(MapUtil.getByObject(res, null), 20);

        str = "{key: 'value', null: 20, true: '100asdfasdf'}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEq(MapUtil.sizeByObject(res), 3);
        REQUIRES.requireEquals(MapUtil.getByObject(res, "key"), "value");
        REQUIRES.requireEquals(MapUtil.getByObject(res, null), 20);
        REQUIRES.requireEquals(MapUtil.getByObject(res, true), "100asdfasdf");

        str = "{key: 'value', null: 20, true: '100asdfasdf'  ,   false  : \"sdfasdfgawfasdfasdf\"}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEq(MapUtil.sizeByObject(res), 4);
        REQUIRES.requireEquals(MapUtil.getByObject(res, "key"), "value");
        REQUIRES.requireEquals(MapUtil.getByObject(res, null), 20);
        REQUIRES.requireEquals(MapUtil.getByObject(res, true), "100asdfasdf");
        REQUIRES.requireEquals(MapUtil.getByObject(res, false), "sdfasdfgawfasdfasdf");

        str = "{key: 'value', null: 20, true: '100asdfasdf'  ,  false  : \"sdfasdfgawfasdfasdf\", 20: 150}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEquals(MapUtil.getByObject(res, 20), 150);

        str = "{key: 'value', null: 20, true: '100asdfasdf',  1503.2: 110}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEquals(MapUtil.getByObject(res, 1503.2), 110);

        str = "{key: 'value', null: 20, true: '100asdfasdf',,  1503.2: 110}";
        REQUIRES.requireThrows(() -> handler = running0(str));

        str = "{key: 'value', null:: 20, true: '100asdfasdf',,  1503.2: 110}";
        REQUIRES.requireThrows(() -> handler = running0(str));

        str = "{key: 'value', null: 20, true: '100asdfasdf'   ,    ,  1503.2: 110}";
        REQUIRES.requireThrows(() -> handler = running0(str));

        str = "{key: 'value', null    :    : 20, true: '100asdfasdf',,  1503.2: 110}";
        REQUIRES.requireThrows(() -> handler = running0(str));

        str = "{key: 'value', null , null   :    : 20, true: '100asdfasdf',,  1503.2: 110}";
        REQUIRES.requireThrows(() -> handler = running0(str));

        str = "{key: 'value', null   : null,    : 20, true: '100asdfasdf',,  1503.2: 110}";
        REQUIRES.requireThrows(() -> handler = running0(str));

        str = "{key: 'value', null: 20, true: '100asdfasdf',  1503.2: (110)}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEquals(MapUtil.getByObject(res, 1503.2), 110);

        str = "{key: 'value', null: 20+@DateUtil.now(), true: '100asdfasdf'+@DateUtil.now(),  1503.2: (110)}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEquals(MapUtil.getByObject(res, 1503.2), 110);
        REQUIRES.requireInstanceOf(MapUtil.getByObject(res, true), String.class);
        REQUIRES.requireInstanceOf(MapUtil.getByObject(res, null), Double.class);

        str = "{key: 'value', null   : null,  '  : 20, true: '100asdfasdf',  1503.2: 110}";
        REQUIRES.requireThrows(() -> handler = running0(str));
    }

    void doList() {

        str = "{,}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 1);

        str = "  {  ,  }  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 1);

        str = "  {  ,  ,}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 2);

        str = "  {  ,  ,,,,}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 5);

        str = "  {  , null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 7);

        int index = 0;
        REQUIRES.requireEquals(ListUtil.getByObject(res, index++), null);
        REQUIRES.requireEquals(ListUtil.getByObject(res, index++), null);
        REQUIRES.requireEquals(ListUtil.getByObject(res, index++), true);
        REQUIRES.requireEquals(ListUtil.getByObject(res, index++), false);
        REQUIRES.requireEquals(ListUtil.getByObject(res, index++), 20);
        REQUIRES.requireEquals(ListUtil.getByObject(res, index++), 30);
        REQUIRES.requireEquals(ListUtil.getByObject(res, index++), "50");


        str = "  {  ,  (),,(null),,}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 5);
        REQUIRES.requireEquals(ListUtil.getByObject(res, 3), null);

        str = "  { 30+60+110 , null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 7);
        REQUIRES.requireEquals(ListUtil.getByObject(res, 0), 200);

        str = "  { 30+60+110 ,(30+60+110 + 2 * 20), null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 8);
        REQUIRES.requireEquals(ListUtil.getByObject(res, 1), 240);

        str = "  { 30+60+110 ,(30+60+110 + 2 * 20 + @DateUtil.now()), null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 8);
        REQUIRES.requireInstanceOf(ListUtil.getByObject(res, 1), Double.class);

        str = "  { 30+60+110,@Objects.toString(20),(30+60+110 + 2 * 20 + @DateUtil.now()), null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEq(ListUtil.sizeByObject(res), 9);
        REQUIRES.requireInstanceOf(ListUtil.getByObject(res, 2), Double.class);

        Object o = ListUtil.getByObject(res, 1);
        REQUIRES.requireInstanceOf(o, String.class);
        REQUIRES.requireEquals(o, "20");
    }

    void doEmpty() {
        doEmpty0();
        doEmpty1();
        doEmpty2();
        doEmpty3();
    }

    void doEmpty0() {
        str = "   {   }    ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEmpty((List) res);

        str = "    {  :    } ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEmpty((Map) res);
    }

    void doEmpty1() {
        str = "   {}   ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEmpty((List) res);

        str = "    {:}              ";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEmpty((Map) res);
    }

    void doEmpty2() {
        str = "{         }";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEmpty((List) res);

        str = "{       :       }";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEmpty((Map) res);
    }

    void doEmpty3() {
        str = "{}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, ArrayList.class);
        REQUIRES.requireEmpty((List) res);
        str = "{:}";
        handler = running0(str);
        res = handler.run();
        REQUIRES.requireInstanceOf(res, HashMap.class);
        REQUIRES.requireEmpty((Map) res);
    }
}