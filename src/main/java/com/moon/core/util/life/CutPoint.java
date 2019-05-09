package com.moon.core.util.life;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.function.ThrowingRunnable;
import com.moon.core.util.function.ThrowingSupplier;

/**
 * @author benshaoye
 */
public enum CutPoint {

    BEFORE(true),
    AFTER(false),
    ;

    private final boolean before;

    CutPoint(boolean before) { this.before = before; }

    public final boolean isBefore() { return before; }

    public final boolean isAfter() { return !before; }

    public final void ifBefore(ThrowingRunnable runner) {
        if (isBefore()) {
            try {
                runner.run();
            } catch (Throwable t) {
                ThrowUtil.doThrow(t);
            }
        }
    }

    public final void ifAfter(ThrowingRunnable runner) {
        if (isAfter()) {
            try {
                runner.run();
            } catch (Throwable t) {
                ThrowUtil.doThrow(t);
            }
        }
    }

    public final <T> T ifBeforeGetOrNull(ThrowingSupplier<T> supplier) {
        if (isBefore()) {
            try {
                return supplier.get();
            } catch (Throwable t) {
                return ThrowUtil.doThrow(t);
            }
        }
        return null;
    }

    public final <T> T ifAfterGetOrNull(ThrowingSupplier<T> supplier) {
        if (isAfter()) {
            try {
                return supplier.get();
            } catch (Throwable t) {
                return ThrowUtil.doThrow(t);
            }
        }
        return null;
    }
}
