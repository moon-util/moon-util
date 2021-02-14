package com.moon.processing.decl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 *
 * @author benshaoye
 */public class BaseExecutableDeclared {

    private final TypeElement thisElement;

    private final TypeElement enclosingElement;

    private final ExecutableElement executable;

    private final Map<String, GenericDeclared> thisGenericMap;

    private final String thisClassname;

    private final String enclosingClassname;

    public BaseExecutableDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement executable,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        this.thisElement = thisElement;
        this.enclosingElement = enclosingElement;
        this.executable = executable;
        this.thisGenericMap = thisGenericMap;

        this.thisClassname = thisElement.getQualifiedName().toString();
        this.enclosingClassname = enclosingElement.getQualifiedName().toString();
    }

    public TypeElement getThisElement() { return thisElement; }

    public TypeElement getEnclosingElement() { return enclosingElement; }

    public ExecutableElement getExecutable() { return executable; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public String getThisClassname() { return thisClassname; }

    public String getEnclosingClassname() { return enclosingClassname; }
}
