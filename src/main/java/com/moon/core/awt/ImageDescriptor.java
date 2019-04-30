package com.moon.core.awt;

/**
 * @author benshaoye
 */
interface ImageDescriptor {
    /**
     * 名称
     *
     * @return
     */
    String name();

    /**
     * 扩展名
     *
     * @return
     */
    default String extensionName() {
        return name().replace('_', '.').toLowerCase();
    }

    /**
     * 文件后缀
     *
     * @return
     */
    default String suffix() {
        return '.' + extensionName();
    }
}
