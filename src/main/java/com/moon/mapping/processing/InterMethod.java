package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import java.util.Map;

/**
 * @author moonsky
 */
final class InterMethod extends ClassMethod {

    /**
     * 是否是覆盖方法
     */
    private final boolean override;

    /**
     * 接口原先声明的方法从这里进入
     *
     * @param elem        方法元素
     * @param declareType 声明类型，可能是泛型
     * @param generics    泛型对应的实际类
     */
    InterMethod(ExecutableElement elem, String declareType, Map<String, GenericModel> generics) {
        super(elem, declareType, generics);
        this.override = true;
    }

    /**
     * 对应生成的 getter、setter 方法从这个进入
     * <p>
     * 生成的方法没有{@link ExecutableElement}
     *
     * @param methodName  方法名
     * @param declareType 声明类型，可能是泛型
     * @param actualType  实际类型
     */
    InterMethod(String methodName, String declareType, String actualType) {
        super(null, methodName, declareType, actualType, false);
        this.override = false;
    }

    @Override
    public boolean isOverride() { return override; }
}
