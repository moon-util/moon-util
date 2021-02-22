package com.moon.processing.decl;

import com.moon.accessor.annotation.Provided;
import com.moon.processing.file.*;
import com.moon.processing.holder.TableHolder;
import com.moon.processing.holder.TypeHolder;
import com.moon.processing.util.Logger2;
import com.moon.processing.util.Processing2;
import com.moon.processor.utils.Assert2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
                implMethodForInsert(methodDeclared);
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
        MethodDeclared methodDeclared, JavaMethod implMethod, Map<ColumnDeclared, ParameterDeclared> parameters
    ) {
        if (parameters.isEmpty()) {
            defaultReturning(methodDeclared, implMethod);
            return;
        }
        writeInsertSQL(implMethod, toColumnsJoined(parameters), parameters.size());
        // TODO execute SQL
        defaultReturning(methodDeclared, implMethod);
    }

    private void doImplInsertFields(
        MethodDeclared methodDeclared, JavaMethod implMethod, Map<ColumnDeclared, PropertyDeclared> columnsMap
    ) {
        writeInsertSQL(implMethod, toColumnsJoined(columnsMap), columnsMap.size());
        // TODO execute SQL
        defaultReturning(methodDeclared, implMethod);
    }

    private void doImplInsertParameter(
        MethodDeclared methodDeclared, JavaMethod implMethod, ColumnDeclared column, ParameterDeclared parameterName
    ) {
        Map<ColumnDeclared, ParameterDeclared> parameters = new LinkedHashMap<>();
        parameters.put(column, parameterName);
        doImplInsertParameters(methodDeclared, implMethod, parameters);
    }

    private void doImplInsertModel(MethodDeclared methodDeclared, JavaMethod implMethod, TypeDeclared paramModel) {
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
            doImplInsertFields(methodDeclared, implMethod, columns);
        }
    }

    private void doImplInsertMethodForModel(
        MethodDeclared methodDeclared, ParameterDeclared parameter, JavaMethod implMethod
    ) {
        TypeMirror parameterType = parameter.getParameter().asType();
        TypeElement parameterElem = (TypeElement) types.asElement(parameterType);
        // Logger2.warn("-----------------------------------------------------");
        ExecutableElement method = methodDeclared.getMethod();
        // Logger2.warn(method);
        // Logger2.warn(method.getTypeParameters());
        // Logger2.warn(Generic2.from(methodDeclared.getMethod(), methodDeclared.getThisGenericMap()));
        TypeDeclared paramModel = typeHolder.with(parameterElem);
        if (Objects.equals(paramModel, tableDeclared.getTypeDeclared())) {
            doImplInsertFields(methodDeclared, implMethod, tableDeclared.reduce((col, cols) -> {
                cols.put(col, col.getProperty());
            }, new LinkedHashMap<>()));
        } else {
            doImplInsertModel(methodDeclared, implMethod, paramModel);
        }
    }

    private void writeInsertSQL(JavaMethod implMethod, String columnsJoined, int count) {
        String sql = "INSERT INTO " + toDatabaseSymbol(tableDeclared.getTableName()) +

            " (" + columnsJoined + ") VALUES (" + toPlaceholders(count) + ')';
        implMethod.nextFormatted("{} sql = \"{}\"", implMethod.onImported(String.class), sql);
    }

    private static String toColumnsJoined(Map<ColumnDeclared, ?> columnsMap) {
        int index = 0, length = columnsMap.size();
        String[] columns = new String[length];
        for (ColumnDeclared declared : columnsMap.keySet()) {
            columns[index++] = toDatabaseSymbol(declared.getColumnName());
        }
        return String.join(", ", columns);
    }

    private static String toDatabaseSymbol(String columnName) { return '`' + columnName + '`'; }

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

    private void onProvidedAnnotated(MethodDeclared method, Provided provided) {
        final JavaMethod setter;
        ExecutableElement element = method.getMethod();
        Assert2.assertProvidedMethodParameters(element);
        String fieldName, actualType, paramName;
        if (Test2.isGetterMethod(element)) {
            fieldName = Element2.toPropertyName(element);
            actualType = method.getReturnActualType();
            JavaField field = impl.privateField(fieldName, actualType);
            field.withGetterMethod().returning(fieldName).override();
            setter = field.useSetterMethod();
        } else {
            fieldName = impl.nextVar();
            actualType = method.getReturnActualType();
            String parameterName = Element2.getSimpleName(element);
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

    private JavaMethod publicMethod(MethodDeclared methodDeclared, Consumer<JavaParameters> usingParameters) {
        return impl.publicMethod(methodDeclared.getMethodName(), usingParameters);
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
            String collectionType = COLLECTION_TYPES.get(stringify);
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

    private final static Map<String, String> COLLECTION_TYPES = new HashMap<>();

    static {
        BiConsumer<Class<?>, Class<?>> consumer = (cls1, cls2) -> COLLECTION_TYPES.put(cls1.getCanonicalName(),
            cls2.getCanonicalName());
        consumer.accept(Map.class, HashMap.class);
        consumer.accept(HashMap.class, HashMap.class);
        consumer.accept(TreeMap.class, TreeMap.class);
        consumer.accept(LinkedHashMap.class, LinkedHashMap.class);

        consumer.accept(Set.class, HashSet.class);
        consumer.accept(HashMap.class, HashMap.class);
        consumer.accept(TreeSet.class, TreeSet.class);
        consumer.accept(LinkedHashSet.class, LinkedHashSet.class);

        consumer.accept(List.class, ArrayList.class);
        consumer.accept(ArrayList.class, ArrayList.class);
        consumer.accept(LinkedList.class, LinkedList.class);

        consumer.accept(Collection.class, ArrayList.class);
        consumer.accept(Iterable.class, ArrayList.class);
        consumer.accept(Queue.class, LinkedList.class);
        consumer.accept(Deque.class, LinkedList.class);
    }

    private static String defaultReturningVal(String actualReturnType) {
        if (Test2.isPrimitiveNumber(actualReturnType)) {
            return "0";
        } else if (Test2.isPrimitiveBool(actualReturnType)) {
            return "false";
        } else if (Test2.isPrimitiveChar(actualReturnType)) {
            return "' '";
        }
        return null;
    }
}
