package com.moon.spring.web.advice;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author moonsky
 */
public interface RestExceptionHandler {

    /**
     * 处理异常
     *
     * @param request   HttpServletRequest
     * @param response  HttpServletResponse
     * @param throwable Throwable
     *
     * @return ResponseEntity
     *
     * @throws Throwable
     */
    ResponseEntity onThrowable(HttpServletRequest request, HttpServletResponse response, Throwable throwable)
        throws Throwable;

    /**
     * 处理异常
     *
     * @param throwable Throwable
     *
     * @return ResponseEntity
     *
     * @throws Throwable
     */
    ResponseEntity onThrowable(Throwable throwable) throws Throwable;
}
