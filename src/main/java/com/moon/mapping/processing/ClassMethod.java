package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import java.util.Map;

import static com.moon.mapping.processing.GenericUtils.findActualType;

/**
 * 仅保存 getter 或 setter 方法
 *
 * @author moonsky
 */
class ClassMethod extends BaseMethod {

    /**
     * getter 或 setter 方法
     */
    private final ExecutableElement elem;

    ClassMethod(ExecutableElement elem, String declareType, Map<String, GenericModel> generics) {
        this(elem, ElementUtils.getSimpleName(elem), declareType, generics, true);
    }

    private ClassMethod(
        ExecutableElement elem, String methodName, String declareType,//
        Map<String, GenericModel> generics, boolean declaration
    ) { this(elem, methodName, declareType, findActualType(generics, declareType), declaration); }

    ClassMethod(
        ExecutableElement elem, String methodName, String declareType, String actualType, boolean declaration
    ) {
        super(methodName, declareType, actualType, declaration);
        this.elem = elem;
    }

    /*
    getter & setter
     */

    public ExecutableElement getElem() { return elem; }

    public String getFactActualType() {
        return getActualType() == null ? getDeclareType() : getActualType();
    }
}
