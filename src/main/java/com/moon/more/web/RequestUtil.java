package com.moon.more.web;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.moon.core.lang.StringUtil.isEmpty;
import static com.moon.core.lang.StringUtil.nullIfEmpty;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class RequestUtil {

    private RequestUtil() { noInstanceError(); }

    public static Map<String, String> headersMap(HttpServletRequest request, String... keys) {
        Map<String, String> headers = new HashMap<>(16);
        if (keys == null || keys.length < 1) {
            Enumeration<String> enums = request.getHeaderNames();
            while (enums.hasMoreElements()) {
                String key = enums.nextElement();
                headers.put(key, header(request, key));
            }
        } else {
            for (String key : keys) {
                headers.put(key, header(request, key));
            }
        }
        return headers;
    }

    public static String header(HttpServletRequest request, String name) { return request.getHeader(name); }

    public static String param(HttpServletRequest request, String name) { return request.getParameter(name); }

    public static String param(
        HttpServletRequest request, String name, boolean headerPriority
    ) {
        String tokenVal = headerPriority ? header(request, name) : null;
        return isEmpty(tokenVal) ? param(request, name) : nullIfEmpty(tokenVal);
    }

    public static <T> T attr(HttpServletRequest request, String key) { return (T) request.getAttribute(key); }

    public static void attr(HttpServletRequest request, String key, Object value) { request.setAttribute(key, value); }

    private static boolean isUnknown(String str) { return StringUtil.isEmpty(str) || "unknown".equalsIgnoreCase(str); }

    @SuppressWarnings("all")
    public static String getRequestRealIP(HttpServletRequest request) {
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

    public static String getRequestDomain(HttpServletRequest request) {
        int port = request.getServerPort(), dftPort = 80;
        String protocol = request.getProtocol();
        String host = getRequestRealIP(request);
        if (port == dftPort) {
            return JoinerUtil.join(new String[]{protocol, "://", host}, "");
        } else {
            String[] s = {protocol, "://", host, ":", String.valueOf(port)};
            return JoinerUtil.join(s, "");
        }
    }

    public static String getRequestURL(HttpServletRequest request) {
        return StringUtil.concat(getRequestDomain(request), request.getContextPath(), request.getServletPath());
    }

    public static String getRequestFullURL(HttpServletRequest request) {
        String url = getRequestURL(request), query = request.getQueryString();
        return isEmpty(query) ? url : StringUtil.concat(url, "?", query);
    }
}
