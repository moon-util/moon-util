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
    /**
     * 是否是否主动声明的
     */
    private final boolean declared;
    private final boolean lombokGenerated;

    public DeclareMethod(
        ExecutableElement method, String declareType, String actualType, boolean declared, boolean lombokGenerated
    ) { this(method, Element2.getSimpleName(method), declareType, actualType, declared, lombokGenerated); }

    public DeclareMethod(
        ExecutableElement method,
        String name,
        String declareType,
        String actualType,
        boolean declared,
        boolean lombokGenerated
    ) {
        this.method = method;
        this.name = name;
        this.declareType = declareType;
        this.actualType = actualType;
        this.declared = declared;
        this.lombokGenerated = lombokGenerated;
    }

    public ExecutableElement getMethod() { return method; }

    public String getName() { return name; }

    public String getDeclareType() { return declareType; }

    public String getActualType() { return actualType; }

    public boolean isLombokGenerated() { return lombokGenerated; }

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
