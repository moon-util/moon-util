package com.moon.processing.file;

/**
 * @author benshaoye
 */
public class JavaParameter {

    /**
     * 参数类型，类全名/泛型全名
     * <p>
     * java.lang.String, T, E
     */
    private final String paramType;
    private final String name;

    public JavaParameter(String paramType, String name) {
        this.paramType = paramType;
        this.name = name;
    }
}
