package com.moon.processing.file;

/**
 * 单行脚本
 *
 * @author benshaoye
 */
public interface LineScripter extends Scripter {

    /**
     * 单行脚本内容
     *
     * @return 脚本内容
     */
    String getLineScript();

    /**
     * 单行长度
     *
     * @return 长度
     */
    int length();

    /**
     * 应用为非有序语句
     */
    void withUnsorted();

    /**
     * 是否是有序的，一般语句要保持声明顺序，以保证语句不出错
     * <p>
     * 但有些语句可以没有作用域上下文依赖，可以不用保持顺序
     *
     * @return 是否有序
     */
    default boolean isSorted() { return true; }
}
