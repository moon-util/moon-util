package com.moon.core.net;

import com.moon.core.lang.LangUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.net.enums.Protocol;

import java.net.URL;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.net.enums.Protocol.HTTP;
import static com.moon.core.net.enums.Protocol.HTTPS;

/**
 * @author benshaoye
 */
public final class URLUtil {
    private URLUtil() {
        noInstanceError();
    }

    /**
     * 屏蔽 URL 构造器的检查异常
     *
     * @param url
     * @return
     */
    public static URL withOnly(String url) {
        return LangUtil.apply(url, URL::new);
    }

    public static URL withHttp(String url) {
        return with(url, HTTP);
    }

    public static URL withHttps(String url) {
        return with(url, HTTPS);
    }

    public static URL with(String url, Protocol protocol) {
        return with(url, protocol.getText());
    }

    public static URL with(String url, String protocol) {
        url = url.trim();
        String root = "//";
        int endIndex = 6;
        int index = url.indexOf(root);
        if (Protocol.matchProtocolWith(url)) {
            if (index >= 0 && index < endIndex) {
                return withOnly(url);
            } else {
                return ThrowUtil.doThrow("no protocol: " + url);
            }
        } else {
            if (index == 0) {
                return withOnly(protocol + url);
            } else if (index < 0) {
                return withOnly(StringUtil.concat(protocol, root, url));
            }
            return ThrowUtil.doThrow("no protocol: " + url);
        }
    }
}
