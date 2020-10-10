package com.moon.spring.annotation.verify;

import com.moon.spring.interceptor.InterceptorUtil;
import com.moon.spring.interceptor.verify.VerifyImgCaptchaInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiPredicate;

/**
 * 图片验证码校验
 *
 * @author moonsky
 * @see InterceptorUtil#verifyImgCaptcha(BiPredicate)
 * @see VerifyImgCaptchaInterceptor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyImgCaptcha {

    /**
     * 从{@code request}中获取图片验证码的{@code key}
     *
     * @return
     *
     * @see HttpServletRequest#getParameter(String)
     */
    String value() default "imgCaptcha";

    /**
     * 获取方式
     *
     * @return 获取图片验证码的方式
     */
    FromType fromType() default FromType.PARAMETER;
}
