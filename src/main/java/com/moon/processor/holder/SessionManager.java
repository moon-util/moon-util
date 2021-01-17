package com.moon.processor.holder;

import com.moon.accessor.Session;
import com.moon.accessor.Supported;
import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.dml.InsertInto;
import com.moon.accessor.dml.InsertIntoColImpl;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.*;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public class SessionManager implements JavaFileWriteable {

    private static final Class<Session> SESSION_CLASS = Session.class;

    private static final String PKG = Element2.getPackageName(SESSION_CLASS);

    private static final int SUPPORTED_LEVEL, MAX_LEVEL;

    private static final String INSERT_INTO_IMPL_PKG;
    private static final String INSERT_INTO_PKG;
    private static final String INSERT_INTO;

    static {
        INSERT_INTO = String2.format("{}<R, TB>", InsertInto.class.getCanonicalName());
        INSERT_INTO_IMPL_PKG = Element2.getPackageName(InsertIntoColImpl.class);
        INSERT_INTO_PKG = Element2.getPackageName(InsertInto.class);

        Supported supported = SESSION_CLASS.getAnnotation(Supported.class);
        SUPPORTED_LEVEL = supported.value();
        MAX_LEVEL = supported.max();
    }

    private int maxLevel = 0;

    public SessionManager() {}

    public void setMaxLevelIfLessThan(int maxLevel) {
        if (this.maxLevel < maxLevel) {
            this.maxLevel = maxLevel;
        }
    }

    private int generateLevel() { return Math.min(maxLevel, MAX_LEVEL); }

    private boolean canGenerate() { return generateLevel() > SUPPORTED_LEVEL; }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        if (canGenerate()) {
            getDeclJavaFileList().forEach(java -> java.writeJavaFile(writer));
        }
    }

    private List<DeclInterFile> getDeclJavaFileList() {
        final int startingAt = SUPPORTED_LEVEL + 1, nextEnding = generateLevel() + 1;
        return getInsertionDeclJavaFile(startingAt, nextEnding);
    }

    private List<DeclInterFile> getInsertionDeclJavaFile(int startingAt, int nextEnding) {
        List<DeclInterFile> selects = new ArrayList<>();
        Map<String, DeclInterFile> javaFileMap = new LinkedHashMap<>();
        DeclParams interValuesParams = toInitInsertIntoValuesParams(startingAt);
        for (int i = startingAt; i < nextEnding; i++) {
            // T1, T2, T3, T4
            final String joined = toJoinedValuesGenericDeclared(i);
            // <T1, T2, R, TB extends {Table}<R, TB>>
            String genericDeclPlaced = '<' + joined + ", R, TB extends {}<R, TB>>";
            // <T1, T2, R, TB>
            String genericUsingPlaced = "{}<" + joined + ", R, TB>";
            DeclParams thisInterValuesArgs = toDeclParamsWith(interValuesParams, i);

            DeclInterFile limit = Select2.getForLimit(genericDeclPlaced, i, genericUsingPlaced);
            DeclInterFile orderBy = Select2.getForOrderBy(genericDeclPlaced, i, genericUsingPlaced, limit);
            DeclInterFile having = Select2.getForHaving(genericDeclPlaced, i, genericUsingPlaced, orderBy);
            DeclInterFile groupBy = Select2.getForGroupBy(genericDeclPlaced, i, genericUsingPlaced, orderBy, having);
            DeclInterFile where = Select2.getForWhere(genericDeclPlaced, i, genericUsingPlaced, groupBy);
            DeclInterFile from = Select2.getForFrom(genericDeclPlaced, i, genericUsingPlaced, where);
            DeclInterFile selectCols = Select2.getForSelectCols(joined, i, genericUsingPlaced, from);
            DeclInterFile insert = Select2.getForInsert(genericDeclPlaced, i, genericUsingPlaced, thisInterValuesArgs);

            interValuesParams = thisInterValuesArgs;
            javaFileMap.put(joined, insert);

            selects.add(limit);
            selects.add(orderBy);
            selects.add(having);
            selects.add(groupBy);
            selects.add(where);
            selects.add(from);
            selects.add(selectCols);
            selects.add(insert);
        }

        DeclJavaFile insertImpl = getInsertIntoColsImpl(javaFileMap, nextEnding - 1);
        selects.add(insertImpl);
        selects.add(getSessionDeclJavaFile(javaFileMap, insertImpl.getCanonicalName()));
        return selects;
    }

    private DeclInterFile getSessionDeclJavaFile(Map<String, DeclInterFile> javaFileMap, String insertImpl) {
        String simpleSessionClass = String2.format("Dml{}Session", generateLevel());
        DeclJavaFile session = DeclJavaFile.classOf(PKG, simpleSessionClass);
        String importedImpl = session.extend(SESSION_CLASS).onImported(insertImpl);

        // 构造器
        session.publicConstruct(DeclParams.of("config", DSLConfiguration.class)).scriptOf("super(config)");

        String bound = Table.class.getCanonicalName();
        String tableField = TableField.class.getCanonicalName();
        for (Map.Entry<String, DeclInterFile> interFileEntry : javaFileMap.entrySet()) {
            String joined = interFileEntry.getKey();
            DeclInterFile interFile = interFileEntry.getValue();

            List<String> generics = String2.split(joined, ',');
            DeclParams params = DeclParams.of().addGeneralization("table", "TB", bound);
            String names = extractParams(generics, params, tableField);

            DeclMethod insertInto = session.publicMethod("insertInto", params);
            insertInto.genericOf("<{}, R, TB extends {}<R, TB>>", joined, Table.class);
            insertInto.returnTypeof("{}<{}, R, TB>", interFile.getCanonicalName(), joined);
            insertInto.returning("new {}<>(table, toArr({}))", importedImpl, names);
        }

        return session;
    }

    private static String extractParams(List<String> generics, DeclParams params, String tableField) {
        String[] fieldsNameList = new String[generics.size()];

        int index = 0;
        for (String generic : generics) {
            String trimmed = generic.trim();
            String name = trimmed.toLowerCase();
            String type = String2.format("{}<{}, R, TB>", tableField, trimmed);
            params.addActual(name, type);
            fieldsNameList[index++] = name;
        }

        return String.join(", ", fieldsNameList);
    }

    private DeclJavaFile getInsertIntoColsImpl(Map<String, DeclInterFile> javaFileMap, int endingAt) {
        String simpleImplName = "InsertIntoCol" + endingAt + "Impl";
        String joined = toJoinedValuesGenericDeclared(endingAt);
        DeclJavaFile insertImpl = DeclJavaFile.classOf(INSERT_INTO_IMPL_PKG, simpleImplName);
        String importedTable = insertImpl.onImported(Table.class);
        insertImpl.genericOf("<{}, R, TB extends {}<R, TB>>", joined, importedTable);
        insertImpl.extend("{}<T1, T2, T3, T4, R, TB>", InsertIntoColImpl.class);

        List<String> interfacesList = new ArrayList<>();
        String valuesReturnType = String.format("%s<%s, R, TB>", simpleImplName, joined);

        for (Map.Entry<String, DeclInterFile> fileEntry : javaFileMap.entrySet()) {
            String interGenericDecl = String.format("<%s, R, TB>", fileEntry.getKey());
            DeclInterFile interFile = fileEntry.getValue();
            interfacesList.add(interFile.getCanonicalName() + interGenericDecl);
            interFile.forEachMethods(method -> {
                String methodName = method.getName();
                if ("values".equals(methodName)) {
                    DeclParams params = method.getClonedParams();
                    DeclMethod override = insertImpl.publicMethod(method.getName(), params);
                    override.override().returnTypeof(valuesReturnType).returning("this");
                }
            });
        }

        insertImpl.implement(interfacesList.toArray(Const2.EMPTY));

        // construct
        String bound = Table.class.getCanonicalName();
        String fieldsType = String2.format("{}<?, R, TB>[]", TableField.class.getCanonicalName());
        DeclParams params = DeclParams.of().addGeneralization("table", "TB", bound);
        DeclConstruct construct = insertImpl.publicConstruct(params.addActual("fields", fieldsType));
        construct.scriptOf("super(getConfig(), table, fields)");

        return insertImpl;
    }

    private static DeclParams toDeclParamsWith(DeclParams prevParams, int index) {
        return prevParams.clone().addGeneralization("v" + index, "T" + index, Object.class);
    }

    private static DeclParams toInitInsertIntoValuesParams(int startingAt) {
        DeclParams params = DeclParams.of();
        for (int i = 1; i < startingAt; i++) {
            params.addGeneralization("v" + i, "T" + i, Object.class);
        }
        return params;
    }

    /**
     * startingAt = 4;
     * return T1, T2, T3, T4
     *
     * @param n n
     *
     * @return t
     */
    private static String toJoinedValuesGenericDeclared(int n) {
        String[] generics = new String[n];
        for (int i = 0; i < n; i++) {
            generics[i] = "T" + (i + 1);
        }
        return String.join(", ", generics);
    }
}
