package com.moon.processing.decl;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class MethodDeclared extends BaseExecutableDeclared {

    private final String methodName;

    private final String returnDeclaredType;

    private final String returnActualType;

    public MethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(thisElement, enclosingElement, method, Generic2.from(method, thisGenericMap));
        this.methodName = method.getSimpleName().toString();

        String declaredType = method.getReturnType().toString();
        String actualType = Generic2.mapToActual(getThisGenericMap(), getEnclosingClassname(), declaredType);
        this.returnDeclaredType = declaredType;
        this.returnActualType = actualType;
    }

    public ExecutableElement getMethod() { return super.getExecutable(); }

    public String getMethodName() { return methodName; }

    public String getReturnDeclaredType() { return returnDeclaredType; }

    public String getReturnActualType() { return returnActualType; }
}
