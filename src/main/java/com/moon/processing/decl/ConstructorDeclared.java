package com.moon.processing.decl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class ConstructorDeclared extends BaseExecutableDeclared {

    private final String constructorSignature;

    public ConstructorDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement executable,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(thisElement, enclosingElement, executable, thisGenericMap);
        this.constructorSignature = String.format("%s", parametersTypesSignature);
    }

    public String getConstructorSignature() { return constructorSignature; }
}
