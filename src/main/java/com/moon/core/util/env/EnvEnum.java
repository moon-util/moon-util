package com.moon.core.util.env;

import com.moon.core.enums.MoonProps;
import com.moon.core.lang.LangUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.ThrowsRunnable;
import com.moon.core.util.function.ThrowsSupplier;

import static java.lang.Boolean.TRUE;

/**
 * @author benshaoye
 */
enum EnvEnum implements Environmental {
    DEVELOPMENT {
        @Override
        boolean isTrue() {
            return isDevelopment();
        }
    },
    PRODUCTION {
        @Override
        boolean isTrue() {
            return isProduction();
        }
    };

    abstract boolean isTrue();

    @Override
    public void run(ThrowsRunnable executor) {
        if (isTrue()) {
            try {
                executor.run();
            } catch (Throwable e) {
                ThrowUtil.wrapRuntime(e);
            }
        }
    }

    @Override
    public <T> T getOrDefault(ThrowsSupplier<T> supplier, T defaultValue) {
        try {
            return isTrue() ? supplier.get() : defaultValue;
        } catch (Throwable e) {
            return ThrowUtil.wrapRuntime(e);
        }
    }

    @Override
    public <T> T getOrElse(ThrowsSupplier<T> supplier, ThrowsSupplier<T> defaultSupplier) {
        try {
            return (isTrue() ? supplier : defaultSupplier).get();
        } catch (Throwable e) {
            return ThrowUtil.wrapRuntime(e);
        }
    }

    @Override
    public <T> T getOrNull(ThrowsSupplier<T> supplier) {
        return getOrDefault(supplier, null);
    }

    public final static boolean isProduction() {
        return production;
    }

    public final static boolean isDevelopment() {
        return !production;
    }

    public final static Environmental current() {
        return production ? PRODUCTION : DEVELOPMENT;
    }

    private static final boolean production;

    static {
        production = LangUtil.getOrDefault(() -> !TRUE.toString()
            .equalsIgnoreCase(MoonProps.moon_env_development.value()), true);
    }
}
