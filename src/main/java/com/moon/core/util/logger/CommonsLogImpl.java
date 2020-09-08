package com.moon.core.util.logger;

import org.apache.commons.logging.LogFactory;

/**
 * @author benshaoye
 */
final class CommonsLogImpl implements Logger {

    private final org.apache.commons.logging.Log logger;

    CommonsLogImpl(String loggerName) { logger = LogFactory.getLog(loggerName); }

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
    public boolean isWarnEnabled() { return logger.isWarnEnabled(); }

    @Override
    public void warn(String message) { logger.warn(message); }

    @Override
    public void warn(String message, Throwable t) { logger.warn(message, t); }

    @Override
    public boolean isErrorEnabled() { return logger.isErrorEnabled(); }

    @Override
    public void error(String message) { logger.error(message); }

    @Override
    public void error(String message, Throwable t) { logger.error(message, t); }
}
