package com.moon.processing.accessor;

import com.moon.processing.decl.MethodDeclared;

/**
 * 基于方法声明的实现器
 *
 * @author benshaoye
 */
public interface DeclaredImplementor {

    /**
     * 实现方法
     *
     * @param methodDeclared 将要实现的方法
     */
    void doImplMethod(MethodDeclared methodDeclared);
}
