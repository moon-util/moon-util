package com.moon.core.util.function;

/**
 * @author ZhangDongMin
 */
@FunctionalInterface
public interface IntLongConsumer {
    /**
     * long array handler
     * @param value current data
     * @param index current getSheet
     */
    void accept(long value, int index);
}
