package com.moon.processing;

/**
 * @author benshaoye
 */
public interface JavaDeclarable {

    /**
     * 返回将要生成类的类全名
     *
     * @return 类全名
     */
    String getClassname();

    /**
     * 类文件内容
     *
     * @return 类文件内容
     */
    @Override
    String toString();
}
