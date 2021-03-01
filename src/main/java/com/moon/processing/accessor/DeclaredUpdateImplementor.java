package com.moon.processing.accessor;

import com.moon.accessor.annotation.ForceModifying;
import com.moon.processing.decl.*;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.file.JavaMethod;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.Collect2;

import java.util.Map;

/**
 * @author benshaoye
 */
public class DeclaredUpdateImplementor extends DeclaredBaseImplementor {

    protected DeclaredUpdateImplementor(
        Holders holders, AccessorDeclared accessorDeclared, FileClassImpl impl
    ) { super(holders, accessorDeclared, impl); }

    @Override
    public void doImplMethod(MethodDeclared methodDecl) {
        final int count = withParsingMethodDeclared(methodDecl).getParametersCount();
        if (count == 0) {
            overrideParsingMethod();
        } else if (count == 2) {
            doImplUpdateMethod(methodDecl.getParameterAt(0), methodDecl.getParameterAt(1));
        } else {
            doImplUpdateMethod();
        }
    }

    private void doImplUpdateMethod(ParameterDeclared first, ParameterDeclared second) {
        TableDeclared tableDeclared = getTableDeclared();
        String firstActualType = first.getActualType();
        String secondActualType = second.getActualType();
        ColumnDeclared firstColumn = tableDeclared.getColumnDeclared(first.getParameterName());
        ColumnDeclared secondColumn = tableDeclared.getColumnDeclared(second.getParameterName());
        if (secondColumn != null && isSamePropertyType(secondActualType, secondColumn.getFieldClass())) {
            if (firstColumn != null && isSamePropertyType(firstActualType, firstColumn.getFieldClass())) {
                doImplSetParamsOnParams(Collect2.ofMap(firstColumn, first), Collect2.ofMap(secondColumn, second));
            } else {
                doImplSetPropsOnParams(getColumnsPropsMap(first), first, Collect2.ofMap(secondColumn, second));
            }
        } else {
            if (firstColumn != null && isSamePropertyType(firstActualType, firstColumn.getFieldClass())) {
                doImplSetParamsOnProps(Collect2.ofMap(firstColumn, first), getColumnsPropsMap(second), second);
            } else {
                doImplSetPropsOnProps(getColumnsPropsMap(first), first, getColumnsPropsMap(second), second);
            }
        }
    }

    private void doImplUpdateMethod() {
        ParameterDeclared parameter = getParsingMethod().getParameterAt(0);
        String parameterName = parameter.getParameterName();
        String paramActualType = parameter.getActualType();
        ColumnDeclared refColumnDecl = getColumnDeclaredByName(parameterName);
        Map<ColumnDeclared, ParameterDeclared> updateWhereColsMap = getParsingColumnsMap(1);
        if (refColumnDecl != null && isSamePropertyType(paramActualType, refColumnDecl.getFieldClass())) {
            doImplSetParamsOnParams(Collect2.ofMap(refColumnDecl, parameter), updateWhereColsMap);
        } else {
            doImplSetPropsOnParams(getColumnsPropsMap(parameter), parameter, updateWhereColsMap);
        }
    }

    private void doImplSetPropsOnParams(
        Map<ColumnDeclared, PropertyDeclared> updateSetColsMap,
        ParameterDeclared parameter,
        Map<ColumnDeclared, ParameterDeclared> updateWhereColsMap
    ) {
        JavaMethod implMethod = allowImplMethod(updateSetColsMap, updateWhereColsMap);
        if (implMethod == null) {
            return;
        }
        writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
        writeAddParameters(implMethod, updateSetColsMap, parameter.getParameterName());
        writeAddParameters(implMethod, updateWhereColsMap);
        writeJdbcSessionUpdate(implMethod);
    }

    private void doImplSetParamsOnProps(
        Map<ColumnDeclared, ParameterDeclared> updateSetColsMap,
        Map<ColumnDeclared, PropertyDeclared> updateWhereColsMap,
        ParameterDeclared parameter
    ) {
        JavaMethod implMethod = allowImplMethod(updateSetColsMap, updateWhereColsMap);
        if (implMethod == null) {
            return;
        }
        writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
        writeAddParameters(implMethod, updateSetColsMap);
        writeAddParameters(implMethod, updateWhereColsMap, parameter.getParameterName());
        writeJdbcSessionUpdate(implMethod);
    }

    private void doImplSetPropsOnProps(
        Map<ColumnDeclared, PropertyDeclared> updateSetColsMap,
        ParameterDeclared setParam,
        Map<ColumnDeclared, PropertyDeclared> updateWhereColsMap,
        ParameterDeclared whereParam
    ) {
        JavaMethod implMethod = allowImplMethod(updateSetColsMap, updateWhereColsMap);
        if (implMethod == null) {
            return;
        }
        writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
        writeAddParameters(implMethod, updateSetColsMap, setParam.getParameterName());
        writeAddParameters(implMethod, updateWhereColsMap, whereParam.getParameterName());
        writeJdbcSessionUpdate(implMethod);
    }

    private void doImplSetParamsOnParams(
        Map<ColumnDeclared, ParameterDeclared> updateSetColsMap,
        Map<ColumnDeclared, ParameterDeclared> updateWhereColsMap
    ) {
        JavaMethod implMethod = allowImplMethod(updateSetColsMap, updateWhereColsMap);
        if (implMethod == null) {
            return;
        }
        writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
        writeAddParameters(implMethod, updateSetColsMap);
        writeAddParameters(implMethod, updateWhereColsMap);
        writeJdbcSessionUpdate(implMethod);
    }

    private JavaMethod allowImplMethod(
        Map<ColumnDeclared, ?> updateSetColsMap, Map<ColumnDeclared, ?> updateWhereColsMap
    ) {
        JavaMethod implMethod = overrideParsingMethod();
        if (updateSetColsMap.isEmpty()) {
            return null;
        }
        if (updateWhereColsMap.isEmpty()) {
            ForceModifying modifying = getParsingMethod().getAtExecutableAnnotation(ForceModifying.class);
            if (modifying == null) {
                unsafeUpdateModifying(implMethod);
                return null;
            }
        }
        return implMethod;
    }

    protected void writeUpdateSql(
        JavaMethod implMethod,
        Map<ColumnDeclared, ? extends AnnotatedDeclared> columnsMap,
        Map<ColumnDeclared, ? extends AnnotatedDeclared> whereColumnsMap
    ) {
        if (whereColumnsMap.isEmpty()) {
            implMethod.nextFormatted("{} sql = \"UPDATE {} SET {}\"",
                implMethod.onImported(String.class),
                toDatabaseSymbol(getTableDeclared().getTableName()),
                joinForUpdateSet(columnsMap));
        } else {
            implMethod.nextFormatted("{} sql = \"UPDATE {} SET {} WHERE {}\"",
                implMethod.onImported(String.class),
                toDatabaseSymbol(getTableDeclared().getTableName()),
                joinForUpdateSet(columnsMap),
                joinForUpdateSet(whereColumnsMap));
        }
        writeDeclareJdbcParameters(implMethod, columnsMap.size() + whereColumnsMap.size());
    }
}
