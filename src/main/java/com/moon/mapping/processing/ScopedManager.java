package com.moon.mapping.processing;

/**
 * 方法级别的作用域管理器
 *
 * @author benshaoye
 */
final class ScopedManager {

    private final ImportManager importManager;

    public ScopedManager(ImportManager importManager) {
        this.importManager = importManager;
    }

    public void onStartScoped() {

    }
}
