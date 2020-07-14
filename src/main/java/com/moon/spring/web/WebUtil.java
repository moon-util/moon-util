package com.moon.spring.web;

import com.moon.more.web.CookieUtil;
import com.moon.more.web.RequestUtil;
import com.moon.more.web.ResponseUtil;
import com.moon.more.web.ResponseWriter;
import com.moon.spring.web.advice.RestExceptionEnum;
import com.moon.spring.web.advice.MvcUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.more.web.ResponseUtil.writer;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * @author moonsky
 * @see org.springframework.web.util.WebUtils
 * @see RequestUtil
 * @see ResponseUtil
 */
public final class WebUtil {

    private WebUtil() { noInstanceError(); }

    /**
     * 是否是明确的 ajax json 类型数据返回
     *
     * @param handler 处理器
     *
     * @return 明确的 ajax json 数据返回
     */
    public static boolean isConfirmedResponseBody(HandlerMethod handler) {
        Method method = handler.getMethod();
        if (ResponseEntity.class.isAssignableFrom(method.getReturnType())) {
            return true;
        }
        ResponseBody body = handler.getMethodAnnotation(ResponseBody.class);
        if (null != body) {
            return true;
        }
        // 获取类上面的注解，可能包含组合注解，故采用 spring 的工具类
        Class<?> beanType = handler.getBeanType();
        body = findMergedAnnotation(beanType, ResponseBody.class);
        return body != null;
    }

    /**
     * 是否是明确的 ajax json 类型数据返回
     *
     * @param method 方法
     *
     * @return 明确的 ajax json 数据返回
     *
     * @see HandlerMethod#getBeanType()
     */
    public static boolean isConfirmedResponseBody(Method method) {
        if (ResponseEntity.class.isAssignableFrom(method.getReturnType())) {
            return true;
        }
        ResponseBody body = findMergedAnnotation(method, ResponseBody.class);
        if (null != body) {
            return true;
        }
        Class<?> beanType = method.getDeclaringClass();
        body = findMergedAnnotation(beanType, ResponseBody.class);
        return body != null;
    }

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
     * 获取 cookie
     *
     * @param name cookie name
     *
     * @return Cookie
     */
    public static Cookie getCookie(String name) { return CookieUtil.get(getRequest(), name); }

    /**
     * 获取 cookie 值
     *
     * @param name cookie name
     *
     * @return Cookie 值
     */
    public static String getCookieValue(String name) { return CookieUtil.getValue(getRequest(), name); }

    /**
     * 获取请求请求头的值
     *
     * @param name 名称
     *
     * @return 值
     */
    public static String getRequestHeader(String name) { return getRequest().getHeader(name); }

    /**
     * 获取请求参数值
     *
     * @param name 参数名
     *
     * @return 参数值
     */
    public static String getRequestParam(String name) { return getRequest().getParameter(name); }

    /**
     * 获取请求参数值
     *
     * @param name           参数名
     * @param headerPriority 是否优先从 header 获取参数值
     *
     * @return 参数值
     *
     * @see RequestUtil#param(HttpServletRequest, String, boolean)
     */
    public static String getRequestParam(String name, boolean headerPriority) {
        return RequestUtil.param(getRequest(), name, headerPriority);
    }

    /**
     * 获取请求属性
     *
     * @param name 属性名
     *
     * @return 属性值
     */
    public static Object getRequestAttr(String name) { return getRequest().getAttribute(name); }

    /**
     * 设置请求属性
     *
     * @param name 属性名
     * @param data 属性值
     */
    public static void setRequestAttr(String name, Object data) { getRequest().setAttribute(name, data); }

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
     * 获取{@link HttpSession}
     *
     * @return HttpSession or null
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getSession();
    }

    /**
     * 获取{@link HttpSession}
     *
     * @return HttpSession or null
     */
    public static HttpSession getSession(boolean createIfAbsent) {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getSession(createIfAbsent);
    }

    /**
     * 获取 session 属性
     *
     * @param name 属性名
     *
     * @return 属性值
     */
    public static Object getSessionAttr(String name) { return getSession(true).getAttribute(name); }

    /**
     * 获取 session 属性
     *
     * @param name 属性名
     * @param data 属性值
     */
    public static void setSessionAttr(String name, Object data) { getSession(true).setAttribute(name, data); }

    /**
     * 获取{@link ServletContext}
     *
     * @return ServletContext or null
     */
    public static ServletContext getServletContext() {
        HttpServletRequest request = getRequest();
        return request == null ? null : request.getServletContext();
    }

    /**
     * 获取全局属性
     *
     * @param name 属性名
     *
     * @return 属性值
     */
    public static Object getAppAttr(String name) {
        return getServletContext().getAttribute(name);
    }


    /**
     * 获取 session 属性
     *
     * @param name 属性名
     * @param data 属性值
     */
    public static void setAppAttr(String name, Object data) {
        getServletContext().setAttribute(name, data);
    }

    /**
     * 将{@link HttpServletResponse}包装成{@link ResponseWriter}返回
     *
     * @return ResponseWriter ResponseWriter instance
     *
     * @throws NullPointerException if {@link #getResponse()} result is null
     */
    public static ResponseWriter getResponseWriter() { return writer(getResponse()); }

    /**
     * 处理全局异常
     *
     * @param request   HttpServletRequest
     * @param response  HttpServletResponse
     * @param throwable 异常对象
     *
     * @return ResponseEntity
     *
     * @throws Throwable 如果{@link RestExceptionEnum}没有对应的异常类型是原样抛出异常
     * @see MvcUtil
     * @see RestExceptionEnum
     */
    public static ResponseEntity onThrowable(
        HttpServletRequest request, HttpServletResponse response, Throwable throwable
    ) throws Throwable {
        return MvcUtil.onThrowable(request, response, throwable);
    }

    /**
     * 处理全局异常
     *
     * @param throwable 异常对象
     *
     * @return ResponseEntity
     *
     * @throws Throwable 如果{@link RestExceptionEnum}没有对应的异常类型是原样抛出异常
     * @see MvcUtil
     * @see RestExceptionEnum
     */
    public static ResponseEntity onThrowable(Throwable throwable) throws Throwable {
        return MvcUtil.onThrowable(throwable);
    }
}
