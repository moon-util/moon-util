package com.moon.processor.model;

import com.moon.processor.utils.Element2;

import javax.lang.model.element.ExecutableElement;

/**
 * @author benshaoye
 */
public class DeclareMethod {

    private final ExecutableElement method;
    private final String name;
    private final String declareType;
    private final String actualType;
    private final boolean declared;

    public DeclareMethod(ExecutableElement method, String declareType, String actualType, boolean declared) {
        this.method = method;
        this.name = Element2.getSimpleName(method);
        this.declareType = declareType;
        this.actualType = actualType;
        this.declared = declared;
    }

    public ExecutableElement getMethod() { return method; }

    public String getName() { return name; }

    public String getDeclareType() { return declareType; }

    public String getActualType() { return actualType; }

    /**
     * 是否是生成的
     *
     * @return true|false
     */
    public boolean isGenerated() { return !isDeclared(); }

    /**
     * 是否是声明的
     *
     * @return true|false
     */
    public boolean isDeclared() { return declared; }
}
