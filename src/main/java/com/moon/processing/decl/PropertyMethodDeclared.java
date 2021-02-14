package com.moon.processing.decl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * 专门描述 setter/getter 方法
 *
 * @author benshaoye
 */
public class PropertyMethodDeclared extends MethodDeclared {

    public PropertyMethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(thisElement, enclosingElement, method, thisGenericMap);
    }
}
