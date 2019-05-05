package com.moon.core.util.life;

import com.moon.core.lang.ThrowUtil;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public final class LifeUtil {

    private LifeUtil() { ThrowUtil.noInstanceError(); }

    final static LifeCycle EMPTY = EmptyLifeCycle.VALUE;

    final static <T> T before(T value, LifeCycle<T> before) {
        value = before.computeWhenCutIn(value);
        before.before(value);
        before.around(value, CutPoint.BEFORE);
        return value;
    }

    final static <T> void after(T value, LifeCycle<T> after) {
        after.around(value, CutPoint.AFTER);
        after.after(value);
    }

    /**
     * 执行
     *
     * @param value
     * @param runner
     * @param cycle
     * @param <T>
     */
    public final static <T> void runAccept(T value, Consumer<T> runner, LifeCycle<T> cycle) {
        cycle = cycle == null ? EMPTY : cycle;
        value = before(value, cycle);
        runner.accept(value);
        after(value, cycle);
    }

    /**
     * 执行
     *
     * @param value
     * @param runner
     * @param cycle
     * @param <T>
     * @param <R>
     * @return
     */
    public final static <T, R> R runApply(T value, Function<T, R> runner, LifeCycle<T> cycle) {
        cycle = cycle == null ? EMPTY : cycle;
        value = before(value, cycle);
        R ret = runner.apply(value);
        after(value, cycle);
        return ret;
    }
}
