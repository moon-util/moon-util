package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import java.util.Map;

/**
 * 仅保存 getter 或 setter 方法
 *
 * @author moonsky
 */
class MethodDetail {

    /**
     * getter 或 setter 方法
     */
    private final ExecutableElement elem;
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

    MethodDetail(ExecutableElement elem, String declareType, Map<String, GenericModel> generics) {
        this(elem, ElementUtils.getSimpleName(elem), declareType, generics);
    }

    MethodDetail(ExecutableElement elem, String methodName, String declareType, Map<String, GenericModel> generics) {
        this(elem, methodName, declareType, findActualType(generics, declareType));
    }

    public MethodDetail(ExecutableElement elem, String methodName, String declareType, String actualType) {
        this.methodName = methodName;
        this.declareType = declareType;
        this.actualType = actualType;
        this.elem = elem;
    }

    private static String findActualType(Map<String, GenericModel> generics, String declareType) {
        GenericModel model = generics.get(declareType);
        return model == null ? null : model.getSimpleFinalType();
    }

    /*
    getter & setter
     */

    public ExecutableElement getElem() { return elem; }

    public String getMethodName() { return methodName; }

    public String getDeclareType() { return declareType; }

    public String getActualType() { return actualType; }

    public String getFactActualType() {
        return getActualType() == null ? getDeclareType() : getActualType();
    }
}
