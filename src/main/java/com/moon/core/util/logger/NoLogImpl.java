package com.moon.core.util.logger;

/**
 * @author benshaoye
 */
enum NoLogImpl implements Logger {
    /**
     * 单例
     */
    IMPL;

    @Override
    public boolean isDebugEnabled() { return false; }

    @Override
    public void debug(String message) { }

    @Override
    public void debug(String message, Throwable t) { }

    @Override
    public boolean isInfoEnabled() { return false; }

    @Override
    public void info(String message) { }

    @Override
    public void info(String message, Throwable t) { }

    @Override
    public boolean isWarnEnabled() { return false; }

    @Override
    public void warn(String message) { }

    @Override
    public void warn(String message, Throwable t) { }

    @Override
    public boolean isErrorEnabled() { return false; }

    @Override
    public void error(String message) { }

    @Override
    public void error(String message, Throwable t) { }
}
