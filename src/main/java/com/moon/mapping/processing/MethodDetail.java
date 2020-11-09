package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import java.util.Map;

/**
 * 仅保存 getter 或 setter 方法
 *
 * @author moonsky
 */
final class MethodDetail {

    /**
     * getter 或 setter 方法
     */
    private final ExecutableElement elem;
    /**
     * getter 方法声明返回类型（可能是泛型）
     */
    private final String declareType;
    /**
     * getter 方法实际返回类型（实际类型）
     */
    private final String actualType;

    MethodDetail(ExecutableElement elem, String declareType, Map<String, GenericModel> generics) {
        GenericModel model = generics.get(declareType);
        this.actualType = model == null ? null : model.getSimpleFinalType();
        this.declareType = declareType;
        this.elem = elem;
    }

    /*
    getter & setter
     */

    public ExecutableElement getElem() { return elem; }

    public String getDeclareType() { return declareType; }

    public String getActualType() { return actualType; }

    public String getFactActualType() {
        return getActualType() == null ? getDeclareType() : getActualType();
    }
}
