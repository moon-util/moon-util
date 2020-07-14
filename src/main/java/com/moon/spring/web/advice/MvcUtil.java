package com.moon.spring.web.advice;

import com.moon.spring.web.WebUtil;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author benshaoye
 * @see WebUtil#onThrowable(Throwable)
 * @see WebUtil#onThrowable(HttpServletRequest, HttpServletResponse, Throwable)
 */
public final class MvcUtil extends ExceptionCache {

    /**
     * 处理全局异常
     *
     * @param root 异常对象
     *
     * @return ResponseEntity
     *
     * @throws Throwable
     * @see WebUtil#onThrowable(Throwable)
     */
    public static ResponseEntity onThrowable(Throwable root) throws Throwable {
        ResponseEntity result = doThrowable(root);
        if (result == null) {
            throw root;
        }
        return result;
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
     * @see MvcUtil
     * @see RestExceptionEnum
     * @see WebUtil#onThrowable(HttpServletRequest, HttpServletResponse, Throwable)
     */
    public static ResponseEntity onThrowable(HttpServletRequest request, HttpServletResponse response, Throwable root)
        throws Throwable {
        ResponseEntity result = doThrowable(request, response, root);
        if (result == null) {
            throw root;
        }
        return result;
    }

    private static ResponseEntity doThrowable(Throwable ex) throws Throwable {
        Throwable cause;
        ResponseEntity result = null;
        if ((cause = ex.getCause()) != null) {
            result = doThrowable(cause);
        }
        if (result == null) {
            RestExceptionHandler handler = RestExceptionEnum.forType(ex.getClass());
            if (handler != null) {
                result = handler.onThrowable(ex);
            }
        }
        return result;
    }

    private static ResponseEntity doThrowable(HttpServletRequest request, HttpServletResponse response, Throwable ex)
        throws Throwable {
        Throwable cause;
        ResponseEntity result = null;
        if ((cause = ex.getCause()) != null) {
            result = doThrowable(request, response, cause);
        }
        if (result == null) {
            RestExceptionHandler handler = RestExceptionEnum.forType(ex.getClass());
            if (handler != null) {
                result = handler.onThrowable(request, response, ex);
            }
        }
        return result;
    }
}
