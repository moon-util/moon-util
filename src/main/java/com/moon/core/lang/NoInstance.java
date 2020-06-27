package com.moon.core.lang;

/**
 * 无实例构造器
 *
 * @author benshaoye
 */
@SuppressWarnings("all")
public abstract class NoInstance {

    protected NoInstance() { ThrowUtil.noInstanceError(); }
}
