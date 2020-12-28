package com.moon.processor;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Completable {

    /**
     * 完成时回调
     */
    void onCompleted();
}
