package com.moon.core.util.env;

import com.moon.core.util.function.ThrowingRunnable;
import com.moon.core.util.function.ThrowingSupplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.util.env.EnvEnum.DEVELOPMENT;
import static com.moon.core.util.env.EnvEnum.PRODUCTION;

/**
 * 开发模式和生产模式
 * <p>
 * 通过一个系统参数 moon.production 控制
 * <p>
 * 默认是生产模式，设置 -Dmoon.production=false 后开启开发模式
 * <p>
 * 写这个工具的起因是在开发中，登录时通常会有验证码验证，但是开发就没必要验证了，节省时间精力;
 * 又如：开发和生产有些配置文件和参数是不同的，spring 本身是会支持，但是难免还是会有例外的场景
 * 有时候开发和生产使用的业务逻辑都是不一样（怎么会有这情况？我怎么知道，但是就是遇到了）
 *
 * @author benshaoye
 */
public final class EnvUtil {

    private EnvUtil() { noInstanceError(); }

    public final static Environmental current() { return EnvEnum.current(); }

    public final static boolean isProduction() { return PRODUCTION.isTrue(); }

    public final static boolean isDevelopment() { return DEVELOPMENT.isTrue(); }

    public final static void ifProd(ThrowingRunnable executor) { PRODUCTION.run(executor); }

    public final static void ifDev(ThrowingRunnable executor) { DEVELOPMENT.run(executor); }

    public final static <T> T ifProdOrOther(T prod, T other) { return isProduction() ? prod : other; }

    public final static <T> T ifDevOrOther(T dev, T other) { return isDevelopment() ? dev : other; }

    public final static <T> T ifProdOrDefault(ThrowingSupplier<T> supplier, T defaultValue) {
        return PRODUCTION.getOrDefault(supplier, defaultValue);
    }

    public final static <T> T ifDevOrDefault(ThrowingSupplier<T> supplier, T defaultValue) {
        return DEVELOPMENT.getOrDefault(supplier, defaultValue);
    }

    public final static <T> T ifProdOrElse(ThrowingSupplier<T> supplier, ThrowingSupplier<T> defaultSupplier) {
        return PRODUCTION.getOrElse(supplier, defaultSupplier);
    }

    public final static <T> T ifDevOrElse(ThrowingSupplier<T> supplier, ThrowingSupplier<T> defaultSupplier) {
        return DEVELOPMENT.getOrElse(supplier, defaultSupplier);
    }
}
