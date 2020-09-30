package com.moon.core.util;

import com.moon.core.json.JSON;
import com.moon.core.lang.LangUtil;
import com.moon.core.util.iterator.LinesIterator;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import static com.moon.core.enums.Const.WIN_FILE_INVALID_CHAR;
import static com.moon.core.lang.StringUtil.trimToEmpty;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class JSONUtil {

    private JSONUtil() { noInstanceError(); }

    /*
     * ----------------------------------------------------------------------
     * converters
     * ----------------------------------------------------------------------
     */

    public static String stringify(Object obj) { return JSON.stringify(obj); }

    public static Object parse(String jsonStr) { return JSON.parse(jsonStr); }

    /*
     * ----------------------------------------------------------------------
     * read json string
     * ----------------------------------------------------------------------
     */

    /**
     * 从文件中读取 json，支持单行注释、多行注释
     *
     * @param filePath 文件路径
     *
     * @return json 字符串
     */
    public static String readJsonString(String filePath) { return readJsonString(new LinesIterator(filePath)); }

    /**
     * 从文件中读取 json，支持单行注释、多行注释
     *
     * @param file 文件路径
     *
     * @return json 字符串
     */
    public static String readJsonString(File file) { return readJsonString(IteratorUtil.ofLines(file)); }

    /**
     * 从 URL 资源中读取 json，支持单行/多行注释
     *
     * @param url URL 资源
     *
     * @return json 字符串
     */
    public static String readJsonString(URL url) {
        return LangUtil.apply(url, u -> readJsonString(IteratorUtil.ofLines(u.openStream())));
    }

    /**
     * 读取 json，支持单行/多行注释
     *
     * @param is JSON 资源
     *
     * @return json 字符串
     */
    public static String readJsonString(InputStream is) { return readJsonString(IteratorUtil.ofLines(is)); }

    /**
     * 读取 json，支持单行/多行注释
     *
     * @param url     JSON 资源
     * @param charset 字符集
     *
     * @return json 字符串
     */
    public static String readJsonString(URL url, Charset charset) {
        return LangUtil.applyBi(url, charset, (u, c) -> readJsonString(IteratorUtil.ofLines(u.openStream(), c)));
    }

    /**
     * 读取 json，支持单行/多行注释
     *
     * @param is      JSON 资源
     * @param charset 字符集
     *
     * @return json 字符串
     */
    public static String readJsonString(InputStream is, Charset charset) {
        return readJsonString(IteratorUtil.ofLines(is, charset));
    }

    /**
     * 读取 json，支持单行/多行注释
     *
     * @param iterator iterator 的所有行构成一个完整的 json，不可缺少
     *
     * @return json 字符串
     */
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
        return jsonAppender.length() > 0 && jsonAppender.charAt(0) == WIN_FILE_INVALID_CHAR ? jsonAppender.substring(1) : jsonAppender
            .toString();
    }
}
