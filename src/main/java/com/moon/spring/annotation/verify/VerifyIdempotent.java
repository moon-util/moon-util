package com.moon.spring.annotation.verify;

import com.moon.spring.interceptor.InterceptorUtil;
import com.moon.spring.interceptor.verify.VerifyIdempotentInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiPredicate;

/**
 * 幂等性校验
 *
 * @author moonsky
 * @see InterceptorUtil#verifyIdempotent(BiPredicate)
 * @see VerifyIdempotentInterceptor
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyIdempotent {

    /**
     * 从{@code request}中获取{@code token}的{@code key}
     *
     * @return
     *
     * @see HttpServletRequest#getParameter(String)
     */
    String value() default "idempotent";

    /**
     * 获取方式
     *
     * @return 获取幂等性 token 的方式
     */
    FromType fromType() default FromType.PARAMETER;
}
