package com.moon.core.util.condition;

/**
 * 条件执行，有时候有些代码只在开发环境中执行，有些只在生产环境中执行，可使用这个类
 *
 * @author moonsky
 */
public final class Condition implements Conditional {

    private boolean matched;

    public Condition(boolean matched) { this.matched = matched; }

    public static Condition of(boolean matched) { return new Condition(matched); }

    @Override
    public boolean isMatched() { return matched; }
}
