package com.moon.processing.decl;

import com.moon.processor.utils.String2;

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

    private final String methodSignature;

    public MethodDeclared(
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(thisElement, enclosingElement, method, Generic2.from(method, thisGenericMap));
        String methodName = method.getSimpleName().toString();
        this.methodName = methodName;

        String declaredType = method.getReturnType().toString();
        String actualType = Generic2.mapToActual(getThisGenericMap(), getEnclosingClassname(), declaredType);
        this.returnDeclaredType = declaredType;
        this.returnActualType = actualType;
        this.methodSignature = String2.format("{}({})", methodName, parametersTypesSignature);
    }

    public ExecutableElement getMethod() { return super.getExecutable(); }

    public String getMethodName() { return methodName; }

    public String getReturnDeclaredType() { return returnDeclaredType; }

    public String getReturnActualType() { return returnActualType; }

    public String getMethodSignature() { return methodSignature; }
}
