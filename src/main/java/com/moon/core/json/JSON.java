package com.moon.core.json;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.moon.core.util.IteratorUtil.ofLines;
import static com.moon.core.util.JSONUtil.readJsonString;
import static com.moon.core.util.TypeUtil.cast;

/**
 * JSON 工具类
 * <p>
 * 主要是 JSON 解析，解析完成的是一个{@link JSON}对象，可直接进行各种属性操作和访问
 * <p>
 * 注意：有些数据类型是不支持一些操作的，比如：{@code JSON.parse("true")} 解析出来的是{@link JSONBoolean}
 * 此时作用于对象（{@link JSONObject}）或者数组（{@link JSONArray}）的方法{@link JSON#getString(Object)}等方法
 * 是无效的，true 能执行什么方法呢？对吧，所以这样的操作只会抛出异常，
 *
 * @author moonsky
 */
public interface JSON<KEY> extends Cloneable, Serializable {

    /*
     * ------------------------------------------------------
     * parser
     * ------------------------------------------------------
     */

    /**
     * 将 json string 解析成 JSON 对象
     *
     * @param jsonText
     *
     * @return
     */
    static JSON parse(String jsonText) {
        return jsonText == null ? JSONNull.NULL : new JSONParser(jsonText.startsWith("classpath:") ? readJsonString(
            jsonText.substring(10)) : jsonText).toJSON();
    }

    /**
     * 将 json 文件解析成 JSON 对象
     *
     * @param jsonFile
     *
     * @return
     */
    static JSON parse(File jsonFile) { return new JSONParser(readJsonString(jsonFile)).toJSON(); }

    /**
     * 从 io 流中解析 JSON 对象
     *
     * @param jsonIO
     *
     * @return
     */
    static JSON parse(InputStream jsonIO) { return new JSONParser(readJsonString(jsonIO)).toJSON(); }

    /**
     * 解析
     *
     * @param jsonReader
     *
     * @return
     */
    static JSON parse(Reader jsonReader) { return new JSONParser(readJsonString(ofLines(jsonReader))).toJSON(); }

    /**
     * 解析
     *
     * @param url
     *
     * @return
     */
    static JSON parse(URL url) { return new JSONParser(readJsonString(url)).toJSON(); }

    /**
     * 解析
     *
     * @param jsonText
     *
     * @return
     */
    static JSONArray parseArray(String jsonText) { return (JSONArray) parse(jsonText); }

    /**
     * 解析
     *
     * @param jsonText
     *
     * @return
     */
    static JSONObject parseObject(String jsonText) { return (JSONObject) parse(jsonText); }

    /**
     * 解析为指定类
     *
     * @param jsonText
     * @param clazz
     * @param <T>
     *
     * @return
     */
    static <T> T parseToType(String jsonText, Class<T> clazz) { return cast().toType(parse(jsonText), clazz); }

    /**
     * 解析为指定类 List
     *
     * @param jsonText
     * @param clazz
     * @param <T>
     *
     * @return
     */
    static <T> List<T> parseToList(String jsonText, Class<T> clazz) {
        JSONArray array = parseArray(jsonText);
        List<T> result = new ArrayList<>(array.size());
        array.forEach(item -> result.add(cast().toType(item, clazz)));
        return result;
    }

    /*
     * ------------------------------------------------------
     * stringify
     * ------------------------------------------------------
     */

    /**
     * 访问 URL 得到 JSON 字符串
     *
     * @param url
     *
     * @return
     */
    static String toJsonString(URL url) { return readJsonString(url); }

    /**
     * 将 Object 字符串化
     *
     * @param obj
     *
     * @return
     */
    static String toJsonString(Object obj) { return JSONCfg.getStringer().stringify(obj); }

    /**
     * 将 Object 字符串化
     *
     * @param obj
     *
     * @return
     */
    static String stringify(Object obj) { return toJsonString(obj); }

    /**
     * 字符串化
     *
     * @param obj    数据
     * @param indent 缩进
     *
     * @return json 字符串
     */
    static String stringify(Object obj, int indent) {
        return JSONCfg.getStringer().stringify(obj, StringifySettings.of(indent));
    }

    /*
     * ------------------------------------------------------
     * get value
     * ------------------------------------------------------
     */

    /**
     * 获取当前 JSON 对象实际对应的 Java 对象
     *
     * @param <T> 泛型类，仅返为了兼容各种类型数据都能接受返回值
     *            他是{@link JSON}的实现类
     *
     * @return 实际实现类对应的实例
     */
    <T> T get();

    /**
     * 获取 json object
     *
     * @param key 键名
     *
     * @return 当前 key 对应的 JSON object
     */
    JSONObject getJsonObject(KEY key);

    /**
     * 获取 json array
     *
     * @param key 键名
     *
     * @return 当前 key 对应的 JSON array
     */
    JSONArray getJsonArray(KEY key);

    /**
     * 获取 int
     *
     * @param key 键名
     *
     * @return 整型值
     */
    int getIntValue(KEY key);

    /**
     * 获取 Integer
     *
     * @param key 键名
     *
     * @return 整型值或 null
     */
    Integer getInteger(KEY key);

    /**
     * 获取 long
     *
     * @param key 键名
     *
     * @return 长整形值
     */
    long getLongValue(KEY key);

    /**
     * 获取 Long
     *
     * @param key 键名
     *
     * @return 长整形值或 null
     */
    Long getLong(KEY key);

    /**
     * 获取 double
     *
     * @param key 键名
     *
     * @return double 型值
     */
    double getDoubleValue(KEY key);

    /**
     * 获取 Double
     *
     * @param key 键名
     *
     * @return double 型值或 null
     */
    Double getDouble(KEY key);

    /**
     * 获取 String
     *
     * @param key 键名
     *
     * @return 字符串或 null
     */
    String getString(KEY key);

    /**
     * 获取 Map
     *
     * @param key 键名
     *
     * @return Map 或 null
     */
    default Map<String, Object> getMap(KEY key) { return this.getJsonObject(key); }

    /**
     * 获取 Integer
     *
     * @param key 键名
     *
     * @return
     */
    default List<Object> getList(KEY key) { return this.getJsonArray(key); }

    /**
     * 将当前 JSON 转化为指定类型对象
     *
     * @param type 目标类型
     *
     * @return 目标类型实例
     */
    default <T> T convertTo(Class<T> type) { return cast().toType(this, type); }
}
