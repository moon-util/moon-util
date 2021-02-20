package com.moon.processing.file;

/**
 * 声明与{@link Appender}并没有什么不同，
 * 但这个主要是给方法、构造函数体、代码块用的，
 * 用以区别
 *
 * @author benshaoye
 */
public interface Scripter {

    /**
     * 添加 java 脚本内容
     *
     * @param addr JavaAddr
     */
    void addJavaScript(JavaAddr addr);
}
