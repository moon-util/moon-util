package com.moon.spring.interceptor.verify;

import com.moon.spring.annotation.verify.VerifySmsCaptcha;
import com.moon.spring.interceptor.InterceptorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.BiPredicate;

/**
 * 短信验证码
 *
 * @author moonsky
 */
public class VerifySmsCaptchaInterceptor extends BaseVerifyInterceptor {

    public VerifySmsCaptchaInterceptor() { }

    public VerifySmsCaptchaInterceptor(BiPredicate<HttpServletRequest, String> tester) { super(tester); }

    @Override
    public boolean preHandle(
        HttpServletRequest request, HttpServletResponse response, Object handler
    ) {
        VerifySmsCaptcha verify = InterceptorUtil.getAnnotation(handler, VerifySmsCaptcha.class);
        return verify == null || getTester().test(request, verify.fromType().apply(request, verify.value()));
    }
}
