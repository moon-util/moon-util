package com.moon.processor.manager;

/**
 * @author benshaoye
 */
public interface Importable {

    /**
     * 引入类
     *
     * @param classname 类名
     *
     * @return 简化类名
     */
    String onImported(Class<?> classname);

    /**
     * 引入类
     *
     * @param classname 类名
     *
     * @return 简化类名
     */
    String onImported(String classname);
}
