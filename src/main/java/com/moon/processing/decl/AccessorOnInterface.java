package com.moon.processing.decl;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.file.JavaMethod;
import com.moon.processing.file.JavaParameters;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class AccessorOnInterface extends TypeImported {

    private MethodDeclared parsingDeclared;

    public AccessorOnInterface(FileClassImpl impl, AccessorDeclared declared) { super(impl, declared); }

    public void doGenerate() {
        List<Runnable> runners = new ArrayList<>();
        accessorTypeDeclared.getAllMethodsDeclared().forEach(declared -> {
            this.parsingDeclared = declared;
            ExecutableElement element = declared.getMethod();
            Provided provided = element.getAnnotation(Provided.class);
            if (provided != null) {
                // Provided 有优先权，但要后处理
                runners.add(() -> onProvidedAnnotated(declared, provided));
                return;
            }
            String methodName = Element2.getSimpleName(element);
            if (doParsingOnAnnotated(methodName, element)) {
                // TODO SQL注解
                return;
            }
            // TODO 方法名解析
            doParsingWithDeclared(declared);
        });
        runners.forEach(Runnable::run);
    }

    private boolean doParsingOnAnnotated(String methodName, ExecutableElement element) {
        return false;
    }

    private void doParsingWithDeclared(MethodDeclared methodDeclared) {
        switch (String2.firstWord(methodDeclared.getMethodName()).toLowerCase()) {
            case "insert": {
                new InsertByMethodDeclared().implMethodForInsert(methodDeclared);
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

    /**
     * 从方法声明解析 insert 方法
     */
    private class InsertByMethodDeclared {

        private void implMethodForInsert(MethodDeclared methodDeclared) {
            switch (methodDeclared.getParametersCount()) {
                case 0: {
                    doImplEmptyMethod(methodDeclared);
                    break;
                }
                case 1: {
                    doImplInsertForOnlyParameter(methodDeclared, methodDeclared.getParameterAt(0));
                    break;
                }
                default: {
                    doImplInsertForMultiParameters(methodDeclared);
                    break;
                }
            }
        }

        private void doImplInsertForMultiParameters(MethodDeclared methodDeclared) {
            List<Consumer<JavaParameters>> runners = new ArrayList<>();
            Map<ColumnDeclared, ParameterDeclared> columnsMap = new LinkedHashMap<>();
            for (ParameterDeclared parameter : methodDeclared.getParametersDeclared()) {
                String actualType = parameter.getActualType();
                String parameterName = parameter.getParameterName();
                ColumnDeclared column = tableDeclared.getColumnDeclared(parameterName);
                if (isSamePropertyType(actualType, column.getFieldClass())) {
                    columnsMap.put(column, parameter);
                    runners.add(params -> params.add(parameterName, actualType));
                }
            }
            doImplInsertParameters(methodDeclared, publicMethod(methodDeclared, params -> {
                for (Consumer<JavaParameters> runner : runners) {
                    runner.accept(params);
                }
            }).override().typeOf(methodDeclared.getReturnActualType()), columnsMap);
        }

        private void doImplInsertForOnlyParameter(MethodDeclared methodDeclared, ParameterDeclared parameter) {
            String parameterActualType = parameter.getActualType();
            String parameterName = parameter.getParameterName();
            ColumnDeclared columnDeclared = tableDeclared.getColumnDeclared(parameterName);

            final JavaMethod implMethod = publicMethod(methodDeclared, parameters -> {
                parameters.add(parameterName, parameterActualType);
            }).typeOf(methodDeclared.getReturnActualType()).override();

            if (columnDeclared == null) {
                doImplInsertMethodForModel(methodDeclared, parameter, implMethod);
            } else {
                String columnFieldClass = columnDeclared.getFieldClass();
                String generalizableType = String2.toGeneralizableType(parameterActualType);
                if (Objects.equals(generalizableType, columnFieldClass)) {
                    doImplInsertParameter(methodDeclared, implMethod, columnDeclared, parameter);
                } else if (Test2.isSubtypeOf(generalizableType, columnFieldClass)) {
                    doImplInsertParameter(methodDeclared, implMethod, columnDeclared, parameter);
                } else {
                    doImplInsertMethodForModel(methodDeclared, parameter, implMethod);
                }
            }
        }

        private void doImplInsertParameters(
            MethodDeclared methodDeclared, JavaMethod implMethod, Map<ColumnDeclared, ParameterDeclared> columnsMap
        ) {
            if (columnsMap.isEmpty()) {
                defaultReturning(methodDeclared, implMethod);
                return;
            }
            writeInsertSQL(implMethod, toColumnsJoined(columnsMap), columnsMap.size());
            writeDeclareParameters(implMethod, columnsMap.size());
            for (Map.Entry<ColumnDeclared, ParameterDeclared> columnMap : columnsMap.entrySet()) {
                ParameterDeclared param = columnMap.getValue();
                ColumnDeclared column = columnMap.getKey();
                implMethod.nextFormatted("parameters.add({}, {})",
                    column.getConstColumnRef(getTableImported()),
                    param.getParameterName());
            }
            writeJdbcSessionInsert(implMethod);
            // TODO execute SQL
            defaultReturning(methodDeclared, implMethod);
        }

        private void doImplInsertFields(
            MethodDeclared methodDeclared,
            ParameterDeclared parameter,
            JavaMethod implMethod,
            Map<ColumnDeclared, PropertyDeclared> columnsMap
        ) {
            writeInsertSQL(implMethod, toColumnsJoined(columnsMap), columnsMap.size());
            writeDeclareParameters(implMethod, columnsMap.size());
            for (Map.Entry<ColumnDeclared, PropertyDeclared> columnMap : columnsMap.entrySet()) {
                PropertyDeclared prop = columnMap.getValue();
                ColumnDeclared column = columnMap.getKey();
                implMethod.nextFormatted("parameters.add({}, {})",
                    column.getConstColumnRef(getTableImported()),
                    prop.getReffedGetterScript(parameter.getParameterName()));
            }
            writeJdbcSessionInsert(implMethod);
            implMethod.nextBlank().nextFormatted("{}.insert(sql, parameters)", getJdbcSessionName());
            // TODO execute SQL
            defaultReturning(methodDeclared, implMethod);
        }

        private void writeDeclareParameters(JavaMethod implMethod, int capacity) {
            implMethod.nextFormatted("{} parameters = new {}({})",
                getParamsTypeImported(),
                getParamsTypeImported(),
                capacity);
        }

        private void writeInsertSQL(JavaMethod implMethod, String columnsJoined, int count) {
            String sql = "INSERT INTO " + toDatabaseSymbol(tableDeclared.getTableName()) +

                " (" + columnsJoined + ") VALUES (" + toPlaceholders(count) + ')';
            implMethod.nextFormatted("{} sql = \"{}\"", implMethod.onImported(String.class), sql);
        }

        private void doImplInsertParameter(
            MethodDeclared methodDeclared, JavaMethod implMethod, ColumnDeclared column, ParameterDeclared parameterName
        ) {
            Map<ColumnDeclared, ParameterDeclared> parameters = new LinkedHashMap<>();
            parameters.put(column, parameterName);
            doImplInsertParameters(methodDeclared, implMethod, parameters);
        }

        private void doImplInsertModel(
            MethodDeclared methodDeclared, ParameterDeclared parameter, JavaMethod implMethod, TypeDeclared paramModel
        ) {
            Map<ColumnDeclared, PropertyDeclared> columns = new LinkedHashMap<>();
            Map<String, PropertyDeclared> properties = paramModel.getCopiedProperties();
            for (PropertyDeclared property : properties.values()) {
                if (!property.isReadable()) {
                    continue;
                }
                ColumnDeclared column = tableDeclared.getColumnDeclared(property.getName());
                if (column == null) {
                    continue;
                }
                if (isSamePropertyType(property.getActualType(), column.getFieldClass())) {
                    columns.put(column, property);
                }
            }
            if (columns.isEmpty()) {
                defaultReturning(methodDeclared, implMethod);
            } else {
                doImplInsertFields(methodDeclared, parameter, implMethod, columns);
            }
        }


        private void doImplInsertMethodForModel(
            MethodDeclared methodDeclared, ParameterDeclared parameter, JavaMethod implMethod
        ) {
            TypeMirror parameterType = parameter.getParameter().asType();
            Element parameterElem = types.asElement(parameterType);
            if (parameterElem instanceof TypeElement) {
                TypeElement parameterTypeElem = (TypeElement) types.asElement(parameterType);
                TypeDeclared paramModel = typeHolder.with(parameterTypeElem);
                if (Objects.equals(paramModel, tableDeclared.getTypeDeclared())) {
                    doImplInsertFields(methodDeclared, parameter, implMethod, tableDeclared.reduce((col, cols) -> {
                        cols.put(col, col.getProperty());
                    }, new LinkedHashMap<>()));
                } else {
                    doImplInsertModel(methodDeclared, parameter, implMethod, paramModel);
                }
            } else {
                defaultReturning(methodDeclared, implMethod);
            }
        }
    }

    private void doImplEmptyMethod(MethodDeclared methodDeclared) {
        doImplEmptyMethod(methodDeclared, ps -> {});
    }

    private void doImplEmptyMethod(
        MethodDeclared methodDeclared, Consumer<JavaParameters> usingParameters
    ) {
        JavaMethod implMethod = publicMethod(methodDeclared, usingParameters).override()
            .typeOf(methodDeclared.getReturnActualType());
        defaultReturning(methodDeclared, implMethod);
    }

    private void defaultReturning(MethodDeclared methodDeclared, JavaMethod method) {
        final String returnActualType = methodDeclared.getReturnActualType();
        String returning = defaultReturningVal(returnActualType);
        if (returning == null) {
            ExecutableElement element = methodDeclared.getMethod();
            TypeMirror returnType = element.getReturnType();
            if (returnType.getKind() == TypeKind.VOID) {
                return;
            }
            TypeElement returnElem = (TypeElement) types.asElement(returnType);
            String stringify = Element2.getQualifiedName(returnElem);
            String collectionType = nullableCollectActualType(stringify);
            if (collectionType != null) {
                if (Objects.equals(returnElem.toString(), returnType.toString())) {
                    method.returnFormatted("new {}();", method.onImported(collectionType));
                } else {
                    method.returnFormatted("new {}<>();", method.onImported(collectionType));
                }
            } else {
                method.returning("null");
            }
        } else {
            method.returning(returning);
        }
    }
}
