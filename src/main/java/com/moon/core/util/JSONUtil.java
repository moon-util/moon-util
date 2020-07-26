package com.moon.core.util;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.LangUtil;
import com.moon.core.lang.ref.WeakAccessor;
import com.moon.core.lang.reflect.MethodUtil;
import com.moon.core.util.iterator.LinesIterator;
import com.moon.core.json.JSON;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.function.Function;

import static com.moon.core.enums.Const.WIN_FILE_INVALID_CHAR;
import static com.moon.core.lang.StringUtil.trimToEmpty;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.OptionalUtil.computeOrElse;

/**
 * @author moonsky
 */
public final class JSONUtil {

    private final static WeakAccessor<Json> JsonTaxerAccessor = WeakAccessor.of(Json::new);

    private JSONUtil() {
        noInstanceError();
    }

    /*
     * ----------------------------------------------------------------------
     * converters
     * ----------------------------------------------------------------------
     */

    public static String stringify(Object obj) {
        return JsonTaxerAccessor.get().stringify.apply(obj);
    }

    public static Object parse(String jsonStr) {
        return JsonTaxerAccessor.get().parser.apply(jsonStr);
    }

    /*
     * ----------------------------------------------------------------------
     * read json string
     * ----------------------------------------------------------------------
     */

    public static String readJsonString(String filePath) {
        return readJsonString(new LinesIterator(filePath));
    }

    public static String readJsonString(File file) {
        return readJsonString(IteratorUtil.ofLines(file));
    }

    public static String readJsonString(URL url) {
        return LangUtil.apply(url, u -> readJsonString(IteratorUtil.ofLines(u.openStream())));
    }

    public static String readJsonString(InputStream is) {
        return readJsonString(IteratorUtil.ofLines(is));
    }

    public static String readJsonString(URL url, Charset charset) {
        return LangUtil.applyBi(url, charset, (u, c) -> readJsonString(IteratorUtil.ofLines(u.openStream(), c)));
    }

    public static String readJsonString(InputStream is, Charset charset) {
        return readJsonString(IteratorUtil.ofLines(is, charset));
    }

    public static String readJsonString(Iterator<String> iterator) {
        StringBuilder jsonAppender = new StringBuilder(2048);

        final String multiplyStart = "/*";
        final String multiplyEnd = "*/";
        final String single = "//";
        boolean multiply = false;
        String temp;

        for (; iterator.hasNext(); ) {
            temp = trimToEmpty(String.valueOf(iterator.next()));
            if (multiply) {
                multiply = !temp.endsWith(multiplyEnd);
            } else if (!((multiply = temp.startsWith(multiplyStart)) || temp.startsWith(single))) {
                jsonAppender.append(temp);
                continue;
            }
            if (multiply) {
                multiply = !temp.endsWith(multiplyEnd);
            }
        }
        return jsonAppender.length() > 0 && jsonAppender.charAt(0) == WIN_FILE_INVALID_CHAR
            ? jsonAppender.substring(1) : jsonAppender.toString();
    }

    /*
     * ----------------------------------------------------------------------
     * inner methods
     * ----------------------------------------------------------------------
     */

    private static class Json {
        final Function<Object, String> stringify;
        final Function<String, Object> parser;

        Json() {
            Function[] functions = null;
            try {
                functions = computeOrElse(functions, fs -> fs, this::loadJSON);
                functions = computeOrElse(functions, fs -> fs, this::loadFastJson);
                functions = computeOrElse(functions, fs -> fs, this::loadJackson);
                functions = computeOrElse(functions, fs -> fs, this::loadGson);
            } catch (Exception e) {
                functions = this.loadJSON();
            }
            this.stringify = functions[0];
            this.parser = functions[1];
        }

        Function[] loadFastJson() {
            Class target = ClassUtil.forName("com.alibaba.fastjson.JSON");
            return new Function[]{
                object -> MethodUtil.invokeStatic("toJSONString", target, object),
                object -> MethodUtil.invokeStatic("parse", target, object),
            };
        }

        Function[] loadJackson() {
            Function[] functions = new Function[2];
            throw new UnsupportedOperationException("You must import package of Jackson");
        }

        Function[] loadGson() {
            Function[] functions = new Function[2];
            throw new UnsupportedOperationException("You must import package of Gson");
        }

        Function[] loadJSON() {
            return new Function[]{
                JSON::stringify,
                object -> JSON.parse((String) object)
            };
        }
    }
}
