package com.moon.mapping.processing;

import javax.lang.model.element.ExecutableElement;

/**
 * @author benshaoye
 */
final class BasicMethod extends BaseMethod {

    /**
     * getter 或 setter 方法
     */
    private final ExecutableElement elem;

    public BasicMethod(ExecutableElement element, String declareType, String actualType, boolean declaration) {
        super(ElementUtils.getSimpleName(element), declareType, actualType, declaration);
        this.elem = element;
    }

    public ExecutableElement getElem() { return elem; }
}
