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

    /**
     * 默认使用的日志
     */
    static {
        tryImplementation(LoggerUtil::useSlf4jImplementation);
        tryImplementation(LoggerUtil::useLog4jImplementation);
        tryImplementation(LoggerUtil::useLog4j2Implementation);
        tryImplementation(LoggerUtil::useCommonsImplementation);
        tryImplementation(LoggerUtil::useJdk14Implementation);
        tryImplementation(LoggerUtil::useNoLogImplementation);
    }

    /*
     * 可指定使用某种日志，指定方式是全局的
     */

    public static void useSlf4jImplementation() { setImplementation(Slf4jImpl::new); }

    public static void useLog4jImplementation() { setImplementation(Log4jImpl::new); }

    public static void useLog4j2Implementation() { setImplementation(Log4j2Impl::new); }

    public static void useCommonsImplementation() { setImplementation(CommonsLogImpl::new); }

    public static void useJdk14Implementation() { setImplementation(Jdk14LogImpl::new); }

    public static void useNoLogImplementation() { setImplementation(name -> NoLogImpl.IMPL); }

    private LoggerUtil() { noInstanceError(); }

    /**
     * 自动推断日志类，示例：
     * <pre>
     * package com.example;
     *
     * import com.moon.core.util.logger.Logger;
     * import com.moon.core.util.logger.LoggerUtil;
     *
     * public class DemoLogger {
     *     // 自动推断 logger 名 com.example.DemoLogger；
     *     // 由于是静态字段，所以并不会影响运行性能；
     *     // 所有 Logger 字段建议均设为静态字段；
     *     private final static Logger logger = LoggerUtil.getLogger();
     *
     *     // other coding
     * }
     * </pre>
     *
     * @return Logger
     */
    public static Logger getLogger() {
        return getLogger(Thread.currentThread().getStackTrace()[2].getClassName());
    }

    /**
     * 指定类名 logger
     *
     * @param type 指定类
     *
     * @return Logger
     */
    public static Logger getLogger(Class type) { return getLogger(type.getName()); }

    /**
     * 指定类名 logger
     *
     * @param loggerName 指定 logger 名
     *
     * @return Logger
     */
    public static Logger getLogger(String loggerName) { return LOG_CREATOR.apply(loggerName); }
}
