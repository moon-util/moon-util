package com.moon.spring.web;

import com.moon.more.web.ResponseWriter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.more.web.ResponseUtil.writer;

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

    /**
     * 获取{@link HttpServletResponse}
     *
     * @return HttpServletResponse or null
     */
    public static HttpServletResponse getResponse() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes serveAttrs = (ServletRequestAttributes) attributes;
            return serveAttrs.getResponse();
        }
        return null;
    }

    /**
     * 将{@link HttpServletResponse}包装成{@link ResponseWriter}返回
     *
     * @return ResponseWriter ResponseWriter instance
     *
     * @throws NullPointerException if {@link #getResponse()} result is null
     */
    public static ResponseWriter responseWriter() { return writer(getResponse()); }
}
