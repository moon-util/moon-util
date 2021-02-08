package com.moon.processor.create;

/**
 * @author benshaoye
 */
public class DeclParam {

    /**
     * 参数类型，类全名/泛型全名
     * <p>
     * java.lang.String, T, E
     */
    private final String paramType;
    private final String name;

    public DeclParam(String paramType, String name) {
        this.paramType = paramType;
        this.name = name;
    }
}
