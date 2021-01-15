package com.moon.processor.file;

import com.moon.processor.holder.Importable;
import com.moon.processor.holder.Importer;

/**
 * @author benshaoye
 */
public interface ImporterAware extends Importable {

    /**
     * 获取{@link Importer}
     *
     * @return Importer
     */
    Importable getImportable();

    /**
     * 自动引入
     *
     * @param classname 类名
     *
     * @return
     */
    @Override
    default String onImported(Class<?> classname) {
        return getImportable().onImported(classname);
    }

    /**
     * 自动引入
     *
     * @param classname 类名
     *
     * @return
     */
    @Override
    default String onImported(String classname) {
        return getImportable().onImported(classname);
    }
}
