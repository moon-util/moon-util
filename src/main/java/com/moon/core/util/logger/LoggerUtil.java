package com.moon.core.util.logger;

import java.util.function.Function;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * 日志工具类，默认日志顺序：slf4j、log4j、log4j2、commons-log、no-log
 *
 * @author moonsky
 */
public final class LoggerUtil {

    private final static Function<String, Log> LOG_CREATOR;

    static {
        String classname = LoggerUtil.class.getName();
        Function<String, Log> creator;
        Log log;

        try {
            log = new Slf4jImpl(classname);
            creator = Slf4jImpl::new;
        } catch (Throwable t) {
            creator = null;
            log = null;
        }
        try {
            if (log == null) {
                log = new Log4jImpl(classname);
                creator = Log4jImpl::new;
            }
        } catch (Throwable t) {
            creator = null;
            log = null;
        }
        try {
            if (log == null) {
                log = new Log4j2Impl(classname);
                creator = Log4j2Impl::new;
            }
        } catch (Throwable t) {
            creator = null;
            log = null;
        }
        try {
            if (log == null) {
                log = new CommonsLogImpl(classname);
                creator = CommonsLogImpl::new;
            }
        } catch (Throwable t) {
            creator = null;
            log = null;
        }

        if (log == null) {
            creator = name -> NoLogImpl.IMPL;
        }

        LOG_CREATOR = creator;
    }

    private LoggerUtil() { noInstanceError(); }

    public static Log getLogger() {
        return getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public static Log getLogger(Class type) { return getLogger(type.getName()); }

    public static Log getLogger(String loggerName) { return LOG_CREATOR.apply(loggerName); }
}
