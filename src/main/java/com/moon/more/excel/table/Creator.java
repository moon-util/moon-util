package com.moon.more.excel.table;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * @author benshaoye
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
