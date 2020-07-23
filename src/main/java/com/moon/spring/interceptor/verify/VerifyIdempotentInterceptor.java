package com.moon.spring.interceptor.verify;

import com.moon.spring.annotation.verify.VerifyIdempotent;
import com.moon.spring.interceptor.InterceptorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiPredicate;

/**
 * @author moonsky
 */
public class VerifyIdempotentInterceptor extends BaseVerifyInterceptor {

    public VerifyIdempotentInterceptor() { }

    public VerifyIdempotentInterceptor(BiPredicate<HttpServletRequest, String> tester) { super(tester); }

    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler
    ) {
        VerifyIdempotent verify = InterceptorUtil.getAnnotation(handler, VerifyIdempotent.class);
        return verify == null || getTester().test(request, verify.fromType().apply(request, verify.value()));
    }
}
