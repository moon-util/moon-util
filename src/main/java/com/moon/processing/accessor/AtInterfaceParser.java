package com.moon.processing.accessor;

import com.moon.accessor.annotation.ForceModifying;
import com.moon.accessor.annotation.Provided;
import com.moon.processing.decl.*;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.file.JavaMethod;
import com.moon.processing.util.Collect2;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public class AtInterfaceParser extends TypeImported {

    private MethodDeclared parsingDeclared;

    public AtInterfaceParser(FileClassImpl impl, AccessorDeclared declared) { super(impl, declared); }

    public void doGenerate() {
        List<Runnable> runners = new ArrayList<>();
        accessorTypeDeclared.getAllMethodsDeclared().forEach(declared -> {
            this.parsingDeclared = declared;
            ExecutableElement element = declared.getMethod();
            if (Test2.isAny(element, Modifier.DEFAULT)) {
                return;
            }
            Provided provided = element.getAnnotation(Provided.class);
            if (provided != null) {
                // Provided 有优先权，但要后处理
                runners.add(() -> onProvidedAnnotated(declared, provided));
                return;
            }
            String methodName = Element2.getSimpleName(element);
            if (doParsingOnAnnotated(methodName, element)) {
                // TODO SQL注解
                return;
            }
            // TODO 方法名解析
            doParsingWithDeclared(declared).doImplMethod(declared);
        });
        runners.forEach(Runnable::run);
    }

    private boolean doParsingOnAnnotated(String methodName, ExecutableElement element) {
        return false;
    }

    @SuppressWarnings("all")
    private MethodImplementor doParsingWithDeclared(MethodDeclared methodDeclared) {
        switch (String2.firstWord(methodDeclared.getMethodName()).toLowerCase()) {
            case "insert": {
                return new InsertByMethodDeclared();
            }
            case "modify":
            case "update": {
                return new UpdateByMethodDeclared();
            }
            case "save": {
                // implMethodForSave(methodName, element);
                return null;
            }
            case "delete":
            case "remove": {
                return new DeleteByMethodDeclared();
            }
            case "get": {
                // get 方法，只能且必须返回一行单行数据
                return null;
            }
            case "find":
            case "read":
            case "query":
            case "fetch":
            case "select":
            case "search": {
                return new SelectByMethodDeclared();
            }
            case "count": {
                return null;
            }
            case "exists":
            case "existing": {
                return null;
            }
            default:
                return null;
        }
    }

    private interface MethodImplementor {

        void doImplMethod(MethodDeclared methodDeclared);
    }

    private class SelectByMethodDeclared implements MethodImplementor {

        @Override
        public void doImplMethod(MethodDeclared methodDeclared) { }
    }

    private class DeleteByMethodDeclared implements MethodImplementor {

        @Override
        public void doImplMethod(MethodDeclared declared) {
            final int paramsCount = declared.getParametersCount();
            // if (paramsCount == 0) {
            //     unsafeDeleteModifying(overrideMethod(declared));
            // } else if (paramsCount == 1) {
            //     doImplUpdateForOnlyParameter(declared);
            // } else {
            //     doImplDeleteForMultiParameters(declared);
            // }
        }
    }

    private class UpdateByMethodDeclared implements MethodImplementor {

        @Override
        public void doImplMethod(MethodDeclared methodDecl) {
            switch (methodDecl.getParametersCount()) {
                case 0: {
                    overrideMethod(methodDecl);
                    break;
                }
                case 1: {
                    doImplUpdateMethod(methodDecl);
                    break;
                }
                case 2: {
                    doImplUpdateMethod(methodDecl.getParameterAt(0), methodDecl.getParameterAt(1));
                    break;
                }
                default: {
                    doImplUpdateMethod(methodDecl);
                }
            }
        }

        private void doImplUpdateMethod(ParameterDeclared first, ParameterDeclared second) {
            String firstActualType = first.getActualType();
            String secondActualType = second.getActualType();
            ColumnDeclared firstColumn = tableDeclared.getColumnDeclared(first.getParameterName());
            ColumnDeclared secondColumn = tableDeclared.getColumnDeclared(second.getParameterName());
            if (secondColumn != null && isSamePropertyType(secondActualType, secondColumn.getFieldClass())) {
                if (firstColumn != null && isSamePropertyType(firstActualType, firstColumn.getFieldClass())) {
                    doImplUpdateSetParamsOnParams(Collect2.ofMap(firstColumn, first),
                        Collect2.ofMap(secondColumn, second));
                } else {
                    doImplUpdateSetPropsOnParams(getColsPropsMap(first), first, Collect2.ofMap(secondColumn, second));
                }
            } else {
                if (firstColumn != null && isSamePropertyType(firstActualType, firstColumn.getFieldClass())) {
                    doImplUpdateSetParamsOnProps(Collect2.ofMap(firstColumn, first), getColsPropsMap(second), second);
                } else {
                    doImplUpdateSetPropsOnProps(getColsPropsMap(first), first, getColsPropsMap(second), second);
                }
            }
        }

        private void doImplUpdateMethod(MethodDeclared methodDecl) {
            ParameterDeclared parameter = methodDecl.getParameterAt(0);
            String parameterName = parameter.getParameterName();
            String paramActualType = parameter.getActualType();
            ColumnDeclared refColumnDecl = tableDeclared.getColumnDeclared(parameterName);
            Map<ColumnDeclared, ParameterDeclared> updateWhereColsMap = getColsMap(methodDecl, 1);
            if (refColumnDecl != null && isSamePropertyType(paramActualType, refColumnDecl.getFieldClass())) {
                doImplUpdateSetParamsOnParams(Collect2.ofMap(refColumnDecl, parameter), updateWhereColsMap);
            } else {
                doImplUpdateSetPropsOnParams(getColsPropsMap(parameter), parameter, updateWhereColsMap);
            }
        }

        private void doImplUpdateSetPropsOnParams(
            Map<ColumnDeclared, PropertyDeclared> updateSetColsMap,
            ParameterDeclared parameter,
            Map<ColumnDeclared, ParameterDeclared> updateWhereColsMap
        ) {
            JavaMethod implMethod = allowImplMethod(parsingDeclared, updateSetColsMap, updateWhereColsMap);
            if (implMethod == null) {
                return;
            }
            writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
            writeAddParameters(implMethod, updateSetColsMap, parameter.getParameterName());
            writeAddParameters(implMethod, updateWhereColsMap);
            writeJdbcSessionUpdate(implMethod);
        }

        private void doImplUpdateSetParamsOnProps(
            Map<ColumnDeclared, ParameterDeclared> updateSetColsMap,
            Map<ColumnDeclared, PropertyDeclared> updateWhereColsMap,
            ParameterDeclared parameter
        ) {
            JavaMethod implMethod = allowImplMethod(parsingDeclared, updateSetColsMap, updateWhereColsMap);
            if (implMethod == null) {
                return;
            }
            writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
            writeAddParameters(implMethod, updateSetColsMap);
            writeAddParameters(implMethod, updateWhereColsMap, parameter.getParameterName());
            writeJdbcSessionUpdate(implMethod);
        }

        private void doImplUpdateSetPropsOnProps(
            Map<ColumnDeclared, PropertyDeclared> updateSetColsMap,
            ParameterDeclared setParam,
            Map<ColumnDeclared, PropertyDeclared> updateWhereColsMap,
            ParameterDeclared whereParam
        ) {
            JavaMethod implMethod = allowImplMethod(parsingDeclared, updateSetColsMap, updateWhereColsMap);
            if (implMethod == null) {
                return;
            }
            writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
            writeAddParameters(implMethod, updateSetColsMap, setParam.getParameterName());
            writeAddParameters(implMethod, updateWhereColsMap, whereParam.getParameterName());
            writeJdbcSessionUpdate(implMethod);
        }

        private void doImplUpdateSetParamsOnParams(
            Map<ColumnDeclared, ParameterDeclared> updateSetColsMap,
            Map<ColumnDeclared, ParameterDeclared> updateWhereColsMap
        ) {
            JavaMethod implMethod = allowImplMethod(parsingDeclared, updateSetColsMap, updateWhereColsMap);
            if (implMethod == null) {
                return;
            }
            writeUpdateSql(implMethod, updateSetColsMap, updateWhereColsMap);
            writeAddParameters(implMethod, updateSetColsMap);
            writeAddParameters(implMethod, updateWhereColsMap);
            writeJdbcSessionUpdate(implMethod);
        }

        private JavaMethod allowImplMethod(
            MethodDeclared methodDecl,
            Map<ColumnDeclared, ?> updateSetColsMap,
            Map<ColumnDeclared, ?> updateWhereColsMap
        ) {
            JavaMethod implMethod = overrideMethod(parsingDeclared);
            if (updateSetColsMap.isEmpty()) {
                return null;
            }
            if (updateWhereColsMap.isEmpty()) {
                ForceModifying modifying = methodDecl.getAtExecutableAnnotation(ForceModifying.class);
                if (modifying == null) {
                    unsafeUpdateModifying(implMethod);
                    return null;
                }
            }
            return implMethod;
        }
    }

    /**
     * 从方法声明解析 insert 方法
     */
    private class InsertByMethodDeclared implements MethodImplementor {

        @Override
        public void doImplMethod(MethodDeclared decl) {
            switch (decl.getParametersCount()) {
                case 0: {
                    overrideMethod(decl);
                    break;
                }
                case 1: {
                    doImplInsertForOnlyParameter(decl);
                    break;
                }
                default: {
                    doImplInsertForMultiParameters(decl);
                    break;
                }
            }
        }

        private void doImplInsertForMultiParameters(MethodDeclared methodDecl) {
            Map<ColumnDeclared, ParameterDeclared> columnsMap = getColsMap(methodDecl, 0);
            doImplInsertParameters(methodDecl, overrideMethod(methodDecl), columnsMap);
        }

        private void doImplInsertForOnlyParameter(MethodDeclared methodDecl) {
            ParameterDeclared parameter = methodDecl.getParameterAt(0);
            String parameterActualType = parameter.getActualType();
            String parameterName = parameter.getParameterName();
            ColumnDeclared columnDecl = tableDeclared.getColumnDeclared(parameterName);
            JavaMethod implMethod = overrideMethod(methodDecl);

            if (columnDecl == null) {
                doImplInsertMethodForModel(methodDecl, implMethod, parameter);
            } else if (isSamePropertyType(parameterActualType, columnDecl.getFieldClass())) {
                doImplInsertParameters(methodDecl, implMethod, Collect2.ofMap(columnDecl, parameter));
            } else {
                doImplInsertMethodForModel(methodDecl, implMethod, parameter);
            }
        }

        private void doImplInsertParameters(
            MethodDeclared methodDeclared, JavaMethod implMethod, Map<ColumnDeclared, ParameterDeclared> columnsMap
        ) {
            if (columnsMap.isEmpty()) {
                return;
            }
            writeInsertSql(implMethod, columnsMap);
            writeAddParameters(implMethod, columnsMap);
            writeJdbcSessionInsert(implMethod);
        }

        private void doImplInsertFields(
            MethodDeclared methodDeclared,
            JavaMethod implMethod,
            Map<ColumnDeclared, PropertyDeclared> columnsMap,
            ParameterDeclared parameter
        ) {
            if (columnsMap.isEmpty()) {
                return;
            }
            writeInsertSql(implMethod, columnsMap);
            writeAddParameters(implMethod, columnsMap, parameter.getParameterName());
            writeJdbcSessionInsert(implMethod);
        }

        private void doImplInsertMethodForModel(
            MethodDeclared methodDeclared, JavaMethod implMethod, ParameterDeclared parameter
        ) {
            Map<ColumnDeclared, PropertyDeclared> columnsMap = getColsPropsMap(parameter);
            doImplInsertFields(methodDeclared, implMethod, columnsMap, parameter);
        }
    }
}
