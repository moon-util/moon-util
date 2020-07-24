package com.moon.poi.excel.table;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * @author moonsky
 */
interface Creator<T> {

    /**
     * 获取方法
     *
     * @param descriptor
     *
     * @return
     */
    Method getMethod(PropertyDescriptor descriptor);
}
