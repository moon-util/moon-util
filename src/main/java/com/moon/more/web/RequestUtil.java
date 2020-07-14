package com.moon.more.web;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.moon.core.lang.StringUtil.isEmpty;
import static com.moon.core.lang.StringUtil.nullIfEmpty;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class RequestUtil extends ContentTypeUtil{

    private RequestUtil() { noInstanceError(); }

    /**
     * 获取所有请求头
     *
     * @param request request
     * @param keys    需要获取的请求头名称
     *
     * @return 请求头名称: 请求头值
     */
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

    /**
     * 获取指定请求头
     *
     * @param request request
     * @param name    请求头名字
     *
     * @return 请求头值
     */
    public static String header(HttpServletRequest request, String name) { return request.getHeader(name); }

    /**
     * 获取请求参数
     *
     * @param request request
     * @param name    参数名
     *
     * @return 参数值
     */
    public static String param(HttpServletRequest request, String name) { return request.getParameter(name); }

    /**
     * 获取请求参数，有些请求中可能将参数放在 header 中，这里可通过设置 header 优先获取
     * 如果不存在于 header 中，就从请求参数中获取
     *
     * @param request        request
     * @param name           参数名
     * @param headerPriority 是否优先从 header 中获取参数值
     *
     * @return 参数值
     */
    public static String param(
        HttpServletRequest request, String name, boolean headerPriority
    ) {
        String tokenVal = headerPriority ? header(request, name) : null;
        return isEmpty(tokenVal) ? param(request, name) : nullIfEmpty(tokenVal);
    }

    /**
     * 获取 request 属性
     *
     * @param request request
     * @param key     属性名
     * @param <T>     为了兼容返回接收类型
     *
     * @return 返回属性值 或 null
     */
    public static <T> T attr(HttpServletRequest request, String key) { return (T) request.getAttribute(key); }

    /**
     * 设置 Request 属性
     *
     * @param request request 对象
     * @param key     属性名
     * @param value   属性值
     */
    public static void attr(HttpServletRequest request, String key, Object value) { request.setAttribute(key, value); }

    /**
     * 转发
     *
     * @param request
     * @param response
     * @param path
     *
     * @throws ServletException
     * @throws IOException
     */
    public static void forward(HttpServletRequest request, HttpServletResponse response, String path)
        throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }

    private static boolean isUnknown(String str) { return StringUtil.isEmpty(str) || "unknown".equalsIgnoreCase(str); }

    /**
     * 获取远程真实请求 IP
     *
     * @param request request
     *
     * @return ip
     */
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

    static String getRequestDomain(HttpServletRequest request) {
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

    static String getRequestURL(HttpServletRequest request) {
        return StringUtil.concat(getRequestDomain(request), request.getContextPath(), request.getServletPath());
    }

    static String getRequestFullURL(HttpServletRequest request) {
        String url = getRequestURL(request), query = request.getQueryString();
        return isEmpty(query) ? url : StringUtil.concat(url, "?", query);
    }
}
