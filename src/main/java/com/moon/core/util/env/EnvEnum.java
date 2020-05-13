package com.moon.core.util.env;

import com.moon.core.enums.MoonProps;
import com.moon.core.lang.LangUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.ThrowingRunnable;
import com.moon.core.util.function.ThrowingSupplier;

import static java.lang.Boolean.TRUE;

/**
 * @author benshaoye
 */
enum EnvEnum implements Environmental {
    DEVELOPMENT {
        @Override
        boolean isTrue() { return isDevelopment(); }
    },
    PRODUCTION {
        @Override
        boolean isTrue() { return isProduction(); }
    };

    abstract boolean isTrue();

    @Override
    public void run(ThrowingRunnable executor) {
        if (isTrue()) {
            try {
                executor.run();
            } catch (Throwable e) {
                ThrowUtil.runtime(e);
            }
        }
    }

    @Override
    public <T> T getOrDefault(ThrowingSupplier<T> supplier, T defaultValue) {
        try {
            return isTrue() ? supplier.get() : defaultValue;
        } catch (Throwable e) {
            return ThrowUtil.runtime(e);
        }
    }

    @Override
    public <T> T getOrElse(ThrowingSupplier<T> supplier, ThrowingSupplier<T> defaultSupplier) {
        try {
            return (isTrue() ? supplier : defaultSupplier).get();
        } catch (Throwable e) {
            return ThrowUtil.runtime(e);
        }
    }

    @Override
    public <T> T getOrNull(ThrowingSupplier<T> supplier) { return getOrDefault(supplier, null); }

    public final static boolean isProduction() { return production; }

    public final static boolean isDevelopment() { return !production; }

    public final static Environmental current() { return production ? PRODUCTION : DEVELOPMENT; }

    private static final boolean production;

    static {
        production = LangUtil.getOrDefault(() -> !TRUE.toString()
            .equalsIgnoreCase(MoonProps.moon_env_development.value()), true);
    }
}
