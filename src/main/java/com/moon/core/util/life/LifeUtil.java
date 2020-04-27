package com.moon.core.util.life;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class LifeUtil {

    private LifeUtil() { noInstanceError(); }

    private static boolean canBefore(Object data, BeforeLifeChain lifeChain) {
        return lifeChain.hasBefore();
    }

    private static boolean canAfter(Object data, AfterLifeChain lifeChain) {
        return lifeChain.hasAfter();
    }

    public static <P> P before(BeforeLifeChain<? super P> beforeLifeChain, P param) {
        return canBefore(param, beforeLifeChain) ? beforeLifeChain.before(param) : param;
    }

    public static <R> R after(AfterLifeChain<? super R> afterLifeChain, R result) {
        return canAfter(result, afterLifeChain) ? afterLifeChain.after(result) : result;
    }

    public static <P> P[] before(BeforeLifeChain<? super P> beforeLifeChain, P... params) {
        if (canBefore(params, beforeLifeChain)) {
            int len = params == null ? 0 : params.length;
            for (int i = 0; i < len; i++) {
                beforeLifeChain.before(params[i]);
            }
        }
        return params;
    }

    public static <R> R[] after(AfterLifeChain<? super R> afterLifeChain, R... results) {
        if (canAfter(results, afterLifeChain)) {
            int len = results == null ? 0 : results.length;
            for (int i = 0; i < len; i++) {
                afterLifeChain.after(results[i]);
            }
        }
        return results;
    }

    public static <P, C extends Iterable<? extends P>> C before(
        BeforeLifeChain<? super P> beforeLifeChain, C params
    ) {
        if (canBefore(params, beforeLifeChain)) {
            for (P param : params) {
                beforeLifeChain.before(param);
            }
        }
        return params;
    }

    public static <R, C extends Iterable<? extends R>> C after(AfterLifeChain<? super R> afterLifeChain, C results) {
        if (canAfter(results, afterLifeChain)) {
            for (R result : results) {
                afterLifeChain.after(result);
            }
        }
        return results;
    }
}
