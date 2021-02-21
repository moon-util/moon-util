package com.moon.processing.decl;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.file.JavaField;
import com.moon.processing.file.JavaMethod;
import com.moon.processing.file.LineScripter;
import com.moon.processing.holder.TableHolder;
import com.moon.processor.utils.Assert2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public class AccessorGeneratorForInterface {

    private final TableHolder tableHolder;
    private final TypeElement accessorElement;
    private final String accessorClassname;
    private final AccessorDeclared accessorDeclared;
    private final TypeDeclared accessorTypeDeclared;
    private final FileClassImpl implementation;
    private final Map<String, GenericDeclared> thisGenericMap;
    private MethodDeclared parsingDeclared;

    public AccessorGeneratorForInterface(FileClassImpl implementation, AccessorDeclared declared) {
        TypeDeclared accessorTypeDeclared = declared.getTypeDeclared();
        this.thisGenericMap = accessorTypeDeclared.getGenericDeclaredMap();
        this.accessorClassname = declared.getAccessorClassname();
        this.accessorElement = declared.getAccessorElement();
        this.accessorTypeDeclared = accessorTypeDeclared;
        this.tableHolder = declared.getTableHolder();
        this.implementation = implementation;
        this.accessorDeclared = declared;
    }

    public void doGenerate() {
        List<Runnable> runners = new ArrayList<>();
        accessorTypeDeclared.getAllMethodsDeclared().forEach(declared -> {
            this.parsingDeclared = declared;
            ExecutableElement element = declared.getMethod();
            Provided provided = element.getAnnotation(Provided.class);
            if (provided != null) {
                // Provided 有优先权，但要后处理
                runners.add(() -> onProvidedAnnotated(element, provided));
                return;
            }
            String methodName = Element2.getSimpleName(element);
            if (doParsingOnAnnotated(methodName, element)) {
                // TODO SQL注解
                return;
            }
            // TODO 方法名解析
            doParsingWithDeclared(methodName, element);
        });
        runners.forEach(Runnable::run);
    }

    private boolean doParsingOnAnnotated(String methodName, ExecutableElement element) {
        return false;
    }

    private void doImplInsertEntity() {}

    private void implMethodForInsert(String methodName, ExecutableElement element) {
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters.size() == 1) {
        } else {
        }
    }

    private void doParsingWithDeclared(String methodName, ExecutableElement element) {
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameters == null || parameters.isEmpty()) {
            doImplEmptyMethod(methodName, element);
            return;
        }
        switch (String2.firstWord(methodName).toLowerCase()) {
            case "insert": {
                implMethodForInsert(methodName, element);
                break;
            }
            case "update": {
                // implMethodForUpdate(methodName, element);
                break;
            }
            case "modify": {
                // implMethodForModify(methodName, element);
                break;
            }
            case "save": {
                // implMethodForSave(methodName, element);
                break;
            }
            case "delete":
            case "remove": {
                // implMethodForDelete(methodName, element);
                break;
            }
            case "get": {
                // get 方法，只能且必须返回一行单行数据
                break;
            }
            case "find":
            case "read":
            case "query":
            case "fetch":
            case "select":
            case "search": {
                // implMethodForQuery(methodName, element);
                break;
            }
            case "count": {
                break;
            }
            case "exists":
            case "existing": {
                break;
            }
            default:
        }
    }

    private void onProvidedAnnotated(ExecutableElement element, Provided provided) {
        final JavaMethod setter;
        Assert2.assertProvidedMethodParameters(element);
        String fieldName, fieldType, actualType, paramName;
        if (Test2.isGetterMethod(element)) {
            fieldName = Element2.toPropertyName(element);
            fieldType = Element2.getGetterDeclareType(element);
            actualType = Generic2.mapToActual(thisGenericMap, accessorClassname, fieldType);
            JavaField field = implementation.privateField(fieldName, actualType);
            field.withGetterMethod().returning(fieldName).override();
            setter = field.useSetterMethod();
        } else {
            String parameterName = Element2.getSimpleName(element);
            fieldType = element.getReturnType().toString();
            actualType = Generic2.mapToActual(thisGenericMap, accessorClassname, fieldType);
            fieldName = implementation.nextVar();
            JavaField field = implementation.privateField(fieldName, actualType);
            field.publicMethod(parameterName).override().typeOf(actualType).returning(fieldName);
            setter = field.useMethod(String2.toSetterName(parameterName), params -> {
                params.add(parameterName, actualType);
            });
        }
        paramName = setter.getParameters().getFirstByTypeSimplify(actualType).getName();
        setter.annotationAutowired(provided.required());
        setter.annotationQualifierIfNotBlank(provided.value());
        setter.nextFormatted("this.{} = {}", fieldName, paramName, LineScripter::withUnsorted);
    }

    private void doImplEmptyMethod(String methodName, ExecutableElement element) {
        final String returnType = parsingDeclared.getReturnActualType();
        String returning = String2.defaultReturningVal(element, returnType);
        JavaMethod method = implementation.publicMethod(methodName);
        method.override().typeOf(returnType);
        if (returning != null) {
            method.returning(returning);
        }
    }
}
