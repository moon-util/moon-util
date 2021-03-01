package com.moon.processing.decl;

import com.moon.processing.holder.Holders;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class ConstructorDeclared extends BaseExecutableDeclared {

    private final String constructorSignature;

    public ConstructorDeclared(
        Holders holders,
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement executable,
        TypeDeclared typeDeclared,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(holders, thisElement, enclosingElement, executable, typeDeclared, thisGenericMap);
        this.constructorSignature = String.format("%s", parametersTypesSignature);
    }

    public String getConstructorSignature() { return constructorSignature; }
}
