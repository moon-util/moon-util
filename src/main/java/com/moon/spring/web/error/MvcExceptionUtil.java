package com.moon.spring.web.error;

import com.moon.spring.web.WebUtil;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author benshaoye
 * @see WebUtil#onThrowable(Throwable)
 * @see WebUtil#onThrowable(HttpServletRequest, HttpServletResponse, Throwable)
 * @see ExceptionCache#newExceptionService()
 * @see ExceptionCache#getDefaultInstance()
 */
public final class MvcExceptionUtil extends ExceptionCache {

    /**
     * 处理全局异常
     *
     * @param root 异常对象
     *
     * @return ResponseEntity
     *
     * @throws Throwable 当不能正确处理异常时，抛出原异常
     * @see WebUtil#onThrowable(Throwable)
     */
    public static ResponseEntity onThrowable(Throwable root) throws Throwable {
        return getDefaultInstance().onThrowable(root);
    }

    /**
     * 处理全局异常
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param root     异常对象
     *
     * @return ResponseEntity
     *
     * @throws Throwable
     * @see MvcExceptionUtil
     * @see RestExceptionEnum
     * @see WebUtil#onThrowable(HttpServletRequest, HttpServletResponse, Throwable)
     */
    public static ResponseEntity onThrowable(HttpServletRequest request, HttpServletResponse response, Throwable root)
        throws Throwable {
        return getDefaultInstance().onThrowable(request, response, root);
    }

    /**
     * 注册异常处理器
     *
     * @param exceptionType 处理的异常类型
     * @param handler       异常处理器
     *
     * @see RestExceptionEnum
     * @see ExceptionService#registryIfAbsent(String, RestExceptionHandler)
     */
    public static void registry(Class exceptionType, RestExceptionHandler handler) {
        getDefaultInstance().registry(exceptionType.getName(), handler);
    }
}
