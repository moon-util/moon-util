package com.moon.core.io;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public interface Traveller<T> extends Supplier<List<T>>, List<T> {

    /**
     * 遍历指定目录下的文件
     *
     * @param path
     * @return
     */
    Traveller<T> traverse(String path);

    /**
     * 遍历指定目录下的文件
     *
     * @param path
     * @return
     */
    Traveller<T> traverse(File path);

    /**
     * 初始化或重置
     *
     * @return
     */
    @Override
    void clear();

    /**
     * 获取所有内容
     *
     * @return
     */
    @Override
    default List<T> get() {
        return this;
    }
}
