package com.moon.processing.decl;

import com.moon.accessor.annotation.JdbcDSL;
import com.moon.accessor.annotation.JdbcSQL;
import com.moon.accessor.annotation.Provided;
import com.moon.accessor.annotation.SafeModifying;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.file.JavaMethod;
import com.moon.processing.util.Collect2;
import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * @author benshaoye
 */
public class AccessorOnInterface extends TypeImported {

    private MethodDeclared parsingDeclared;

    public AccessorOnInterface(FileClassImpl impl, AccessorDeclared declared) { super(impl, declared); }

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
            if (paramsCount == 0) {
                unsafeDeleteModifying(overrideMethod(declared));
            } else if (paramsCount == 1) {
                doImplUpdateForOnlyParameter(declared);
            } else {
                doImplDeleteForMultiParameters(declared);
            }
        }

        private void doImplDeleteForMultiParameters(MethodDeclared methodDecl) {
            Map<ColumnDeclared, ParameterDeclared> columnsMap = getColsMap(methodDecl, 0);
            JavaMethod implMethod = overrideMethod(methodDecl);
            if (columnsMap.isEmpty()) {

            } else {
            }
        }

        private void doImplUpdateForOnlyParameter(MethodDeclared declared) {
            ParameterDeclared parameter = declared.getParameterAt(0);
        }
    }

    private class UpdateByMethodDeclared implements MethodImplementor {

        @JdbcSQL("")
        @JdbcDSL("")
        @Override
        public void doImplMethod(MethodDeclared methodDecl) {
            switch (methodDecl.getParametersCount()) {
                case 0: {
                    overrideMethod(methodDecl);
                    break;
                }
                case 1: {
                    ExecutableElement elem = methodDecl.getMethod();
                    SafeModifying modifying = elem.getAnnotation(SafeModifying.class);
                    JavaMethod implMethod = overrideMethod(methodDecl);
                    if (modifying == null) {
                        unsafeUpdateModifying(implMethod);
                        break;
                    }
                    ParameterDeclared parameter = methodDecl.getParameterAt(0);
                    String paramActualType = parameter.getActualType();
                    ColumnDeclared refColumnDecl = tableDeclared.getColumnDeclared(parameter.getParameterName());
                    if (refColumnDecl != null && isSamePropertyType(paramActualType, refColumnDecl.getFieldClass())) {
                        doImplUpdateParameters(methodDecl, implMethod, Collect2.ofMap(refColumnDecl, parameter));
                    } else {
                        Map<ColumnDeclared, PropertyDeclared> columnsMap;
                        Element paramElem = types.asElement(parameter.getParameter().asType());
                        if (paramElem instanceof TypeElement) {
                            TypeDeclared paramModel = typeHolder.withTypeElement(paramElem);
                            if (Objects.equals(paramModel, tableDeclared.getTypeDeclared())) {
                                columnsMap = getColsPropsMap();
                            } else {
                                columnsMap = getColsPropsMap(paramModel);
                            }
                            doImplUpdateFields(methodDecl, implMethod, columnsMap, parameter);
                        }
                    }
                    break;
                }
                case 2: {
                    break;
                }
                default: {
                    doImplUpdateForMultiParameters(methodDecl);
                }
            }
        }

        /**
         * 无条件的全量更新
         *
         * @param methodDecl
         * @param implMethod
         * @param columnsMap
         * @param parameter
         */
        private void doImplUpdateFields(
            MethodDeclared methodDecl,
            JavaMethod implMethod,
            Map<ColumnDeclared, PropertyDeclared> columnsMap,
            ParameterDeclared parameter
        ) {
            if (columnsMap.isEmpty()) {
                return;
            }
            writeUpdateSqlAsNonWhere(implMethod, columnsMap);
            for (Map.Entry<ColumnDeclared, PropertyDeclared> entry : columnsMap.entrySet()) {
                PropertyDeclared param = entry.getValue();
                ColumnDeclared column = entry.getKey();
                implMethod.nextFormatted("parameters.add({}, {})",
                    column.getConstColumnRef(getTableImported()),
                    param.getReffedGetterScript(parameter.getParameterName()));
            }
            writeJdbcSessionUpdate(implMethod);
        }

        /**
         * 无条件的全量更新
         *
         * @param methodDeclared
         * @param implMethod
         * @param columnsMap
         */
        private void doImplUpdateParameters(
            MethodDeclared methodDeclared, JavaMethod implMethod, Map<ColumnDeclared, ParameterDeclared> columnsMap
        ) {
            if (columnsMap.isEmpty()) {
                return;
            }
            writeUpdateSqlAsNonWhere(implMethod, columnsMap);
            for (Map.Entry<ColumnDeclared, ParameterDeclared> entry : columnsMap.entrySet()) {
                ParameterDeclared param = entry.getValue();
                ColumnDeclared column = entry.getKey();
                implMethod.nextFormatted("parameters.add({}, {})",
                    column.getConstColumnRef(getTableImported()),
                    param.getParameterName());
            }
            writeJdbcSessionUpdate(implMethod);
        }

        private void doImplUpdateForMultiParameters(MethodDeclared methodDecl) {
            ParameterDeclared firstParameter = methodDecl.getParameterAt(0);
            String parameterName = firstParameter.getParameterName();
            String paramActualType = firstParameter.getActualType();
            ColumnDeclared refColumnDecl = tableDeclared.getColumnDeclared(parameterName);

            Map<ColumnDeclared, ParameterDeclared> columnsMap = getColsMap(methodDecl, 0);
            JavaMethod implMethod = overrideMethod(methodDecl);
            if (columnsMap.isEmpty()) {
                unsafeUpdateModifying(implMethod);
            } else {
            }
            if (isSamePropertyType(paramActualType, refColumnDecl.getFieldClass())) {
                // String sql =
            } else {

            }
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
            for (Map.Entry<ColumnDeclared, ParameterDeclared> columnMap : columnsMap.entrySet()) {
                ParameterDeclared param = columnMap.getValue();
                ColumnDeclared column = columnMap.getKey();
                implMethod.nextFormatted("parameters.add({}, {})",
                    column.getConstColumnRef(getTableImported()),
                    param.getParameterName());
            }
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
            for (Map.Entry<ColumnDeclared, PropertyDeclared> columnMap : columnsMap.entrySet()) {
                PropertyDeclared prop = columnMap.getValue();
                ColumnDeclared column = columnMap.getKey();
                implMethod.nextFormatted("parameters.add({}, {})",
                    column.getConstColumnRef(getTableImported()),
                    prop.getReffedGetterScript(parameter.getParameterName()));
            }
            writeJdbcSessionInsert(implMethod);
        }

        private void doImplInsertMethodForModel(
            MethodDeclared methodDeclared, JavaMethod implMethod, ParameterDeclared parameter
        ) {
            Map<ColumnDeclared, PropertyDeclared> columnsMap;
            TypeMirror parameterType = parameter.getParameter().asType();
            Element parameterTypeElem = types.asElement(parameterType);
            if (parameterTypeElem instanceof TypeElement) {
                TypeDeclared paramModel = typeHolder.withTypeElement(parameterTypeElem);
                if (Objects.equals(paramModel, tableDeclared.getTypeDeclared())) {
                    columnsMap = getColsPropsMap();
                } else {
                    columnsMap = getColsPropsMap(paramModel);
                }
                if (!columnsMap.isEmpty()) {
                    return;
                }
                doImplInsertFields(methodDeclared, implMethod, columnsMap, parameter);
            }
        }
    }
}
