package com.moon.processor.parser;

import com.moon.accessor.annotation.Provided;
import com.moon.processor.def.DefAccessor;
import com.moon.processor.def.DefTableField;
import com.moon.processor.def.DefTableModel;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMarked;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.holder.ModelHolder;
import com.moon.processor.model.DeclareGeneric;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.*;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.moon.processor.parser.Access2.onPropertyProvided;
import static javax.lang.model.element.Modifier.*;

/**
 * @author benshaoye
 */
public class AccessorParser {

    private final DefAccessor accessor;
    private final DefTableModel tableModel;
    private final TypeElement thisAccessor;
    private final Map<String, DeclareGeneric> genericMap;

    public AccessorParser(DefAccessor accessor) {
        this.accessor = accessor;
        this.tableModel = accessor.getTableModel();
        thisAccessor = accessor.getThisAccessor();
        this.genericMap = Generic2.from(accessor.getThisAccessor());
    }

    public static DeclJavaFile toAccessorJavaFile(DefAccessor accessor) {
        return new AccessorParser(accessor).getDeclJavaFile();
    }

    private DeclJavaFile getDeclJavaFile() {
        DefAccessor accessor = this.accessor;
        TypeElement elem = this.thisAccessor;
        ElementKind kind = elem.getKind();
        if (kind == ElementKind.CLASS && Test2.isNotAny(elem, ABSTRACT)) {
            if (Imported.isComponent(elem)) {
                return null;
            }
        }
        DeclJavaFile impl = DeclJavaFile.classOf(accessor.getPackageName(), accessor.getSimpleName());

        if (elem.getKind() == ElementKind.INTERFACE) {
            implAccessorForInterface(impl, elem);
        } else if (Test2.isAny(elem, ABSTRACT)) {
            implAccessorForAbstract(impl, elem);
        } else {
            // 普通只注解了 Accessor 而未注解 Component 的类
            // 这里处理生成实现类，就单纯的添加一个 Component/Repository
            // 除此之外还处理一下构造器
            implAccessorForNormal(impl, elem);
        }

        return impl;
    }

    private void implAccessorForInterface(DeclJavaFile impl, TypeElement accessor) {
        String accessorClass = Element2.getQualifiedName(accessor);
        impl.implement(accessorClass).markedOf(DeclMarked::ofRepository);

        for (Element element : accessor.getEnclosedElements()) {
            if (!isImplCapable(element)) {
                continue;
            }
            String methodName = Element2.getSimpleName(element);
            ExecutableElement method = (ExecutableElement) element;
            Provided provided = element.getAnnotation(Provided.class);
            if (provided != null) {
                onPropertyProvided(impl, method, accessorClass, genericMap, provided, indexer);
                continue;
            }
            // TODO 各种自定义注解的情况
            implMethod(methodName, impl, method);
        }
    }

    private void implMethod(String methodName, DeclJavaFile impl, ExecutableElement method) {
        switch (String2.firstWord(methodName).toLowerCase()) {
            case "insert":
                implMethodForInsert(impl, method);
                break;
            case "update":
                implMethodForUpdate(impl, method);
                break;
            case "modify":
                implMethodForModify(impl, method);
                break;
            case "save":
                implMethodForSave(impl, method);
                break;
            case "delete":
            case "remove":
                implMethodForDelete(impl, method);
                break;
            case "find":
            case "read":
            case "query":
            case "fetch":
            case "select":
            case "search":
                implMethodForQuery(impl, method);
                break;
            default:
        }
    }

    private void implMethodForUpdate(DeclJavaFile impl, ExecutableElement method) {}

    private void implMethodForModify(DeclJavaFile impl, ExecutableElement method) {}

    private void implMethodForInsert(DeclJavaFile impl, ExecutableElement method) {
        final TypeElement thisElement = (TypeElement) method.getEnclosingElement();
        List<? extends VariableElement> parameters = method.getParameters();
        final int parameterCount = parameters.size();
        final String methodName = Element2.getSimpleName(method);
        final String actualReturnType = getActualType(thisElement, method.getReturnType());
        if (parameterCount == 0) {
            DeclMethod methodImpl = impl.publicMethod(methodName, DeclParams.of())
                .returnTypeof(actualReturnType)
                .override();
            defaultReturning(methodImpl, method, actualReturnType);
        } else if (parameters.size() == 1) {
            VariableElement parameter = parameters.get(0);
            String parameterName = Element2.getSimpleName(parameter);
            String paramActualType = getActualType(thisElement, parameter.asType());
            DefTableModel tableModel = accessor.getTableModel();
            DefTableField tableField = tableModel.getTableFieldInfo(parameterName);
            DeclParams methodImplParams = DeclParams.of(parameterName, paramActualType);
            @SuppressWarnings("all") DeclMethod methodImpl = impl.publicMethod(methodName, methodImplParams)//
                .returnTypeof(actualReturnType).override();
            if (tableField == null || !Objects.equals(tableField.getPropType(), paramActualType)) {
                ModelHolder modelHolder = accessor.getModelHolder();
                // 可以直接插入一个其他类型实体
                DefTableModel otherModel = modelHolder.get(paramActualType);
                if (otherModel != null) {
                    doImplMethodForInsertEntity(methodImpl, otherModel, method, actualReturnType);
                    return;
                }
                TypeElement insertPojo = Environment2.getUtils().getTypeElement(paramActualType);
                if (insertPojo == null) {
                    throw new IllegalStateException("无法确定 POJO 类型: " + paramActualType);
                }
                // 基于当前实体实体字段映射的其他类
                DeclaredPojo pojo = accessor.getPojoHolder().with(insertPojo, false);
                if (pojo.isEmpty()) {
                    defaultReturning(methodImpl, method, actualReturnType);
                    return;
                }
                Map<String, DeclareProperty> properties = filterUsableTableFields(pojo, tableModel);
                if (properties.isEmpty()) {
                    defaultReturning(methodImpl, method, actualReturnType);
                    return;
                }
                doImplMethodFoInsertFields(methodImpl, tableModel, method, actualReturnType, properties);
            } else if (Objects.equals(paramActualType, tableField.getPropType())) {
                // 参数是对应表的单个字段，这种情况比较特殊，但也允许
                doImplMethodFoInsertFields(methodImpl, tableModel, method, actualReturnType, Collect2.set(parameter));
            } else {
                // 如果单个参数既非某个实体、又非当前实体的某个类型匹配的字段就抛异常
                throw new IllegalStateException("未知字段（字段名以及字段类型应与数据表对应）: " + parameter);
            }
        } else {
            // 参数对应表的多个字段，要求参数名、参数类型与实体中的都要一致
            Access2.assertParametersSameWithTableModel(tableModel, parameters);
            // doImplMethodFoInsertFields(methodImpl, tableModel, method, actualReturnType, new LinkedHashSet<>(parameters));
        }
    }

    private void doImplMethodFoInsertFields(
        DeclMethod method,
        DefTableModel tableModel,
        ExecutableElement methodElem,
        String actualReturnType,
        Set<VariableElement> fields
    ) {
        StringBuilder insert = new StringBuilder().append("INSERT INTO ");
        StringBuilder values = new StringBuilder().append(" VALUES (");

        Indexer indexer = new Indexer();
        insert.append(tableModel.getTableName()).append(" (");
        for (VariableElement field : fields) {
            String simpleName = Element2.getSimpleName(field);
            DefTableField tableField = tableModel.getTableFieldInfo(simpleName);
            if (indexer.gt(0)) {
                insert.append(", ");
                values.append(", ");
            }
            insert.append(tableField.getFieldName());
            values.append("?");
            indexer.getAndIncrement();
        }
        insert.append(")");
        values.append(")");
        insert.append(values);
        String wrapped = String2.strWrapped(insert.toString());
        method.scriptOf("{} sql = {}", method.onImported(String.class), wrapped);
        defaultReturning(method, methodElem, actualReturnType);
    }

    private void doImplMethodFoInsertFields(
        DeclMethod method,
        DefTableModel tableModel,
        ExecutableElement methodElem,
        String actualReturnType,
        Map<String, DeclareProperty> fields
    ) {
        StringBuilder insert = new StringBuilder().append("INSERT INTO ");
        StringBuilder values = new StringBuilder().append(" VALUES (");

        Indexer indexer = new Indexer();
        insert.append(tableModel.getTableName()).append(" (");
        for (Map.Entry<String, DeclareProperty> fieldEntry : fields.entrySet()) {
            DefTableField tableField = tableModel.getTableFieldInfo(fieldEntry.getKey());
            if (indexer.gt(0)) {
                insert.append(", ");
                values.append(", ");
            }
            insert.append(tableField.getFieldName());
            values.append("?");
            indexer.getAndIncrement();
        }
        insert.append(")");
        values.append(")");
        insert.append(values);
        String wrapped = String2.strWrapped(insert.toString());
        method.scriptOf("{} sql = {}", method.onImported(String.class), wrapped);
        defaultReturning(method, methodElem, actualReturnType);
    }

    private void doImplMethodForInsertEntity(
        DeclMethod method, DefTableModel tableModel, ExecutableElement methodElem, String actualReturnType
    ) {
        StringBuilder insert = new StringBuilder().append("INSERT INTO ");
        StringBuilder values = new StringBuilder().append(" VALUES (");

        Indexer indexer = new Indexer();
        insert.append(tableModel.getTableName()).append(" (");
        tableModel.forEachFields((name, field) -> {
            if (indexer.gt(0)) {
                insert.append(", ");
                values.append(", ");
            }
            insert.append(field.getFieldName());
            values.append("?");
            indexer.getAndIncrement();
        });
        insert.append(")");
        values.append(")");
        insert.append(values);
        String wrapped = String2.strWrapped(insert.toString());
        method.scriptOf("{} sql = {}", method.onImported(String.class), wrapped);
        defaultReturning(method, methodElem, actualReturnType);
    }

    private void implMethodForDelete(DeclJavaFile impl, ExecutableElement method) {}

    private void implMethodForSave(DeclJavaFile impl, ExecutableElement method) {}

    private void implMethodForQuery(DeclJavaFile impl, ExecutableElement method) {}

    private final AtomicInteger indexer = new AtomicInteger();

    private void implAccessorForNormal(DeclJavaFile impl, TypeElement accessor) {
    }

    private void implAccessorForAbstract(DeclJavaFile impl, TypeElement accessor) {
    }

    private static void defaultReturning(DeclMethod methodImpl, ExecutableElement method, String actualReturnType) {
        if (Test2.isPrimitiveNumber(actualReturnType)) {
            methodImpl.returning("0");
        } else if (Test2.isPrimitiveBool(actualReturnType)) {
            methodImpl.returning("false");
        } else if (Test2.isPrimitiveChar(actualReturnType)) {
            methodImpl.returning("' '");
        } else if (method.getReturnType().getKind() != TypeKind.VOID) {
            methodImpl.returning("null");
        }
    }

    private Map<TypeElement, String> enclosedClassnameMap;

    private String getActualType(TypeElement declaredClass, TypeMirror type) {
        @SuppressWarnings("all") Map<TypeElement, String> classnameMap = enclosedClassnameMap == null //
                                                                         ? (enclosedClassnameMap = new HashMap<>()) : enclosedClassnameMap;
        String classname = classnameMap.get(declaredClass);
        if (classname == null) {
            classname = Element2.getQualifiedName(declaredClass);
            classnameMap.put(declaredClass, classname);
        }
        return Generic2.mappingToActual(genericMap, classname, type.toString());
    }

    /**
     * 筛选出实体与数据表相对应的字段
     *
     * @param pojo
     * @param tableModel
     *
     * @return
     */
    private static Map<String, DeclareProperty> filterUsableTableFields(DeclaredPojo pojo, DefTableModel tableModel) {
        Map<String, DeclareProperty> correctFields = new LinkedHashMap<>();
        for (Map.Entry<String, DeclareProperty> propertyEntry : pojo.entrySet()) {
            if (tableModel.hasProperty(propertyEntry.getKey())) {
                correctFields.put(propertyEntry.getKey(), propertyEntry.getValue());
            }
        }
        return correctFields;
    }

    private static boolean isImplCapable(Element enclosedElement) {
        // 不是构造器/方法, 跳过
        if (!(enclosedElement instanceof ExecutableElement)) {
            return false;
        }
        // 静态方法不处理、私有方法不处理
        if (Test2.isAny(enclosedElement, STATIC, PRIVATE)) {
            return false;
        }
        ElementKind kind = enclosedElement.getKind();
        if (kind == ElementKind.METHOD) {
            return Test2.isAny(enclosedElement, ABSTRACT);
        }
        return kind == ElementKind.CONSTRUCTOR;
    }
}
