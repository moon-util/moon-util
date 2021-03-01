package com.moon.processing.accessor;

import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.annotation.ForceModifying;
import com.moon.accessor.meta.JdbcParameters;
import com.moon.accessor.session.JdbcSession;
import com.moon.processing.decl.*;
import com.moon.processing.file.*;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;

/**
 * @author benshaoye
 */
public abstract class BaseImplementor extends AbstractImplementor {

    private final Accessor accessor;
    /**
     * 被注解{@link com.moon.accessor.annotation.Accessor}的类声明
     */
    private final AccessorDeclared accessorDeclared;
    /**
     * 关联的表
     */
    private final TableDeclared tableDeclared;
    /**
     * 关联的实体
     */
    private final TypeDeclared modelDeclared;
    /**
     * 正在生成的类文件
     */
    protected final FileClassImpl impl;

    protected BaseImplementor(
        Holders holders, AccessorDeclared accessorDeclared, FileClassImpl impl
    ) {
        super(holders);
        this.accessor = accessorDeclared.getAccessor();
        this.accessorDeclared = accessorDeclared;
        this.tableDeclared = accessorDeclared.getTableDeclared();
        this.modelDeclared = tableDeclared.getTypeDeclared();
        this.impl = impl;
    }

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ getters
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected final boolean useStrict() { return accessor.useStrict(); }

    /**
     * 被注解{@link com.moon.accessor.annotation.Accessor}的类声明
     */
    protected final AccessorDeclared getAccessorDeclared() { return accessorDeclared; }

    protected final TypeDeclared getAccessorTypeDeclared() { return accessorDeclared.getTypeDeclared(); }

    /**
     * 关联的表
     */
    protected final TableDeclared getTableDeclared() { return tableDeclared; }

    protected final TypeDeclared getModelDeclared() { return modelDeclared; }

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ computed getters
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected final ColumnDeclared getColumnDeclaredByName(String propertyName) {
        return getTableDeclared().getColumnDeclared(propertyName);
    }

    protected final ParameterDeclared getFirstParameter(MethodDeclared methodDecl) {
        return methodDecl.getParameterAt(0);
    }

    protected final JavaParameter getFirstParameter(JavaMethod method) {
        return method.getParameters().getFirstParameter();
    }

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ impl method
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * 实现方法声明
     *
     * @param methodDeclared 方法声明
     *
     * @return 方法 java 实现
     */
    protected final JavaMethod publicMethod(MethodDeclared methodDeclared) {
        List<ParameterDeclared> params = methodDeclared.getParametersDeclared();
        JavaMethod method = impl.publicMethod(methodDeclared.getMethodName(), parameters -> {
            for (ParameterDeclared parameter : params) {
                parameters.add(parameter.getParameterName(), parameter.getActualType());
            }
        }).typeOf(methodDeclared.getReturnActualType());
        defaultReturning(methodDeclared, method);
        return method;
    }

    /**
     * 覆盖实现方法声明
     *
     * @param methodDeclared 方法声明
     *
     * @return 方法 java 实现
     */
    protected final JavaMethod overridePublicMethod(MethodDeclared methodDeclared) {
        return publicMethod(methodDeclared).override();
    }

    /**
     * 实现当前正在处理的方法
     *
     * @return 方法 java 实现
     */
    protected final JavaMethod overrideParsingMethod() { return overridePublicMethod(getParsingMethod()); }

    /**
     * 返回默认值语句
     * <p>
     * 1、基本数据类型返回对应基本值
     * 2、集合返回对应空集合，不返回 null
     * 3、其他对象返回 null
     *
     * @param methodDeclared 方法声明
     * @param method         方法 java 实现
     */
    protected final void defaultReturning(MethodDeclared methodDeclared, JavaMethod method) {
        final String returnActualType = methodDeclared.getReturnActualType();
        String returning = defaultReturningVal(returnActualType);
        if (returning == null) {
            ExecutableElement element = methodDeclared.getMethod();
            TypeMirror returnType = element.getReturnType();
            if (returnType.getKind() == TypeKind.VOID) {
                return;
            }
            TypeElement returnElem = (TypeElement) types().asElement(returnType);
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

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ declare
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected final void writeFormattedSql(JavaMethod implMethod, String sqlTemplate, Object... values) {
        String sql = String2.format(sqlTemplate, values);
        implMethod.nextFormatted("{} sql = \"{}\"", implMethod.onImported(String.class), sql);
    }

    @SuppressWarnings("all")
    protected final void writeDeclareJdbcParameters(JavaMethod implMethod, int capacity) {
        implMethod.nextFormatted("{} parameters = new {}({})",//
            getJdbcParametersImported(), getJdbcParametersImported(), capacity);
    }

    protected final void writeJdbcSessionUpdate(JavaMethod implMethod) {
        writeJdbcSessionExecution(implMethod, "update");
    }

    protected final void writeJdbcSessionInsert(JavaMethod implMethod) {
        writeJdbcSessionExecution(implMethod, "insert");
    }

    protected final void writeJdbcSessionExecution(JavaMethod implMethod, String methodName) {
        String affected = "final int affected = {}.{}(sql, parameters)";
        implMethod.nextBlank().nextFormatted(affected, getJdbcSessionName(), methodName);
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

    private final static String MODIFYING = ForceModifying.class.getCanonicalName();

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ customs
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private JavaField jdbcSession;
    private String tableImported;
    private String modelImported;
    private String paramsTypeImported;
    private MethodDeclared parsingMethod;

    protected final String onImported(String classname) { return impl.onImported(classname); }

    protected final String onImported(Class<?> classname) { return impl.onImported(classname); }

    protected final String getJdbcSessionName() { return getJdbcSession().getFieldName(); }

    protected final JavaField getJdbcSession() {
        if (jdbcSession == null) {
            String fieldName = "jdbcSession";
            String fieldType = JdbcSession.class.getCanonicalName();
            JavaField session = impl.useField(fieldName, v -> {}, fieldType);

            JavaMethod setter = session.useSetterMethod();
            String paramName = getFirstParameter(setter).getName();
            setter.nextFormatted("this.{} = {}", fieldName, paramName, LineScripter::withUnsorted);
            setter.annotationAutowired(true);
            this.jdbcSession = session;
        }
        return jdbcSession;
    }

    protected final String getTableImported() {
        if (tableImported == null) {
            tableImported = onImported(tableDeclared.getTableClassname());
        }
        return tableImported;
    }

    protected final String getModelImported() {
        if (modelImported == null) {
            modelImported = onImported(tableDeclared.getModelClassname());
        }
        return modelImported;
    }

    protected final String getJdbcParametersImported() {
        if (paramsTypeImported == null) {
            paramsTypeImported = onImported(JdbcParameters.class);
        }
        return paramsTypeImported;
    }

    protected final MethodDeclared withParsingMethodDeclared(MethodDeclared parsingMethod) {
        this.parsingMethod = parsingMethod;
        return parsingMethod;
    }

    protected final MethodDeclared getParsingMethod() { return parsingMethod; }
}
