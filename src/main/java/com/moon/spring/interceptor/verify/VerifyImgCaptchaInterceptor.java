package com.moon.spring.interceptor.verify;

import com.moon.spring.annotation.verify.VerifyImgCaptcha;
import com.moon.spring.interceptor.InterceptorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiPredicate;

/**
 * @author moonsky
 */
public class VerifyImgCaptchaInterceptor extends BaseVerifyInterceptor {

    public VerifyImgCaptchaInterceptor() { }

    public VerifyImgCaptchaInterceptor(BiPredicate<HttpServletRequest, String> tester) { super(tester); }

    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler
    ) {
        VerifyImgCaptcha verify = InterceptorUtil.getAnnotation(handler, VerifyImgCaptcha.class);
        return verify == null || getTester().test(request, verify.fromType().apply(request, verify.value()));
    }
}
