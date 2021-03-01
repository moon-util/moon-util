package com.moon.processing.accessor;

import com.moon.processing.decl.*;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.file.JavaMethod;
import com.moon.processing.holder.Holders;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public abstract class DeclaredBaseImplementor extends BaseImplementor implements DeclaredImplementor {

    protected DeclaredBaseImplementor(
        Holders holders, AccessorDeclared accessorDeclared, FileClassImpl impl
    ) { super(holders, accessorDeclared, impl); }

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ extract columns properties map
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected final Map<ColumnDeclared, ParameterDeclared> getColumnsMap(
        MethodDeclared methodDecl, final int startIdx
    ) {
        int index = 0;
        Map<ColumnDeclared, ParameterDeclared> columnsMap = new LinkedHashMap<>();
        for (ParameterDeclared parameter : methodDecl.getParametersDeclared()) {
            if ((index++) < startIdx) {
                continue;
            }
            String actualType = parameter.getActualType();
            String parameterName = parameter.getParameterName();
            ColumnDeclared column = getColumnDeclaredByName(parameterName);
            if (isSamePropertyType(actualType, column.getFieldClass())) {
                columnsMap.put(column, parameter);
            }
        }
        return columnsMap;
    }

    protected final Map<ColumnDeclared, PropertyDeclared> getColumnsPropsMap(ParameterDeclared parameter) {
        TypeDeclared paramModel = withTypeDeclared(parameter);
        Map<ColumnDeclared, PropertyDeclared> columnPropertyMap;
        if (isSamePropertyType(paramModel.getTypeClassname(), getTableDeclared().getModelClassname())) {
            columnPropertyMap = getColumnsPropsMap();
        } else {
            columnPropertyMap = getColumnsPropsMap(paramModel);
        }
        return columnPropertyMap;
    }

    protected final Map<ColumnDeclared, PropertyDeclared> getColumnsPropsMap() {
        return getTableDeclared().reduce((col, cols) -> cols.put(col, col.getProperty()), new LinkedHashMap<>());
    }

    protected final Map<ColumnDeclared, PropertyDeclared> getColumnsPropsMap(TypeDeclared paramModel) {
        Map<ColumnDeclared, PropertyDeclared> columnsMap = new LinkedHashMap<>();
        Map<String, PropertyDeclared> properties = paramModel.getCopiedProperties();
        for (PropertyDeclared property : properties.values()) {
            if (!property.isReadable()) {
                continue;
            }
            ColumnDeclared column = getColumnDeclaredByName(property.getName());
            if (column == null) {
                continue;
            }
            if (isSamePropertyType(property.getActualType(), column.getFieldClass())) {
                columnsMap.put(column, property);
            }
        }
        return columnsMap;
    }

    protected final Map<ColumnDeclared, ParameterDeclared> getParsingColumnsMap(int startIdx) {
        return getColumnsMap(getParsingMethod(), startIdx);
    }

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ extract columns properties map
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

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

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ join columns
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected final String joinForUpdateSet(Map<ColumnDeclared, ?> columnsMap) {
        return joinWith(columnsMap, column -> toDatabaseSymbol(column.getColumnName()) + " = ?");
    }

    protected final String joinForInsert(Map<ColumnDeclared, ?> columnsMap) {
        return joinWith(columnsMap, column -> toDatabaseSymbol(column.getColumnName()));
    }

    protected final String joinForSelect(Map<ColumnDeclared, ?> columnsMap) {
        return joinForInsert(columnsMap);
    }

    protected static String joinWith(Map<ColumnDeclared, ?> columnsMap, Function<ColumnDeclared, String> transformer) {
        int index = 0, length = columnsMap.size();
        String[] columns = new String[length];
        for (ColumnDeclared declared : columnsMap.keySet()) {
            columns[index++] = transformer.apply(declared);
        }
        return String.join(", ", columns);
    }

    protected final String toDatabaseSymbol(String columnName) { return '`' + columnName + '`'; }

    /*
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     ~ write parameters
     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    protected final void writeDeclareInsert(JavaMethod implMethod, Map<ColumnDeclared, ?> columnsMap) {
        writeFormattedSql(implMethod,
            "INSERT INTO {} ({}) VALUES ({})",
            toDatabaseSymbol(getTableDeclared().getTableName()),
            joinForInsert(columnsMap),
            toPlaceholders(columnsMap.size()));
        writeDeclareJdbcParameters(implMethod, columnsMap.size());
    }

    protected final void writeAddParameters(
        JavaMethod implMethod, Map<ColumnDeclared, PropertyDeclared> columnsMap, String parameterName
    ) {
        columnsMap.forEach((column, prop) -> implMethod.nextFormatted("parameters.add({}, {})",
            column.getConstColumnRef(getTableImported()),
            prop.getReffedGetterScript(parameterName)));
    }

    protected final void writeAddParameters(JavaMethod implMethod, Map<ColumnDeclared, ParameterDeclared> columnsMap) {
        columnsMap.forEach((column, param) -> implMethod.nextFormatted("parameters.add({}, {})",
            column.getConstColumnRef(getTableImported()),
            param.getParameterName()));
    }
}
