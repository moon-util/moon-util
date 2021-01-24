package com.moon.processor.holder;

import com.moon.accessor.Supported;
import com.moon.accessor.config.Configuration;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.accessor.session.DSLSession;
import com.moon.accessor.session.DSLSessionBuilder;
import com.moon.accessor.session.DSLSessionImpl;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclInterFile;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import java.util.*;

import static com.moon.processor.holder.Select2.*;

/**
 * @author benshaoye
 */
public class SessionManager implements JavaFileWriteable {

    private static final String SESSION_CLASS = DSLSession.class.getCanonicalName();
    private static final String SESSION_BUILDER_PKG = Element2.getPackageName(DSLSessionBuilder.class);
    private static final String SESSION_BUILDER_CLASS = Element2.getQualifiedName(DSLSessionBuilder.class);
    private static final Class<DSLSessionImpl> SESSION_IMPL_CLASS = DSLSessionImpl.class;

    private static final String PKG = Element2.getPackageName(SESSION_IMPL_CLASS);

    private static final int SUPPORTED_LEVEL, MAX_LEVEL;

    static {
        Supported supported = SESSION_IMPL_CLASS.getAnnotation(Supported.class);
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

            Set<String> sessionBuilderImpls = new LinkedHashSet<>();
            sessionBuilderImpls.add(getSessionImplCanonicalName());
            writer.writeResourceFile(SESSION_BUILDER_CLASS, sessionBuilderImpls);
        }
    }

    private List<DeclInterFile> getDeclJavaFileList() {
        final int startingAt = SUPPORTED_LEVEL + 1, nextEnding = generateLevel() + 1;
        return getInsertionDeclJavaFile(startingAt, nextEnding);
    }

    private List<DeclInterFile> getInsertionDeclJavaFile(int startingAt, int nextEnding) {
        List<DeclInterFile> files = new ArrayList<>();
        Map<String, DeclInterFile> insertColsMap = new LinkedHashMap<>();
        Map<String, DeclInterFile> insertValsMap = new LinkedHashMap<>();
        DeclParams interValuesParams = toInitInsertIntoValuesParams(startingAt);
        for (int i = startingAt; i < nextEnding; i++) {
            // T1, T2, T3, T4
            final String joined = Holder2.toTypesJoined(i);
            // <T1, T2, R, TB extends {Table}<R, TB>>
            String genericDeclPlaced = '<' + joined + ", R, TB extends {}<R, TB>>";
            // <T1, T2, R, TB>
            String genericUsingPlaced = "{}<" + joined + ", R, TB>";
            DeclParams thisInterValuesArgs = toDeclParamsWith(interValuesParams, i);

            DeclInterFile limit = getForLimit(genericDeclPlaced, i, genericUsingPlaced);
            DeclInterFile orderBy = getForOrderBy(genericDeclPlaced, i, genericUsingPlaced, limit);
            DeclInterFile having = getForHaving(genericDeclPlaced, i, genericUsingPlaced, orderBy);
            DeclInterFile groupBy = getForGroupBy(genericDeclPlaced, i, genericUsingPlaced, orderBy, having);
            DeclInterFile where = getForWhere(genericDeclPlaced, i, genericUsingPlaced, groupBy);
            DeclInterFile from = getForFrom(genericDeclPlaced, i, genericUsingPlaced, where);
            DeclInterFile selectCols = getForSelectCols(joined, i, genericUsingPlaced, from);
            DeclInterFile insertCols = forInsertCols(genericDeclPlaced, i, genericUsingPlaced, thisInterValuesArgs);
            DeclInterFile insertVals = forInsertVals(genericDeclPlaced, i, genericUsingPlaced);

            interValuesParams = thisInterValuesArgs;
            insertColsMap.put(joined, insertCols);
            insertValsMap.put(joined, insertVals);

            files.add(limit);
            files.add(orderBy);
            files.add(having);
            files.add(groupBy);
            files.add(where);
            files.add(from);
            files.add(selectCols);
            files.add(insertCols);
            files.add(insertVals);
        }

        DeclJavaFile colImpl = InsertImpl2.forColImpl(nextEnding - 1, insertColsMap);
        DeclJavaFile valImpl = InsertImpl2.forValImpl(nextEnding - 1, insertValsMap);
        files.add(colImpl);
        files.add(valImpl);
        forDSLSession(files, insertColsMap, colImpl.getCanonicalName());
        return files;
    }

    private String getSessionImplCanonicalName() {
        return String.join(".", SESSION_BUILDER_PKG, getSessionBuilderImplSimpleName());
    }

    private String getSessionBuilderImplSimpleName() {
        return String2.format("DSL{}SessionBuilderImpl", generateLevel());
    }

    private void forDSLSession(
        List<DeclInterFile> files, Map<String, DeclInterFile> javaFileMap, String insertImpl
    ) {
        String sessionName = String2.format("DSL{}Session", generateLevel());
        String sessionImplName = String2.format("DSL{}SessionImpl", generateLevel());
        DeclInterFile session = DeclInterFile.interfaceOf(PKG, sessionName).implement(SESSION_CLASS);
        DeclJavaFile sessionImpl = DeclJavaFile.classOf(PKG, sessionImplName).extend(SESSION_IMPL_CLASS);
        sessionImpl.implement(session.getCanonicalName(), SESSION_CLASS);
        String importedImpl = sessionImpl.onImported(insertImpl);

        // 构造器
        DeclParams constructParams = DeclParams.of("config", Configuration.class);
        sessionImpl.construct(constructParams).scriptOf("super(config)");

        String bound = Table.class.getCanonicalName();
        String tableField = TableField.class.getCanonicalName();
        for (Map.Entry<String, DeclInterFile> interFileEntry : javaFileMap.entrySet()) {
            String joined = interFileEntry.getKey();
            DeclInterFile interFile = interFileEntry.getValue();

            List<String> generics = String2.split(joined, ',');
            DeclParams params = DeclParams.of().addGeneralization("table", "TB", bound);
            String names = extractParams(generics, params, tableField);

            // 接口方法
            DeclMethod insert = session.publicMethod("insertInto", params);
            insert.genericOf("<{}, R, TB extends {}<R, TB>>", joined, Table.class);
            insert.returnTypeof("{}<{}, R, TB>", interFile.getCanonicalName(), joined);

            // 实现
            DeclMethod insertInto = sessionImpl.publicMethod("insertInto", params).override();
            insertInto.genericOf("<{}, R, TB extends {}<R, TB>>", joined, Table.class);
            insertInto.returnTypeof("{}<{}, R, TB>", interFile.getCanonicalName(), joined);
            insertInto.returning("new {}<>(getConfig(), table, toArr({}))", importedImpl, names);
        }

        // DSLSessionBuilder
        String builderSimpleName = getSessionBuilderImplSimpleName();
        DeclJavaFile builderImpl = DeclJavaFile.classOf(SESSION_BUILDER_PKG, builderSimpleName);
        builderImpl.implement(SESSION_BUILDER_CLASS);
        DeclMethod buildMethod = builderImpl.publicMethod("build", constructParams);
        buildMethod.override().returnTypeof(session.getCanonicalName());
        String importedSessionImpl = builderImpl.onImported(sessionImpl.getCanonicalName());
        buildMethod.returning("new {}(config)", importedSessionImpl);

        files.add(session);
        files.add(sessionImpl);
        files.add(builderImpl);
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
}
