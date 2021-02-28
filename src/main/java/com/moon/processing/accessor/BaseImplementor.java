package com.moon.processing.accessor;

import com.moon.accessor.annotation.Accessor;
import com.moon.accessor.meta.JdbcParameters;
import com.moon.accessor.session.JdbcSession;
import com.moon.processing.decl.*;
import com.moon.processing.file.*;
import com.moon.processing.holder.BaseHolder;
import com.moon.processing.holder.Holders;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author benshaoye
 */
public abstract class BaseImplementor extends BaseHolder {

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
     * 将要生成的类文件
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

    protected final TypeDeclared withTypeDeclared(ParameterDeclared parameter) {
        TypeMirror parameterType = parameter.getParameter().asType();
        Element element = types().asElement(parameterType);
        if (element instanceof TypeElement) {
            return withTypeDeclared((TypeElement) element);
        }
        String parameterActualType = parameter.getActualType();
        return withTypeDeclared(utils().getTypeElement(parameterActualType));
    }

    protected final TypeDeclared withTypeDeclared(TypeElement element) {
        return typeHolder().with(element);
    }

    protected final ParameterDeclared getFirstParameter(MethodDeclared methodDecl) {
        return methodDecl.getParameterAt(0);
    }

    protected final JavaParameter getFirstParameter(JavaMethod method) {
        return method.getParameters().getFirstParameter();
    }

    private JavaField jdbcSession;
    private String tableImported;
    private String modelImported;
    private String paramsTypeImported;

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

    protected final String getJavaParametersImported() {
        if (paramsTypeImported == null) {
            paramsTypeImported = onImported(JdbcParameters.class);
        }
        return paramsTypeImported;
    }
}
