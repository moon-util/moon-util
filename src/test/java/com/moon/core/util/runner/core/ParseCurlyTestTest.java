package com.moon.core.util.runner.core;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.ListUtil;
import com.moon.core.util.MapUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class ParseCurlyTestTest {

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
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 2);

        str = "  {  ,  (),,(null),,}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 5);

        str = "  {  'name' ,'age'  ,}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 2);
    }

    @Test
    void testParse0() {
        doEmpty();
        doList();
        doMap();
        doVarMap();
        doVarList();

        assertThrows(Throwable.class, () -> running0("{key: 'value', : (50 + 60   ), true: @DateUtil.now()+0}"));
    }

    void doVarList() {
        data = running0("{key: 'value', null: (50 + 60   ), true: @DateUtil.now()+0}").run();

        str = "{key, key,true,[true],[null]}";
        handler = running0(str);
        assertThrows(Throwable.class, () -> handler.run());
        res = handler.run(data);
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.getByObject(res, 0), "value");
        assertEquals(ListUtil.getByObject(res, 1), "value");
        assertEquals(ListUtil.getByObject(res, 2), true);
        assertTrue(ListUtil.getByObject(res, 3) instanceof Double);
        assertEquals(ListUtil.getByObject(res, 4), (Object) 110);
    }

    void doVarMap() {
        data = running0("{key: 'value', null: (50 + 60   ), true: @DateUtil.now()+0}").run();

        str = "{key: key}";
        handler = running0(str);
        assertThrows(Throwable.class, () -> handler.run());
        res = handler.run(data);
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.getByObject(res, "key"), "value");

        str = "{key: 'value', null: [null]}";
        handler = running0(str);
        assertThrows(Throwable.class, () -> handler.run());
        res = handler.run(data);
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.getByObject(res, "key"), "value");
        assertEquals(MapUtil.getByObject(res, null), 110);
        assertTrue(MapUtil.getByObject(data, true) instanceof Double);
        System.out.println(((Number) MapUtil.getByObject(data, true)).longValue());
    }

    void doMap() {
        str = "{key: 'value'}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.sizeByObject(res), 1);
        assertEquals(MapUtil.getByObject(res, "key"), "value");

        str = "{key: 'value',}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.sizeByObject(res), 1);
        assertEquals(MapUtil.getByObject(res, "key"), "value");

        str = "{key: 'value', null: 20}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.sizeByObject(res), 2);
        assertEquals(MapUtil.getByObject(res, "key"), "value");
        assertEquals(MapUtil.getByObject(res, null), 20);

        str = "{key: 'value', null: 20, true: '100asdfasdf'}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.sizeByObject(res), 3);
        assertEquals(MapUtil.getByObject(res, "key"), "value");
        assertEquals(MapUtil.getByObject(res, null), 20);
        assertEquals(MapUtil.getByObject(res, true), "100asdfasdf");

        str = "{key: 'value', null: 20, true: '100asdfasdf'  ,   false  : \"sdfasdfgawfasdfasdf\"}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.sizeByObject(res), 4);
        assertEquals(MapUtil.getByObject(res, "key"), "value");
        assertEquals(MapUtil.getByObject(res, null), 20);
        assertEquals(MapUtil.getByObject(res, true), "100asdfasdf");
        assertEquals(MapUtil.getByObject(res, false), "sdfasdfgawfasdfasdf");

        str = "{key: 'value', null: 20, true: '100asdfasdf'  ,  false  : \"sdfasdfgawfasdfasdf\", 20: 150}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.getByObject(res, 20), 150);

        str = "{key: 'value', null: 20, true: '100asdfasdf',  1503.2: 110}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.getByObject(res, 1503.2), 110);

        str = "{key: 'value', null: 20, true: '100asdfasdf',,  1503.2: 110}";
        assertThrows(Throwable.class, () -> handler = running0(str));

        str = "{key: 'value', null:: 20, true: '100asdfasdf',,  1503.2: 110}";
        assertThrows(Throwable.class, () -> handler = running0(str));

        str = "{key: 'value', null: 20, true: '100asdfasdf'   ,    ,  1503.2: 110}";
        assertThrows(Throwable.class, () -> handler = running0(str));

        str = "{key: 'value', null    :    : 20, true: '100asdfasdf',,  1503.2: 110}";
        assertThrows(Throwable.class, () -> handler = running0(str));

        str = "{key: 'value', null , null   :    : 20, true: '100asdfasdf',,  1503.2: 110}";
        assertThrows(Throwable.class, () -> handler = running0(str));

        str = "{key: 'value', null   : null,    : 20, true: '100asdfasdf',,  1503.2: 110}";
        assertThrows(Throwable.class, () -> handler = running0(str));

        str = "{key: 'value', null: 20, true: '100asdfasdf',  1503.2: (110)}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.getByObject(res, 1503.2), 110);

        str = "{key: 'value', null: 20+@DateUtil.now(), true: '100asdfasdf'+@DateUtil.now(),  1503.2: (110)}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertEquals(MapUtil.getByObject(res, 1503.2), 110);
        assertTrue(MapUtil.getByObject(res, true) instanceof String);
        assertTrue(MapUtil.getByObject(res, null) instanceof Double);

        str = "{key: 'value', null   : null,  '  : 20, true: '100asdfasdf',  1503.2: 110}";
        assertThrows(Throwable.class, () -> handler = running0(str));
    }

    void doList() {

        str = "{,}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 1);

        str = "  {  ,  }  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 1);

        str = "  {  ,  ,}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 2);

        str = "  {  ,  ,,,,}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 5);

        str = "  {  , null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 7);

        int index = 0;
        assertEquals(ListUtil.getByObject(res, index++), (Object) null);
        assertEquals(ListUtil.getByObject(res, index++), (Object) null);
        assertEquals(ListUtil.getByObject(res, index++), true);
        assertEquals(ListUtil.getByObject(res, index++), false);
        assertEquals(ListUtil.getByObject(res, index++), (Object) 20);
        assertEquals(ListUtil.getByObject(res, index++), (Object) 30);
        assertEquals(ListUtil.getByObject(res, index++), "50");


        str = "  {  ,  (),,(null),,}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.getByObject(res, 3), (Object) null);

        str = "  { 30+60+110 , null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 7);
        assertEquals(ListUtil.getByObject(res, 0), (Object) 200);

        str = "  { 30+60+110 ,(30+60+110 + 2 * 20), null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 8);
        assertEquals(ListUtil.getByObject(res, 1), (Object) 240);

        str = "  { 30+60+110 ,(30+60+110 + 2 * 20 + @DateUtil.now()), null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 8);
        assertTrue(ListUtil.getByObject(res, 1) instanceof Double);

        str = "  { 30+60+110,@Objects.toString(20),(30+60+110 + 2 * 20 + @DateUtil.now()), null , true, false,20,30,'50'}  ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertEquals(ListUtil.sizeByObject(res), 9);
        assertTrue(ListUtil.getByObject(res, 2) instanceof Double);

        Object o = ListUtil.getByObject(res, 1);
        assertTrue(o instanceof String);
        assertEquals(o, "20");
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
        assertTrue(res instanceof ArrayList);
        assertTrue(((List) res).isEmpty());

        str = "    {  :    } ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertTrue(((Map) res).isEmpty());
    }

    void doEmpty1() {
        str = "   {}   ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertTrue(((List) res).isEmpty());

        str = "    {:}              ";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertTrue(((Map) res).isEmpty());
    }

    void doEmpty2() {
        str = "{         }";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertTrue(((List) res).isEmpty());

        str = "{       :       }";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertTrue(((Map) res).isEmpty());
    }

    void doEmpty3() {
        str = "{}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof ArrayList);
        assertTrue(((List) res).isEmpty());
        str = "{:}";
        handler = running0(str);
        res = handler.run();
        assertTrue(res instanceof HashMap);
        assertTrue(((Map) res).isEmpty());
    }
}