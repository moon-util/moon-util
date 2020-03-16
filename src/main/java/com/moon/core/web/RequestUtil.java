package com.moon.core.web;

import com.moon.core.lang.StringUtil;

import javax.servlet.http.HttpServletRequest;

import static com.moon.core.lang.StringUtil.isEmpty;
import static com.moon.core.lang.StringUtil.nullIfEmpty;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class RequestUtil {

    private RequestUtil() { noInstanceError(); }

    public static String header(HttpServletRequest request, String name) { return request.getHeader(name); }

    public static String param(HttpServletRequest request, String name) { return request.getParameter(name); }

    public static String param(
        HttpServletRequest request, String name, boolean headerPriority
    ) {
        String tokenVal = headerPriority ? header(request, name) : null;
        return isEmpty(tokenVal) ? param(request, name) : nullIfEmpty(tokenVal);
    }

    public static <T> T attr(HttpServletRequest request, String key) {
        return (T) request.getAttribute(key);
    }

    public static void attr(HttpServletRequest request, String key, Object value) {
        request.setAttribute(key, value);
    }

    private static boolean isUnknown(String str) {
        return StringUtil.isEmpty(str) || "unknown".equalsIgnoreCase(str);
    }

    @SuppressWarnings("all")
    public static String getRemoteRealIP(HttpServletRequest request) {
        String ip = null, comma = ",";
        try {
            ip = header(request, "x-forwarded-for");

            if (!isUnknown(ip)) {
                if (ip.indexOf(comma) != -1) {
                    ip = ip.split(comma)[0];
                }
            }
            if (isUnknown(ip)) { ip = header(request, "Proxy-Client-IP"); }
            if (isUnknown(ip)) { ip = header(request, "WL-Proxy-Client-IP"); }
            if (isUnknown(ip)) { ip = header(request, "HTTP_CLIENT_IP"); }
            if (isUnknown(ip)) { ip = header(request, "HTTP_X_FORWARDED_FOR"); }
            if (isUnknown(ip)) { ip = header(request, "X-Real-IP"); }
            if (isUnknown(ip)) { ip = request.getRemoteAddr(); }
        } catch (Exception e) {
            // ignore
        }
        return ip;
    }
}
