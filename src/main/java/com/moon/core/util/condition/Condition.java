package com.moon.core.util.condition;

/**
 * @author benshaoye
 */
public final class Condition implements Conditional {

    private boolean matched;

    public Condition(boolean matched) { this.matched = matched; }

    @Override
    public boolean isMatched() { return matched; }
}
