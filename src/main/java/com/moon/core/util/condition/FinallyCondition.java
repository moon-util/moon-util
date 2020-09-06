package com.moon.core.util.condition;

import com.moon.core.lang.ref.LazyAccessor;

/**
 * 条件执行，有时候有些代码只在开发环境中执行，有些只在生产环境中执行，可使用这个类
 *
 * @author moonsky
 */
public final class FinallyCondition implements Conditional {

    private final LazyAccessor<Boolean> accessor;

    public FinallyCondition(boolean matched) { this(LazyAccessor.of(matched)); }

    public FinallyCondition(LazyAccessor<Boolean> accessor) { this.accessor = accessor; }

    public static FinallyCondition of(boolean matched) { return new FinallyCondition(matched); }

    public static FinallyCondition of(LazyAccessor<Boolean> accessor) {
        return new FinallyCondition(accessor);
    }

    @Override
    public boolean isMatched() { return accessor.get(); }
}
