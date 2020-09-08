package com.moon.core.util.logger;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * @author benshaoye
 */
final class Log4j2Impl implements Logger {

    private final org.apache.log4j.Logger logger;

    Log4j2Impl(String loggerName) { logger = LogManager.getLogger(loggerName); }

    @Override
    public boolean isDebugEnabled() { return logger.isDebugEnabled(); }

    @Override
    public void debug(String message) { logger.debug(message); }

    @Override
    public void debug(String message, Throwable t) { logger.debug(message, t); }

    @Override
    public boolean isInfoEnabled() { return logger.isInfoEnabled(); }

    @Override
    public void info(String message) { logger.info(message); }

    @Override
    public void info(String message, Throwable t) { logger.info(message, t); }

    @Override
    public boolean isWarnEnabled() { return logger.isEnabledFor(Level.WARN); }

    @Override
    public void warn(String message) { logger.warn(message); }

    @Override
    public void warn(String message, Throwable t) { logger.warn(message, t); }

    @Override
    public boolean isErrorEnabled() { return logger.isEnabledFor(Level.ERROR); }

    @Override
    public void error(String message) { logger.error(message); }

    @Override
    public void error(String message, Throwable t) { logger.error(message, t); }
}
