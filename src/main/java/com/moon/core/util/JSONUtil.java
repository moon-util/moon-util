package com.moon.core.util;

import com.moon.core.json.JSON;
import com.moon.core.lang.LangUtil;
import com.moon.core.lang.ref.WeakAccessor;
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

    public static String readJsonString(String filePath) { return readJsonString(new LinesIterator(filePath)); }

    public static String readJsonString(File file) { return readJsonString(IteratorUtil.ofLines(file)); }

    public static String readJsonString(URL url) {
        return LangUtil.apply(url, u -> readJsonString(IteratorUtil.ofLines(u.openStream())));
    }

    public static String readJsonString(InputStream is) { return readJsonString(IteratorUtil.ofLines(is)); }

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
}
