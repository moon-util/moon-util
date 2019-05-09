package com.moon.core.util.life;

/**
 * @author benshaoye
 */
public class LifeCycleImpl<T> implements LifeCycle<T> {

    final static EmptyLifeCycle EMPTY = EmptyLifeCycle.VALUE;

    private final LifeCycle<T> before;
    private final LifeCycle<T> around;
    private final LifeCycle<T> after;

    public LifeCycleImpl() { this(null, null, null); }

    public LifeCycleImpl(BeforeLifeCycle<T> before, AroundLifeCycle<T> around, AfterLifeCycle<T> after) {
        this.before = before == null ? EMPTY : before;
        this.around = around == null ? EMPTY : around;
        this.after = after == null ? EMPTY : after;
    }

    public LifeCycleImpl(LifeCycle<T> before, LifeCycle<T> around, LifeCycle<T> after) {
        this.before = before == null ? EMPTY : before;
        this.around = around == null ? EMPTY : around;
        this.after = after == null ? EMPTY : after;
    }

    @Override
    public void before(T item) { before.before(item); }

    @Override
    public void after(T item) { after.after(item); }

    @Override
    public void around(T item, CutPoint point) { around.around(item, point); }
}
