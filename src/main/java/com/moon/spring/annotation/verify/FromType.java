package com.moon.spring.annotation.verify;

import com.moon.web.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiFunction;

/**
 * @author moonsky
 */
public enum FromType implements BiFunction<HttpServletRequest, String, String> {
    /**
     * 从请求参数获取
     */
    PARAMETER(RequestUtil::param),
    /**
     * 从请求头获取
     */
    HEADER(RequestUtil::header),
    /**
     * 优先从请求头获取，可自动降级为从请求参数获取
     */
    HEADER_PRIORITY((request, name) -> RequestUtil.param(request, name, true));

    private final BiFunction<HttpServletRequest, String, String> getter;

    FromType(BiFunction<HttpServletRequest, String, String> getter) {this.getter = getter;}

    @Override
    public String apply(HttpServletRequest request, String name) { return getter.apply(request, name); }
}
