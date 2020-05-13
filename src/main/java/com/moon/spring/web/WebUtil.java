package com.moon.spring.web;

import com.moon.more.web.RequestUtil;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class WebUtil {

    private WebUtil() { noInstanceError(); }

    /**
     * 获取{@link HttpServletRequest}
     *
     * @return HttpServletRequest or null
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes serveAttrs = (ServletRequestAttributes) attributes;
            return serveAttrs.getRequest();
        }
        return null;
    }

    public static HttpServletResponse getResponse() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes serveAttrs = (ServletRequestAttributes) attributes;
            return serveAttrs.getResponse();
        }
        return null;
    }

    public static Map<String, String> headersMap(String... keys) { return RequestUtil.headersMap(getRequest(), keys); }

    public static String header(String name) { return getRequest().getHeader(name); }

    public static String param(String name) { return getRequest().getParameter(name); }

    public static String param(String name, boolean headerPriority) {
        return RequestUtil.param(getRequest(), name, headerPriority);
    }

    public static <T> T attr(String key) { return (T) getRequest().getAttribute(key); }

    public static void attr(String key, Object value) { getRequest().setAttribute(key, value); }

    @SuppressWarnings("all")
    public static String getRequestRealIP() { return RequestUtil.getRequestRealIP(getRequest()); }

    public static String getRequestDomain() { return RequestUtil.getRequestDomain(getRequest()); }

    public static String getRequestURL() { return RequestUtil.getRequestURL(getRequest()); }

    public static String getRequestFullURL() { return RequestUtil.getRequestFullURL(getRequest()); }
}
