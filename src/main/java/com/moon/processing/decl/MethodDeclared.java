package com.moon.processing.decl;

import com.moon.processing.holder.Holders;
import com.moon.processing.util.String2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

import static com.moon.processing.decl.Generic2.mapToActual;

/**
 * @author benshaoye
 */
public class MethodDeclared extends BaseExecutableDeclared {

    private final String methodName;

    private final String returnDeclaredType;

    private final String returnActualType;

    private final String methodSignature;

    public MethodDeclared(
        Holders holders,
        TypeElement thisElement,
        TypeElement enclosingElement,
        ExecutableElement method,
        TypeDeclared typeDeclared,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(holders, thisElement, enclosingElement, method, typeDeclared, Generic2.from(method, thisGenericMap));
        String methodName = method.getSimpleName().toString();
        this.methodName = methodName;

        String declaredType = method.getReturnType().toString();
        String actualType = mapToActual(getThisGenericMap(), getEnclosingClassname(), declaredType);
        this.returnDeclaredType = declaredType;
        this.returnActualType = actualType;
        this.methodSignature = String2.format("{}({})", methodName, parametersTypesSignature);
    }

    /**
     * 主要用于 lombok 自动生成的 getter/setter
     *
     * @param thisElement
     * @param enclosingElement
     * @param methodName
     * @param returnDeclaredType
     * @param thisGenericMap
     * @param parametersMap
     */
    protected MethodDeclared(
        Holders holders,
        TypeElement thisElement,
        TypeElement enclosingElement,
        TypeDeclared typeDeclared,
        String methodName,
        String returnDeclaredType,
        Map<String, GenericDeclared> thisGenericMap,
        Map<String, String> parametersMap
    ) {
        super(holders, thisElement, enclosingElement, typeDeclared, thisGenericMap, parametersMap);
        this.methodName = methodName;
        this.returnDeclaredType = returnDeclaredType;
        this.returnActualType = mapToActual(getThisGenericMap(), getEnclosingClassname(), returnDeclaredType);
        this.methodSignature = String2.format("{}({})", methodName, parametersTypesSignature);
    }

    public ExecutableElement getMethod() { return super.getExecutable(); }

    public String getMethodName() { return methodName; }

    public String getReturnDeclaredType() { return returnDeclaredType; }

    public String getReturnActualType() { return returnActualType; }

    public String getMethodSignature() { return methodSignature; }

    public String getActualTypeFor(String declareType) {
        return Generic2.mapToActual(getThisGenericMap(), getEnclosingClassname(), declareType);
    }
}
