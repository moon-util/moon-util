package com.moon.mapper.processing;

/**
 * @author benshaoye
 */
public class DeclareMethod {

    private final String name;
    private final String declareType;
    private final String actualType;
    private final boolean declared;

    public DeclareMethod(String name, String declareType, String actualType, boolean declared) {
        this.name = name;
        this.declareType = declareType;
        this.actualType = actualType;
        this.declared = declared;
    }

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
