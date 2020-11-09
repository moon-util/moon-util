package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import java.util.Map;

/**
 * @author moonsky
 */
final class InterMethod extends MethodDetail {

    private final boolean override;

    /**
     * 接口原先声明的方法从这里进入
     *
     * @param elem
     * @param declareType
     * @param generics
     */
    InterMethod(
        ExecutableElement elem, String declareType, Map<String, GenericModel> generics
    ) {
        super(elem, declareType, generics);
        this.override = true;
    }

    /**
     * 对应生成的 getter、setter 方法从这个进入
     * <p>
     * 生成的方法没有{@link ExecutableElement}
     *
     * @param methodName
     * @param declareType
     * @param actualType
     */
    InterMethod(
        String methodName, String declareType, String actualType
    ) {
        super(null, methodName, declareType, actualType);
        this.override = false;
    }

    public boolean isOverride() { return override; }
}
