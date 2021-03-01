package com.moon.processing.accessor;

import com.moon.processing.decl.*;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.file.JavaMethod;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.Collect2;

import java.util.Map;

/**
 * @author benshaoye
 */
public class DeclaredInsertImplementor extends DeclaredBaseImplementor {

    protected DeclaredInsertImplementor(
        Holders holders, AccessorDeclared accessorDeclared, FileClassImpl impl
    ) {
        super(holders, accessorDeclared, impl);
    }

    @Override
    public void doImplMethod(MethodDeclared methodDeclared) {
        withParsingMethodDeclared(methodDeclared);
        JavaMethod implMethod = overrideParsingMethod();
        switch (methodDeclared.getParametersCount()) {
            case 0:
                return;
            case 1:
                MethodDeclared methodDecl = getParsingMethod();
                ParameterDeclared parameter = methodDecl.getParameterAt(0);
                String parameterActualType = parameter.getActualType();
                ColumnDeclared columnDecl = getColumnDeclaredByName(parameter.getParameterName());

                if (columnDecl != null && isSamePropertyType(parameterActualType, columnDecl.getFieldClass())) {
                    doImplInsertParameters(implMethod, Collect2.ofMap(columnDecl, parameter));
                } else {
                    doImplInsertProperties(implMethod, parameter);
                }
                return;
            default:
                doImplInsertParameters(implMethod, getParsingColumnsMap(0));
                return;
        }
    }

    private void doImplInsertParameters(JavaMethod implMethod, Map<ColumnDeclared, ParameterDeclared> columnsMap) {
        if (columnsMap.isEmpty()) {
            return;
        }
        writeDeclareInsert(implMethod, columnsMap);
        writeAddParameters(implMethod, columnsMap);
        writeJdbcSessionInsert(implMethod);
    }

    private void doImplInsertProperties(JavaMethod implMethod, ParameterDeclared parameter) {
        Map<ColumnDeclared, PropertyDeclared> columnsMap = getColumnsPropsMap(parameter);
        if (columnsMap.isEmpty()) {
            return;
        }
        writeDeclareInsert(implMethod, columnsMap);
        writeAddParameters(implMethod, columnsMap, parameter.getParameterName());
        writeJdbcSessionInsert(implMethod);
    }
}
