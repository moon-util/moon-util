package com.moon.more.excel.table;

import java.lang.reflect.Method;

/**
 * @author benshaoye
 */
class MarkMethod extends Marked<Method> {

    protected MarkMethod(String name, Class type, Method member) {
        super(name, type, member);
    }
}
