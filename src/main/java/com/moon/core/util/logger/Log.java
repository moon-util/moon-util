package com.moon.core.util.logger;

/**
 * @author benshaoye
 */
public interface Log {

    /**
     * 是否开启调试级别日志
     *
     * @return true|false
     */
    boolean isDebugEnabled();

    /**
     * 打印调试日志
     *
     * @param message 日志内容
     */
    void debug(String message);

    /**
     * 打印调试日志
     *
     * @param message 日志内容
     * @param t       异常
     */
    void debug(String message, Throwable t);

    /**
     * 是否开启提示信息级别日志
     *
     * @return true|false
     */
    boolean isInfoEnabled();

    /**
     * 打印提示信息日志
     *
     * @param message 日志内容
     */
    void info(String message);

    /**
     * 打印提示信息日志
     *
     * @param message 日志内容
     * @param t       异常
     */
    void info(String message, Throwable t);

    /**
     * 是否开启警告级别日志
     *
     * @return true|false
     */
    boolean isWarnEnabled();

    /**
     * 打印警告日志
     *
     * @param message 日志内容
     */
    void warn(String message);

    /**
     * 打印警告日志
     *
     * @param message 日志内容
     * @param t       异常
     */
    void warn(String message, Throwable t);

    /**
     * 是否开启错误级别日志
     *
     * @return true|false
     */
    boolean isErrorEnabled();

    /**
     * 打印错误日志
     *
     * @param message 日志内容
     */
    void error(String message);

    /**
     * 打印错误日志
     *
     * @param message 日志内容
     * @param t       异常
     */
    void error(String message, Throwable t);
}
