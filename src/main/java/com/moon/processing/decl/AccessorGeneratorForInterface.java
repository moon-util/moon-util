package com.moon.processing.decl;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.file.JavaClassFile;
import com.moon.processing.file.JavaField;
import com.moon.processing.file.JavaMethod;
import com.moon.processor.utils.Assert2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

/**
 * @author benshaoye
 */
public class AccessorGeneratorForInterface {

    private final TypeElement accessorElement;
    private final String accessorClassname;
    private final AccessorDeclared accessorDeclared;
    private final TypeDeclared accessorTypeDeclared;
    private final JavaClassFile implementation;
    private final VarHelper varHelper;
    private final Map<String, GenericDeclared> thisGenericMap;

    public AccessorGeneratorForInterface(JavaClassFile implementation, AccessorDeclared declared) {
        TypeDeclared accessorTypeDeclared = declared.getTypeDeclared();
        this.thisGenericMap = accessorTypeDeclared.getGenericDeclaredMap();
        this.accessorClassname = declared.getAccessorClassname();
        this.accessorElement = declared.getAccessorElement();
        this.accessorTypeDeclared = accessorTypeDeclared;
        this.varHelper = declared.getVarHelper();
        this.implementation = implementation;
        this.accessorDeclared = declared;
    }

    public void doGenerate() {
        accessorTypeDeclared.getAllMethodsDeclared().forEach(declared -> {
            ExecutableElement element = declared.getMethod();
            Provided provided = element.getAnnotation(Provided.class);
            if (provided != null) {
                onProvidedAnnotated(element, provided);
            }
        });
    }

    private void onProvidedAnnotated(ExecutableElement element, Provided provided) {
        Assert2.assertProvidedMethodParameters(element);
        String fieldName, fieldType, actualType;
        JavaMethod setterMethod;
        if (Test2.isGetterMethod(element)) {
            fieldName = Element2.toPropertyName(element);
            fieldType = Element2.getGetterDeclareType(element);
            actualType = Generic2.mapToActual(thisGenericMap, accessorClassname, fieldType);
            JavaField field = implementation.privateField(fieldName, actualType);
            field.withGetterMethod().returning(fieldName).override();
            String setField = String2.format("this.{} = {}", fieldName, fieldName);
            setterMethod = field.withSetterMethod().nextScript(setField);
        } else {
            String simpleName = Element2.getSimpleName(element);
            fieldType = element.getReturnType().toString();
            actualType = Generic2.mapToActual(thisGenericMap, accessorClassname, fieldType);
            fieldName = varHelper.next(implementation.getDeclaredFieldsName());
            JavaField field = implementation.privateField(fieldName, actualType);
            field.withPublicMethod(simpleName).override().typeOf(actualType).returning(fieldName);
            setterMethod = field.withPublicMethod(String2.toSetterName(simpleName), params -> {
                params.add(simpleName, actualType);
            }).nextScript(String2.format("this.{} = {}", fieldName, simpleName));
        }
        setterMethod.annotationAutowired(provided.required());
        setterMethod.annotationQualifierIfNotBlank(provided.value());
    }
}
