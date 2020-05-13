package com.moon.more.web;

import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public class CookieUtil {

    private CookieUtil() { noInstanceError(); }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        return WebUtils.getCookie(request, name);
    }

    public static String getValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie == null ? null : cookie.getValue();
    }

    public static void remove(HttpServletResponse response, String name) {
        set(response, name, null, 0);
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
