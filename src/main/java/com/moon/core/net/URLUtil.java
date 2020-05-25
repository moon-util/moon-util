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
     * @param url url 地址
     *
     * @return url instance
     */
    public static URL url(String url) {
        return LangUtil.apply(url, URL::new);
    }

    public static URL ofHttp(String url) {
        return of(url, HTTP);
    }

    public static URL ofHttps(String url) {
        return of(url, HTTPS);
    }

    public static URL of(String url, Protocol protocol) {
        return of(url, protocol.getText());
    }

    public static URL of(String url, String protocol) {
        url = url.trim();
        String root = "//";
        int endIndex = 6;
        int index = url.indexOf(root);
        if (Protocol.matchProtocolWith(url)) {
            if (index >= 0 && index < endIndex) {
                return url(url);
            } else {
                return ThrowUtil.runtime("no protocol: " + url);
            }
        } else {
            if (index == 0) {
                return url(protocol + url);
            } else if (index < 0) {
                return url(StringUtil.concat(protocol, root, url));
            }
            return ThrowUtil.runtime("no protocol: " + url);
        }
    }
}
