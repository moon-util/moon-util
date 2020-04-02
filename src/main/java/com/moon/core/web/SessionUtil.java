package com.moon.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class SessionUtil {

    private SessionUtil() { noInstanceError(); }

    public static <T> T attr(HttpSession session, String key) {
        return session == null ? null : (T) session.getAttribute(key);
    }

    public static void attr(HttpSession session, String key, Object data) { session.setAttribute(key, data); }

    public static <T> T attr(HttpServletRequest request, String key) { return attr(request.getSession(), key); }

    public static void attr(HttpServletRequest request, String key, Object data) {
        attr(request.getSession(true), key, data);
    }

    public static <T> T removeAttr(HttpSession session, String key) {
        if (session == null) {
            return null;
        } else {
            T data = (T) session.getAttribute(key);
            session.removeAttribute(key);
            return data;
        }
    }

    public static <T> T removeAttr(HttpServletRequest request, String key) {
        return removeAttr(request.getSession(), key);
    }
}
