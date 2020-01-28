package com.moon.core.util.function;

/**
 * @author ZhangDongMin
 */
@FunctionalInterface
public interface IntDoubleConsumer {
    /**
     * double array handler
     * @param value current items
     * @param index current getSheet
     */
    void accept(double value, int index);
}
