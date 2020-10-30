package com.moon.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public class CookieUtil {

    private CookieUtil() { noInstanceError(); }

    /**
     * 获取 Cookie
     * @param request 请求
     * @param name Cookie 名
     * @return Cookie
     */
    public static Cookie get(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 获取请求客户端 Cookie 值
     * @param request 请求
     * @param name  Cookie 名
     * @return Cookie 值
     */
    public static String getValue(HttpServletRequest request, String name) {
        Cookie cookie = get(request, name);
        return cookie == null ? null : cookie.getValue();
    }

    /**
     * 删除 Cookie
     *
     * @param response
     * @param name Cookie 名
     */
    public static void remove(HttpServletResponse response, String name) { set(response, name, null, 0); }

    /**
     * 设置cookie
     *
     * @param response HttpServletResponse
     * @param name     cookie name
     * @param value    cookie value
     */
    public static void set(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 设置cookie
     *
     * @param response        HttpServletResponse
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAgeInSeconds maxAge
     */
    public static void set(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
