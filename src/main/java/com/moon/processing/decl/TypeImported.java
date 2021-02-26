package com.moon.processing.decl;

import com.moon.accessor.meta.JdbcParameters;
import com.moon.accessor.session.JdbcSession;
import com.moon.processing.file.*;
import com.moon.processing.holder.TableHolder;
import com.moon.processing.holder.TypeHolder;
import com.moon.processing.util.Processing2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
abstract class TypeImported extends BaseGenerator {

    protected final TypeHolder typeHolder;
    protected final TableHolder tableHolder;
    protected final TypeElement accessorElement;
    protected final String accessorClassname;
    protected final AccessorDeclared accessorDeclared;
    protected final TypeDeclared accessorTypeDeclared;
    protected final TableDeclared tableDeclared;
    protected final FileClassImpl impl;

    protected final Map<String, GenericDeclared> thisGenericMap;
    protected final Elements utils;
    protected final Types types;

    private JavaField jdbcSession;
    private String tableImported;
    private String modelImported;
    private String paramsTypeImported;

    public TypeImported(FileClassImpl impl, AccessorDeclared declared) {
        super(impl.getImporter());
        TypeDeclared accessorTypeDeclared = declared.getTypeDeclared();
        this.thisGenericMap = accessorTypeDeclared.getGenericDeclaredMap();
        this.accessorClassname = declared.getAccessorClassname();
        this.accessorElement = declared.getAccessorElement();
        this.accessorTypeDeclared = accessorTypeDeclared;
        this.tableHolder = declared.tableHolder();
        this.typeHolder = declared.typeHolder();
        this.tableDeclared = declared.getTableDeclared();
        this.impl = impl;
        this.accessorDeclared = declared;
        this.utils = Processing2.getUtils();
        this.types = Processing2.getTypes();
    }

    protected final JavaMethod publicMethod(MethodDeclared methodDeclared, Consumer<JavaParameters> usingParameters) {
        return impl.publicMethod(methodDeclared.getMethodName(), usingParameters);
    }

    @Override
    protected final BaseImplementation getImplementation() { return impl; }

    @Override
    protected final String getJdbcSessionName() { return getJdbcSession().getFieldName(); }

    public final JavaField getJdbcSession() {
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

    public final String getTableImported() {
        if (tableImported == null) {
            tableImported = onImported(tableDeclared.getTableClassname());
        }
        return tableImported;
    }

    public final String getModelImported() {
        if (modelImported == null) {
            modelImported = onImported(tableDeclared.getModelClassname());
        }
        return modelImported;
    }

    public final String getParamsTypeImported() {
        if (paramsTypeImported == null) {
            paramsTypeImported = onImported(JdbcParameters.class);
        }
        return paramsTypeImported;
    }
}
