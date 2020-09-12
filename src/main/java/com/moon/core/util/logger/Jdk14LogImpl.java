package com.moon.core.util.logger;

import java.util.logging.Level;

/**
 * @author benshaoye
 */
public final class Jdk14LogImpl implements Logger {

    private final java.util.logging.Logger logger;

    public Jdk14LogImpl(String loggerName) {
        logger = java.util.logging.Logger.getLogger(loggerName);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String message) {
        logger.fine(message);
    }

    @Override
    public void debug(String message, Throwable t) {
        logger.fine(message + '\n' + t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.info(message + '\n' + t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.warning(message + '\n' + t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.severe(message + '\n' + t);
    }
}
