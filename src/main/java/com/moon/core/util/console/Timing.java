package com.moon.core.util.console;

/**
 * @author benshaoye
 */
public interface Timing {
    /**
     * 开始计时
     */
    void time();

    /**
     * 开始计时
     *
     * @param template
     */
    void time(String template);

    /**
     * 计次计时
     */
    void timeNext();

    /**
     * 计次计时
     *
     * @param template
     */
    void timeNext(String template);

    /**
     * 打印当前所经过的时间
     */
    void timeEnd();
}
