package com.moon.spring.interceptor.verify;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author moonsky
 */
public abstract class BaseVerifyInterceptor implements HandlerInterceptor {

    private final BiPredicate<HttpServletRequest, String> tester;

    public BaseVerifyInterceptor() { this((request, s) -> true); }

    public BaseVerifyInterceptor(Predicate<String> tester) { this(((request, value) -> tester.test(value))); }

    public BaseVerifyInterceptor(BiPredicate<HttpServletRequest, String> tester) { this.tester = tester; }

    public BiPredicate<HttpServletRequest, String> getTester() { return tester; }
}
