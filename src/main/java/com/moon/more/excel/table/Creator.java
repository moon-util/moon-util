package com.moon.more.excel.table;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author benshaoye
 */
interface Creator<T> {

    Method getMethod(PropertyDescriptor descriptor);
}
