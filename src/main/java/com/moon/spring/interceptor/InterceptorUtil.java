package com.moon.spring.interceptor;

import com.moon.core.enums.Placeholder;
import com.moon.spring.annotation.verify.VerifyIdempotent;
import com.moon.spring.annotation.verify.VerifyImgCaptcha;
import com.moon.spring.annotation.verify.VerifySmsCaptcha;
import com.moon.spring.interceptor.verify.VerifyIdempotentInterceptor;
import com.moon.spring.interceptor.verify.VerifyImgCaptchaInterceptor;
import com.moon.spring.interceptor.verify.VerifySmsCaptchaInterceptor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author benshaoye
 */
public final class InterceptorUtil {

    private static final Map<Class, Map<Method, Object>> CACHE = new HashMap<>();

    private static Map<Method, Object> ensureGet(Class type) {
        Map<Method, Object> map = CACHE.get(type);
        if (map == null) {
            Map<Method, Object> nowMap = new HashMap<>(16);
            // 这里并不会频繁执行，用 synchronized 即可
            synchronized (InterceptorUtil.class) {
                map = CACHE.putIfAbsent(type, nowMap);
                map = map == null ? nowMap : map;
            }
        }
        return map;
    }

    /**
     * 获取{@code handler}的指定注解
     *
     * @param method         处理方法
     * @param annotationType 注解类型 class
     * @param <T>            注解类型
     *
     * @return 获取到的注解或 null
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationType) {
        T annotation = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
        if (annotation != null) {
            return annotation;
        }
        Map<Method, Object> cache = ensureGet(annotationType);
        Object cachedVal = cache.get(method);
        Object placeholder = Placeholder.DEFAULT;
        if (cachedVal == placeholder) {
            return null;
        } else if (cachedVal == null) {
            annotation = method.getAnnotation(annotationType);
            if (annotation == null) {
                // 这里并不会频繁执行，用 synchronized 即可
                synchronized (cache) {
                    cache.put(method, placeholder);
                }
                return null;
            }
            // 这里并不会频繁执行，用 synchronized 即可
            synchronized (cache) {
                cache.put(method, annotation);
            }
        } else {
            annotation = (T) cachedVal;
        }
        return annotation;
    }

    /**
     * 获取{@code handler}的指定注解
     *
     * @param handler        处理器
     * @param annotationType 注解类型 class
     * @param <T>            注解类型
     *
     * @return 获取到的注解或 null
     *
     * @see HandlerMethod#getMethodAnnotation(Class)
     */
    public static <T extends Annotation> T getAnnotation(HandlerMethod handler, Class<T> annotationType) {
        return getAnnotation(handler.getMethod(), annotationType);
    }

    /**
     * 获取{@code handler}的指定注解
     *
     * @param handler        处理器
     * @param annotationType 注解类型 class
     * @param <T>            注解类型
     *
     * @return 获取到的注解或 null
     */
    public static <T extends Annotation> T getAnnotation(Object handler, Class<T> annotationType) {
        return handler instanceof HandlerMethod ? getAnnotation((HandlerMethod) handler, annotationType) : null;
    }

    /**
     * 幂等性校验拦截器
     *
     * @param tester 测试是否通过幂等性校验，接收两个参数：request 和 idempotentTokenValue
     *               idempotentTokenValue 是在 request 中携带的参数（或请求头）
     *
     * @return 幂等性校验是否通过
     *
     * @see VerifyIdempotent
     */
    public static HandlerInterceptor verifyIdempotent(BiPredicate<HttpServletRequest, String> tester) {
        return new VerifyIdempotentInterceptor(tester);
    }

    /**
     * 图片验证码校验拦截器
     *
     * @param tester 测试是否通过幂等性校验，接收两个参数：request 和 idempotentTokenValue
     *               idempotentTokenValue 是在 request 中携带的参数（或请求头）
     *
     * @return 幂等性校验是否通过
     *
     * @see VerifyImgCaptcha
     */
    public static HandlerInterceptor verifyImgCaptcha(BiPredicate<HttpServletRequest, String> tester) {
        return new VerifyImgCaptchaInterceptor(tester);
    }

    /**
     * 短信验证码校验拦截器
     *
     * @param tester 测试是否通过幂等性校验，接收两个参数：request 和 idempotentTokenValue
     *               idempotentTokenValue 是在 request 中携带的参数（或请求头）
     *
     * @return 幂等性校验是否通过
     *
     * @see VerifySmsCaptcha
     */
    public static HandlerInterceptor verifySmsCaptcha(BiPredicate<HttpServletRequest, String> tester) {
        return new VerifySmsCaptchaInterceptor(tester);
    }
}
