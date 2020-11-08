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
        this.elem = elem;
        this.declareType = declareType;
        GenericModel model = generics.get(this.declareType);
        this.actualType = model == null ? null : model.getSimpleFinalType();
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MethodDetail{");
        sb.append("elem=").append(elem);
        sb.append(", declareType='").append(declareType).append('\'');
        sb.append(", actualType='").append(actualType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
