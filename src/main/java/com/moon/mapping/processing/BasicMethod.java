package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;
import java.util.Map;

import static com.moon.mapping.processing.GenericUtils.findActualType;

/**
 * @author benshaoye
 */
class BasicMethod extends BaseMethod {

    /**
     * getter 或 setter 方法
     */
    private final ExecutableElement elem;

    public BasicMethod(ExecutableElement element, String declareType, String actualType, boolean declaration) {
        super(ElementUtils.getSimpleName(element), declareType, actualType, declaration);
        this.elem = element;
    }

    public ExecutableElement getElem() { return elem; }

    BasicMethod(ExecutableElement elem, String declareType, Map<String, GenericModel> generics) {
        this(elem, ElementUtils.getSimpleName(elem), declareType, generics, true);
    }

    private BasicMethod(
        ExecutableElement elem, String methodName, String declareType,//
        Map<String, GenericModel> generics, boolean declaration
    ) { this(elem, methodName, declareType, findActualType(generics, declareType), declaration); }

    BasicMethod(
        ExecutableElement elem, String methodName, String declareType, String actualType, boolean declaration
    ) {
        super(methodName, declareType, actualType, declaration);
        this.elem = elem;
    }
}
