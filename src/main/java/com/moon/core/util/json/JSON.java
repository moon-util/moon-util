package com.moon.core.util.json;

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
 * @author benshaoye
 * @date 2018/9/14
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
     * @return
     */
    static JSON parse(String jsonText) {
        return jsonText == null
            ? JSONNull.NULL
            : new JSONParser(jsonText.startsWith("classpath:")
            ? readJsonString(jsonText.substring(10))
            : jsonText).toJSON();
    }

    /**
     * 将 json 文件解析成 JSON 对象
     *
     * @param jsonFile
     * @return
     */
    static JSON parse(File jsonFile) { return new JSONParser(readJsonString(jsonFile)).toJSON(); }

    /**
     * 从 io 流中解析 JSON 对象
     *
     * @param jsonIO
     * @return
     */
    static JSON parse(InputStream jsonIO) { return new JSONParser(readJsonString(jsonIO)).toJSON(); }

    /**
     * 解析
     *
     * @param jsonReader
     * @return
     */
    static JSON parse(Reader jsonReader) { return new JSONParser(readJsonString(ofLines(jsonReader))).toJSON(); }

    /**
     * 解析
     *
     * @param url
     * @return
     */
    static JSON parse(URL url) { return new JSONParser(readJsonString(url)).toJSON(); }

    /**
     * 解析
     *
     * @param jsonText
     * @return
     */
    static JSONArray parseArray(String jsonText) { return (JSONArray) parse(jsonText); }

    /**
     * 解析
     *
     * @param jsonText
     * @return
     */
    static JSONObject parseObject(String jsonText) { return (JSONObject) parse(jsonText); }

    /**
     * 解析为指定类
     *
     * @param jsonText
     * @param clazz
     * @param <T>
     * @return
     */
    static <T> T parseToType(String jsonText, Class<T> clazz) { return cast().toType(parse(jsonText), clazz); }

    /**
     * 解析为指定类 List
     *
     * @param jsonText
     * @param clazz
     * @param <T>
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
     * @return
     */
    static String toJSONString(URL url) { return readJsonString(url); }

    /**
     * 将 Object 字符串化
     *
     * @param obj
     * @return
     */
    static String toJSONString(Object obj) { return JSONCfg.WEAK.get().stringify(obj); }

    /**
     * 将 Object 字符串化
     *
     * @param obj
     * @return
     */
    static String stringify(Object obj) { return toJSONString(obj); }

    /*
     * ------------------------------------------------------
     * getSheet value
     * ------------------------------------------------------
     */

    /**
     * 获取当前 JSON 对象实际对应的 Java 对象
     *
     * @param <T>
     * @return
     */
    <T> T get();

    /**
     * 获取 json object
     *
     * @param key
     * @return
     */
    JSONObject getJSONObject(KEY key);

    /**
     * 获取 json array
     *
     * @param key
     * @return
     */
    JSONArray getJSONArray(KEY key);

    /**
     * 获取 int
     *
     * @param key
     * @return
     */
    int getIntValue(KEY key);

    /**
     * 获取 Integer
     *
     * @param key
     * @return
     */
    Integer getInteger(KEY key);

    /**
     * 获取 long
     *
     * @param key
     * @return
     */
    long getLongValue(KEY key);

    /**
     * 获取 Long
     *
     * @param key
     * @return
     */
    Long getLong(KEY key);

    /**
     * 获取 double
     *
     * @param key
     * @return
     */
    double getDoubleValue(KEY key);

    /**
     * 获取 Double
     *
     * @param key
     * @return
     */
    Double getDouble(KEY key);

    /**
     * 获取 String
     *
     * @param key
     * @return
     */
    String getString(KEY key);

    /**
     * 获取 Map
     *
     * @param key
     * @return
     */
    default Map<String, Object> getMap(KEY key) { return this.getJSONObject(key); }

    /**
     * 获取 Integer
     *
     * @param key
     * @return
     */
    default List<Object> getList(KEY key) { return this.getJSONArray(key); }

    /**
     * 将当前 JSON 转化为指定类型对象
     *
     * @param type
     * @return
     */
    default <T> T convertTo(Class<T> type) { return cast().toType(this, type); }
}
