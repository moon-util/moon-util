package com.moon.processing.accessor;

import com.moon.processing.decl.MethodDeclared;

/**
 * 基于 SQL 注解的实现器
 *
 * @author benshaoye
 */
public interface AnnotatedImplementor {

    /**
     * 实现方法
     *
     * @param methodDeclared 将要实现的方法
     */
    void doImplMethod(MethodDeclared methodDeclared);
}
