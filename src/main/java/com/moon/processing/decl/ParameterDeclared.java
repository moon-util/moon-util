package com.moon.processing.decl;

import com.moon.accessor.annotation.condition.IfMatching;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.Collect2;
import com.moon.processing.util.Element2;
import com.moon.processing.util.Log2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public class ParameterDeclared extends AnnotatedDeclared {

    private final TypeElement thisElement;
    private final TypeElement declaredElement;
    private final ExecutableElement thisExecutable;
    private final VariableElement parameter;
    private final TypeDeclared thisTypeDeclared;
    private final Map<String, GenericDeclared> thisGenericMap;
    private final String thisClassname;
    private final String declaredClassname;
    private final int parameterIndex;
    private final String parameterName;
    private final String declaredType;
    private final String actualType;
    private final String simplifyActualType;

    public ParameterDeclared(
        Holders holders,
        TypeElement thisElement,
        TypeElement declaredElement,
        ExecutableElement thisExecutable,
        VariableElement parameter,
        TypeDeclared thisTypeDeclared,
        int parameterIndex,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(holders);
        this.thisElement = thisElement;
        this.declaredElement = declaredElement;
        this.thisExecutable = thisExecutable;
        this.thisGenericMap = thisGenericMap;
        this.parameter = parameter;
        this.parameterIndex = parameterIndex;
        this.thisTypeDeclared = thisTypeDeclared;
        this.thisClassname = Element2.getQualifiedName(thisElement);
        String declaredClassname = Element2.getQualifiedName(declaredElement);
        String declaredType = parameter.asType().toString();
        String actualType = Generic2.mapToActual(thisGenericMap, declaredClassname, declaredType);
        this.parameterName = Element2.getSimpleName(parameter);
        this.declaredClassname = declaredClassname;
        this.declaredType = declaredType;
        this.actualType = actualType;
        this.simplifyActualType = Generic2.typeSimplify(actualType);
    }



    public final  TypeElement getConditionalAnnotated() {
        List<? extends AnnotationMirror> mirrors = parameter.getAnnotationMirrors();
        if (Collect2.isEmpty(mirrors)) {
            return null;
        }
        String typeSimplify = Generic2.typeSimplify(actualType);

        for (AnnotationMirror mirror : mirrors) {
            DeclaredType annotationType = mirror.getAnnotationType();
            if (Test2.isTypeKind(annotationType, TypeKind.DECLARED)) {
                TypeElement annotationElem = (TypeElement) annotationType.asElement();
                String annotationName = getQualifiedName(annotationElem);
                if (isIfConditionType(annotationName)) {
                    continue;
                }
                IfMatching matching = annotationElem.getAnnotation(IfMatching.class);
                if (matching != null) {
                    return annotationElem;
                }
            }
            Log2.warn("Kind: {}, Type: {}, Elem: {}",
                annotationType.getKind(),
                annotationType,
                annotationType.asElement());
        }
        return null;
    }

    /**
     * 主要用于 lombok 自动生成的 getter/setter
     *
     * @param thisElement
     * @param declaredElement
     * @param declaredType
     * @param declaredName
     * @param parameterIndex
     * @param thisGenericMap
     */
    public ParameterDeclared(
        Holders holders,
        TypeElement thisElement,
        TypeElement declaredElement,
        TypeDeclared thisTypeDeclared,
        String declaredType,
        String declaredName,
        int parameterIndex,
        Map<String, GenericDeclared> thisGenericMap
    ) {
        super(holders);
        this.thisElement = thisElement;
        this.declaredElement = declaredElement;
        this.thisExecutable = null;
        this.thisGenericMap = thisGenericMap;
        this.parameter = null;
        this.thisTypeDeclared = thisTypeDeclared;
        this.parameterIndex = parameterIndex;
        this.thisClassname = Element2.getQualifiedName(thisElement);
        String declaredClassname = Element2.getQualifiedName(declaredElement);
        String actualType = Generic2.mapToActual(thisGenericMap, declaredClassname, declaredType);
        this.parameterName = declaredName;
        this.declaredClassname = declaredClassname;
        this.declaredType = declaredType;
        this.actualType = actualType;
        this.simplifyActualType = Generic2.typeSimplify(actualType);
    }

    public boolean isConditional() { return false; }

    public TypeElement getThisElement() { return thisElement; }

    public TypeElement getDeclaredElement() { return declaredElement; }

    public ExecutableElement getThisExecutable() { return thisExecutable; }

    public VariableElement getParameter() { return parameter; }

    public TypeDeclared getThisTypeDeclared() { return thisTypeDeclared; }

    public Map<String, GenericDeclared> getThisGenericMap() { return thisGenericMap; }

    public String getThisClassname() { return thisClassname; }

    public String getDeclaredClassname() { return declaredClassname; }

    public int getParameterIndex() { return parameterIndex; }

    public String getParameterName() { return parameterName; }

    public String getDeclaredType() { return declaredType; }

    public String getActualType() { return actualType; }

    public String getSimplifyActualType() { return simplifyActualType; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParameterDeclared{");
        sb.append("thisElement=").append(thisElement);
        sb.append(", declaredElement=").append(declaredElement);
        sb.append(", thisExecutable=").append(thisExecutable);
        sb.append(", parameter=").append(parameter);
        sb.append(", thisTypeDeclared=").append(thisTypeDeclared);
        sb.append(", thisGenericMap=").append(thisGenericMap);
        sb.append(", thisClassname='").append(thisClassname).append('\'');
        sb.append(", declaredClassname='").append(declaredClassname).append('\'');
        sb.append(", parameterIndex=").append(parameterIndex);
        sb.append(", parameterName='").append(parameterName).append('\'');
        sb.append(", declaredType='").append(declaredType).append('\'');
        sb.append(", actualType='").append(actualType).append('\'');
        sb.append(", simplifyActualType='").append(simplifyActualType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
