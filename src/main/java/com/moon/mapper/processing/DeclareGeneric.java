package com.moon.mapper.processing;

import java.util.List;

/**
 * @author benshaoye
 */
public class DeclareGeneric {

    /**
     * 泛型声明类型
     */
    private final String declare;
    /**
     * 泛型实际类型
     */
    private final String actual;
    /**
     * 泛型边界
     */
    private final String bound;

    public DeclareGeneric(String declare, String actual, String bound) {
        this.declare = declare;
        this.actual = actual;
        this.bound = bound;
    }

    public String getDeclare() { return declare; }

    public String getActual() { return actual; }

    public String getBound() { return bound; }
}
