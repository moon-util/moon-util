package com.moon.processor.parser;

import com.moon.accessor.annotation.Provided;
import com.moon.processor.def.DefAccessor;
import com.moon.processor.def.DefTableField;
import com.moon.processor.def.DefTableModel;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMarked;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.model.DeclareGeneric;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.*;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        switch (String2.firstWord(methodName)) {
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
            String actualType = getActualType(thisElement, parameter.asType());
            String parameterName = Element2.getSimpleName(parameter);
            DefTableModel tableModel = accessor.getTableModel();
            DefTableField field = tableModel.getTableFieldInfo(parameterName);
            DeclMethod methodImpl = impl.publicMethod(methodName, DeclParams.of(parameterName, actualType))
                .returnTypeof(actualReturnType)
                .override();
            // 只有一个参数
            if (field == null) {
                TypeElement insertPojo = Environment2.getUtils().getTypeElement(actualType);
                if (insertPojo == null) {
                    throw new IllegalStateException("无法确定 POJO 类型: " + actualType);
                }
                DeclaredPojo pojo = accessor.getPojoHolder().with(insertPojo, false);
                if (pojo.isEmpty()) {
                    defaultReturning(methodImpl, method, actualReturnType);
                    return;
                } else {
                    List<String> insertionProps = pojo.keySet()
                        .stream()
                        .filter(tableModel::hasProperty)
                        .collect(Collectors.toList());
                    if (insertionProps.isEmpty()) {
                        defaultReturning(methodImpl, method, actualReturnType);
                        return;
                    }
                }
                // 参数是对应表的 0 至多个字段
            } else if (Objects.equals(actualType, field.getPropType())) {
                // 参数是对应表的个字段，这种情况比较特殊，但也允许
            } else {

            }

            // TODO
            defaultReturning(methodImpl, method, actualReturnType);
        } else {

        }
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
        Map<TypeElement, String> classnameMap = enclosedClassnameMap == null ? (enclosedClassnameMap = new HashMap<>()) : enclosedClassnameMap;
        String classname = classnameMap.get(declaredClass);
        if (classname == null) {
            classname = Element2.getQualifiedName(declaredClass);
            classnameMap.put(declaredClass, classname);
        }
        return Generic2.mappingToActual(genericMap, classname, type.toString());
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
