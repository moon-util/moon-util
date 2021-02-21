package com.moon.processing.decl;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.file.*;
import com.moon.processing.holder.TableHolder;
import com.moon.processing.holder.TypeHolder;
import com.moon.processing.util.Processing2;
import com.moon.processor.utils.Assert2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class AccessorGeneratorForInterface {

    private final TypeHolder typeHolder;
    private final TableHolder tableHolder;
    private final TypeElement accessorElement;
    private final String accessorClassname;
    private final AccessorDeclared accessorDeclared;
    private final TypeDeclared accessorTypeDeclared;
    private final TableDeclared tableDeclared;
    private final FileClassImpl impl;
    private final Map<String, GenericDeclared> thisGenericMap;
    private final Elements utils;
    private final Types types;
    private MethodDeclared parsingDeclared;

    public AccessorGeneratorForInterface(FileClassImpl impl, AccessorDeclared declared) {
        TypeDeclared accessorTypeDeclared = declared.getTypeDeclared();
        this.thisGenericMap = accessorTypeDeclared.getGenericDeclaredMap();
        this.accessorClassname = declared.getAccessorClassname();
        this.accessorElement = declared.getAccessorElement();
        this.accessorTypeDeclared = accessorTypeDeclared;
        this.tableHolder = declared.getTableHolder();
        this.typeHolder = declared.getTypeHolder();
        this.tableDeclared = declared.getTableDeclared();
        this.impl = impl;
        this.accessorDeclared = declared;
        this.utils = Processing2.getUtils();
        this.types = Processing2.getTypes();
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

    private void doParsingWithDeclared(String methodName, ExecutableElement element) {
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

    private void implMethodForInsert(String methodName, ExecutableElement element) {
        List<? extends VariableElement> parameters = element.getParameters();
        switch (parameters.size()) {
            case 0:
                doImplEmptyMethod(methodName, element);
                break;
            case 1:
                doImplInsertForOnlyParameter(methodName, parameters.get(0));
                break;
            default:
                doImplInsertForMultiParameters(methodName, parameters);
                break;
        }
    }

    private void doImplInsertForMultiParameters(String methodName, List<? extends VariableElement> parameters) {
        List<Consumer<JavaParameters>> runners = new ArrayList<>();
        Map<ColumnDeclared, String> columnsMap = new LinkedHashMap<>();
        for (VariableElement parameter : parameters) {
            String declareType = parameter.asType().toString();
            String actualType = parsingDeclared.getActualTypeFor(declareType);
            String parameterName = parameter.getSimpleName().toString();
            ColumnDeclared column = tableDeclared.getColumnDeclared(parameterName);
            if (isSamePropertyType(actualType, column.getFieldClass())) {
                columnsMap.put(column, parameterName);
                runners.add(params -> params.add(parameterName, actualType));
            }
        }
        doImplInsertParameters(impl.publicMethod(methodName, params -> {
            for (Consumer<JavaParameters> runner : runners) {
                runner.accept(params);
            }
        }).typeOf(parsingDeclared.getReturnActualType()).override(), columnsMap);
    }

    private void doImplInsertForOnlyParameter(String methodName, VariableElement parameter) {
        TypeMirror parameterType = parameter.asType();
        String parameterActualType = parsingDeclared.getActualTypeFor(parameterType.toString());
        String parameterName = Element2.getSimpleName(parameter);
        ColumnDeclared columnDeclared = tableDeclared.getColumnDeclared(parameterName);
        final JavaMethod implMethod = impl.publicMethod(methodName, parameters -> {
            parameters.add(parameterName, parameterActualType);
        }).typeOf(parsingDeclared.getReturnActualType());
        if (columnDeclared == null) {
            // 自定义属性隔离容器
            TypeElement parameterElem = (TypeElement) types.asElement(parameterType);
            TypeDeclared paramModel = typeHolder.with(parameterElem);
            if (Objects.equals(paramModel, tableDeclared.getTypeDeclared())) {
                doImplInsertEntity(implMethod);
            } else {
                doImplInsertModel(implMethod, paramModel);
            }
        } else {
            String columnFieldClass = columnDeclared.getFieldClass();
            String generalizableType = String2.toGeneralizableType(parameterActualType);
            if (Objects.equals(generalizableType, columnFieldClass)) {
                // 属性类型
                doImplInsertParameter(implMethod, columnDeclared, parameterName);
            } else if (Test2.isSubtypeOf(generalizableType, columnFieldClass)) {
                // 属性子类型
                doImplInsertParameter(implMethod, columnDeclared, parameterName);
            } else {
                // 自定义属性隔离容器
                TypeElement parameterElem = (TypeElement) types.asElement(parameterType);
                TypeDeclared paramModel = typeHolder.with(parameterElem);
                if (Objects.equals(paramModel, tableDeclared.getTypeDeclared())) {
                    doImplInsertEntity(implMethod);
                } else {
                    doImplInsertModel(implMethod, paramModel);
                }
            }
        }
    }

    private void doImplInsertParameters(JavaMethod implMethod, Map<ColumnDeclared, String> parameters) {
        if (parameters.isEmpty()) {
            defaultReturning(implMethod, parsingDeclared.getMethod());
            return;
        }
        writeInsertSQL(implMethod, toColumnsJoined(parameters), parameters.size());
        // TODO execute SQL
        defaultReturning(implMethod, parsingDeclared.getMethod());
    }

    private void doImplInsertFields(
        JavaMethod implMethod, Map<ColumnDeclared, PropertyDeclared> columnsMap
    ) {
        writeInsertSQL(implMethod, toColumnsJoined(columnsMap), columnsMap.size());
        // TODO execute SQL
        defaultReturning(implMethod, parsingDeclared.getMethod());
    }

    private void doImplInsertParameter(JavaMethod implMethod, ColumnDeclared column, String parameterName) {
        Map<ColumnDeclared, String> parameters = new LinkedHashMap<>();
        parameters.put(column, parameterName);
        doImplInsertParameters(implMethod, parameters);
    }

    private void doImplInsertModel(JavaMethod implMethod, TypeDeclared paramModel) {
        Map<ColumnDeclared, PropertyDeclared> columns = new LinkedHashMap<>();
        Map<String, PropertyDeclared> properties = paramModel.getCopiedProperties();
        for (PropertyDeclared property : properties.values()) {
            if (!property.isReadable()) {
                continue;
            }
            ColumnDeclared column = tableDeclared.getColumnDeclared(property.getName());
            if (isSamePropertyType(property.getActualType(), column.getFieldClass())) {
                columns.put(column, property);
            }
        }
        if (columns.isEmpty()) {
            defaultReturning(implMethod, parsingDeclared.getMethod());
        } else {
            doImplInsertFields(implMethod, columns);
        }
    }

    private void doImplInsertEntity(JavaMethod implMethod) {
        doImplInsertFields(implMethod, tableDeclared.reduce((col, cols) -> {
            cols.put(col, col.getProperty());
        }, new LinkedHashMap<>()));
    }

    private void writeInsertSQL(JavaMethod implMethod, String columnsJoined, int count) {
        String sql = "INSERT INTO " + '`' + tableDeclared.getTableName() + "` " +

            '(' + columnsJoined + ") VALUES " +

            '(' + toPlaceholders(count) + ')';
        implMethod.nextFormatted("{} sql = \"{}\"", implMethod.onImported(String.class), sql);
    }

    private static String toColumnsJoined(Map<ColumnDeclared, ?> columnsMap) {
        return columnsMap.keySet().stream().map(col -> toTableColumn(col.getColumnName()))
            .collect(Collectors.joining(", "));
    }

    private static String toTableColumn(String columnName) { return '`' + columnName + '`'; }

    private static String toPlaceholders(int count) {
        String[] holders = new String[count];
        Arrays.fill(holders, "?");
        return String.join(", ", holders);
    }

    private static boolean isSamePropertyType(String actualType, String fieldClass) {
        String generalizableType = String2.toGeneralizableType(actualType);
        if (Objects.equals(generalizableType, fieldClass)) {
            return true;
        }
        return Test2.isSubtypeOf(generalizableType, fieldClass);
    }

    private void onProvidedAnnotated(ExecutableElement element, Provided provided) {
        final JavaMethod setter;
        Assert2.assertProvidedMethodParameters(element);
        String fieldName, fieldType, actualType, paramName;
        if (Test2.isGetterMethod(element)) {
            fieldName = Element2.toPropertyName(element);
            fieldType = Element2.getGetterDeclareType(element);
            actualType = Generic2.mapToActual(thisGenericMap, accessorClassname, fieldType);
            JavaField field = impl.privateField(fieldName, actualType);
            field.withGetterMethod().returning(fieldName).override();
            setter = field.useSetterMethod();
        } else {
            String parameterName = Element2.getSimpleName(element);
            fieldType = element.getReturnType().toString();
            actualType = Generic2.mapToActual(thisGenericMap, accessorClassname, fieldType);
            fieldName = impl.nextVar();
            JavaField field = impl.privateField(fieldName, actualType);
            field.publicMethod(parameterName).override().typeOf(actualType).returning(fieldName);
            setter = field.useMethod(String2.toSetterName(parameterName), params -> {
                params.add(parameterName, actualType);
            });
        }
        paramName = setter.getParameters().getFirstByTypeSimplify(actualType).getName();
        // TODO 注解的属性值应该考虑优先值问题
        setter.annotationAutowired(provided.required());
        setter.annotationQualifierIfNotBlank(provided.value());
        setter.nextFormatted("this.{} = {}", fieldName, paramName, LineScripter::withUnsorted);
    }

    private void doImplEmptyMethod(String methodName, ExecutableElement element) {
        doImplEmptyMethod(methodName, element, ps -> {});
    }

    private void doImplEmptyMethod(
        String methodName, ExecutableElement element, Consumer<JavaParameters> usingParameters
    ) {
        final String returnType = parsingDeclared.getReturnActualType();
        JavaMethod method = impl.publicMethod(methodName, usingParameters).typeOf(returnType);
        defaultReturning(method, element);
    }

    private void defaultReturning(JavaMethod method, ExecutableElement element) {
        final String returnType = parsingDeclared.getReturnActualType();
        defaultReturning(method.override(), String2.defaultReturningVal(element, returnType));
    }

    private static void defaultReturning(JavaMethod method, String returning) {
        if (returning != null) {
            method.returning(returning);
        }
    }
}
