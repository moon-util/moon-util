package com.moon.processing;

/**
 * @author benshaoye
 */
public interface JavaDeclarable {

    JavaDeclarable NULL = JavaDeclaredNull.NULL;

    /**
     * 返回将要生成类的类全名
     *
     * @return 类全名
     */
    String getClassname();

    /**
     * 返回 java 文件内容
     *
     * @return java 文件内容
     */
    String getJavaContent();
}
