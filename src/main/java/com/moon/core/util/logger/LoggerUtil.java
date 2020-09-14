package com.moon.core.util.logger;

import java.util.function.Function;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * 日志工具类，提供统一的日志包装{@link Logger}，可咋不同场景下自动获取相应的实际日志库
 * <p>
 * 默认日志顺序：slf4j、log4j、log4j2、commons-log、no-log
 *
 * @author moonsky
 */
public final class LoggerUtil {

    private static Function<String, Logger> LOG_CREATOR;

    private static void tryImplementation(Runnable runner) {
        if (LOG_CREATOR == null) {
            try {
                runner.run();
            } catch (Throwable ignored) {
            }
        }
    }

    private static void setImplementation(Function<String, Logger> creator) {
        try {
            creator.apply(LoggerUtil.class.getName());
            LOG_CREATOR = creator;
        } catch (Throwable t) {
            LOG_CREATOR = null;
            throw new IllegalStateException(t);
        }
    }

    static {
        tryImplementation(LoggerUtil::useSlf4jImplementation);
        tryImplementation(LoggerUtil::useLog4jImplementation);
        tryImplementation(LoggerUtil::useLog4j2Implementation);
        tryImplementation(LoggerUtil::useCommonsImplementation);
        tryImplementation(LoggerUtil::useJdk14Implementation);
        tryImplementation(LoggerUtil::useNoLogImplementation);
    }

    public static void useSlf4jImplementation() { setImplementation(Slf4jImpl::new); }

    public static void useLog4jImplementation() { setImplementation(Log4jImpl::new); }

    public static void useLog4j2Implementation() { setImplementation(Log4j2Impl::new); }

    public static void useCommonsImplementation() { setImplementation(CommonsLogImpl::new); }

    public static void useJdk14Implementation() { setImplementation(Jdk14LogImpl::new); }

    public static void useNoLogImplementation() { setImplementation(name -> NoLogImpl.IMPL); }

    private LoggerUtil() { noInstanceError(); }

    public static Logger getLogger() {
        return getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    public static Logger getLogger(Class type) { return getLogger(type.getName()); }

    public static Logger getLogger(String loggerName) { return LOG_CREATOR.apply(loggerName); }
}
