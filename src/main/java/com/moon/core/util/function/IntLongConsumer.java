package com.moon.core.util.function;

/**
 * @author ZhangDongMin
 * @date 2018/9/11
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
