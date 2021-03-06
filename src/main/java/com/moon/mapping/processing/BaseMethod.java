package com.moon.mapping.processing;


/**
 * @author benshaoye
 */
abstract class BaseMethod implements TypeGetter {

    /**
     * getter 或 setter 方法名
     */
    private final String methodName;
    /**
     * getter 方法声明返回类型（可能是泛型）
     */
    private final String declareType;
    /**
     * getter 方法实际返回类型（实际类型）
     */
    private final String actualType;
    /**
     * 是否是主动声明的（，）
     * <p>
     * 类的方法都是主动声明的
     * <p>
     * 接口的方法有些 getter、setter 是根据上下文自动生成的
     */
    private final boolean declaration;

    public BaseMethod(String methodName, String declareType, String actualType, boolean declaration) {
        this.methodName = methodName;
        this.declareType = declareType;
        this.actualType = actualType;
        this.declaration = declaration;
    }

    public String getMethodName() { return methodName; }

    @Override
    public String getDeclareType() { return declareType; }

    @Override
    public String getActualType() { return actualType; }

    /**
     * 是否是原始声明的，原始声明的可以被重写
     *
     * @return true/false
     */
    public boolean isDeclaration() { return declaration; }

    /**
     * 是否是自动生成的
     *
     * @return true/false
     */
    public boolean isGenerated() { return !isDeclaration(); }
}
