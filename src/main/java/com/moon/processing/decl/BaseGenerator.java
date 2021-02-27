package com.moon.processing.decl;

import com.moon.accessor.annotation.Provided;
import com.moon.accessor.annotation.SafeModifying;
import com.moon.processing.file.*;
import com.moon.processing.util.Assert2;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.ExecutableElement;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author benshaoye
 */
public abstract class BaseGenerator extends BaseImportable {

    protected final static String MODIFYING = SafeModifying.class.getCanonicalName();


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

    protected static String nullableCollectActualType(String collectionType) {
        return COLLECTION_TYPES.get(collectionType);
    }

    protected static void unsafeUpdateModifying(JavaMethod method) {
        unsafeModifyingWith(method, "update");
    }

    protected static void unsafeDeleteModifying(JavaMethod method) {
        unsafeModifyingWith(method, "delete");
    }

    protected static void unsafeModifyingWith(JavaMethod implMethod, String dml) {
        implMethod.nextScript("// 不安全的 " + dml + " 语句，请添加 where 子句");
        implMethod.nextScript("// 或注解 @" + MODIFYING);
    }

    protected static JavaParameter getFirstParameter(JavaMethod method) {
        return method.getParameters().getFirstParameter();
    }

    protected static JavaParameter getFirstParameterByTypeSimplify(JavaMethod method, String actualType) {
        return method.getParameters().getFirstByTypeSimplify(actualType);
    }

    protected static String defaultReturningVal(String actualReturnType) {
        if (Test2.isPrimitiveNumber(actualReturnType)) {
            return "0";
        } else if (Test2.isPrimitiveBool(actualReturnType)) {
            return "false";
        } else if (Test2.isPrimitiveChar(actualReturnType)) {
            return "' '";
        }
        return null;
    }

    protected static boolean isSamePropertyType(String actualType, String fieldClass) {
        String generalizableType = String2.toGeneralizableType(actualType);
        if (Objects.equals(generalizableType, fieldClass)) {
            return true;
        }
        return Test2.isSubtypeOf(generalizableType, fieldClass);
    }

    protected static String dotClass(String classname) { return classname + ".class"; }

    protected static String toColumnsJoined(Map<ColumnDeclared, ?> columnsMap) {
        int index = 0, length = columnsMap.size();
        String[] columns = new String[length];
        for (ColumnDeclared declared : columnsMap.keySet()) {
            columns[index++] = toDatabaseSymbol(declared.getColumnName());
        }
        return String.join(", ", columns);
    }

    protected static String toDatabaseSymbol(String columnName) { return '`' + columnName + '`'; }

    protected static String toPlaceholders(int count) {
        String[] holders = new String[count];
        Arrays.fill(holders, "?");
        return String.join(", ", holders);
    }

    protected final void onProvidedAnnotated(MethodDeclared method, Provided provided) {
        final JavaMethod setter;
        ExecutableElement element = method.getMethod();
        BaseImplementation impl = getImplementation();
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
        paramName = getFirstParameter(setter).getName();
        // TODO 注解的属性值应该考虑优先值问题
        setter.annotationAutowired(provided.required());
        setter.annotationQualifierIfNotBlank(provided.value());
        setter.nextFormatted("this.{} = {}", fieldName, paramName, LineScripter::withUnsorted);
    }

    /*
     #################################################################################################
     */

    public BaseGenerator(Importer importer) { super(importer); }

    /*
     #################################################################################################
     */

    protected final void writeJdbcSessionInsert(JavaMethod implMethod) {
        writeJdbcSessionExecution(implMethod, "insert");
    }

    protected final void writeJdbcSessionExecution(JavaMethod implMethod, String methodName) {
        implMethod.nextBlank().nextFormatted("int inserted = {}.{}(sql, parameters)", getJdbcSessionName(), methodName);
    }

    /**
     * 获取被引用的 jdbcSession 字段名
     *
     * @return jdbcSession
     */
    protected abstract String getJdbcSessionName();

    /**
     * 文件实现
     *
     * @return 文件实现
     */
    protected abstract BaseImplementation getImplementation();
}
