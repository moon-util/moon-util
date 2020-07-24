package com.moon.poi.excel;

/**
 * 属性控件（这个名字是不是有点...）
 *
 * @author moonsky
 */
public interface PropertyControl {

    /**
     * 操作一个对象和一个值
     * @param data
     * @return
     */
    Object control(Object data);
}
