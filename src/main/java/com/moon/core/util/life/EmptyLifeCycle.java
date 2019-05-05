package com.moon.core.util.life;

/**
 * @author benshaoye
 */
enum EmptyLifeCycle implements BeforeLifeCycle, AfterLifeCycle, AroundLifeCycle, LifeCycle {
    VALUE;

    @Override
    public void before(Object item) { }

    @Override
    public void after(Object item) { }

    @Override
    public void around(Object item, CutPoint point) { }
}
