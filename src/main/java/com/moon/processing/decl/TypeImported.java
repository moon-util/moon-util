package com.moon.processing.decl;

import com.moon.accessor.meta.JdbcParameters;
import com.moon.accessor.session.JdbcSession;
import com.moon.processing.file.*;
import com.moon.processing.holder.TableHolder;
import com.moon.processing.holder.TypeHolder;
import com.moon.processing.util.Element2;
import com.moon.processing.util.Processing2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    protected final JavaMethod publicMethod(MethodDeclared methodDeclared) {
        List<ParameterDeclared> params = methodDeclared.getParametersDeclared();
        JavaMethod method= impl.publicMethod(methodDeclared.getMethodName(), parameters -> {
            for (ParameterDeclared parameter : params) {
            parameters.add(parameter.getParameterName(), parameter.getActualType());
            }
        }).typeOf(methodDeclared.getReturnActualType());
        defaultReturning(methodDeclared, method);
        return method;
    }

    protected final Map<ColumnDeclared,ParameterDeclared> getColsMap(MethodDeclared methodDecl, final int startIdx){
        int index = 0;
        Map<ColumnDeclared, ParameterDeclared> columnsMap = new LinkedHashMap<>();
        for (ParameterDeclared parameter : methodDecl.getParametersDeclared()) {
            if ((index++) < startIdx) {
                continue;
            }
            String actualType = parameter.getActualType();
            String parameterName = parameter.getParameterName();
            ColumnDeclared column = tableDeclared.getColumnDeclared(parameterName);
            if (isSamePropertyType(actualType, column.getFieldClass())) {
                columnsMap.put(column, parameter);
            }
        }
        return columnsMap;
    }

    protected final void defaultReturning(MethodDeclared methodDeclared, JavaMethod method) {
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

    // protected final JavaMethod publicMethod(MethodDeclared methodDeclared, Consumer<JavaParameters> usingParameters) {
    //     return impl.publicMethod(methodDeclared.getMethodName(), usingParameters);
    // }

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
