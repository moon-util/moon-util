package com.moon.processor.holder;

import com.moon.accessor.Session;
import com.moon.accessor.Supported;
import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.dml.InsertInto;
import com.moon.accessor.dml.InsertIntoColsImpl;
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

import static com.moon.processor.holder.Select2.*;

/**
 * @author benshaoye
 */
public class SessionManager implements JavaFileWriteable {

    private static final Class<Session> SESSION_CLASS = Session.class;

    private static final String PKG = Element2.getPackageName(SESSION_CLASS);

    private static final int SUPPORTED_LEVEL, MAX_LEVEL;

    static {
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

            selects.add(limit);
            selects.add(orderBy);
            selects.add(having);
            selects.add(groupBy);
            selects.add(where);
            selects.add(from);
            selects.add(selectCols);
            selects.add(insertCols);
            selects.add(insertVals);
        }

        DeclJavaFile colImpl = InsertImpl2.forColImpl(nextEnding - 1, insertColsMap);
        DeclJavaFile valImpl = InsertImpl2.forValImpl(nextEnding - 1, insertValsMap);
        selects.add(colImpl);
        selects.add(valImpl);
        selects.add(getSessionDeclJavaFile(insertColsMap, colImpl.getCanonicalName()));
        return selects;
    }

    private DeclInterFile getSessionDeclJavaFile(Map<String, DeclInterFile> javaFileMap, String insertImpl) {
        String simpleSessionClass = String2.format("Dml{}Session", generateLevel());
        DeclJavaFile session = DeclJavaFile.classOf(PKG, simpleSessionClass).extend(SESSION_CLASS);
        String importedImpl = session.onImported(insertImpl);

        // 构造器
        session.construct(DeclParams.of("config", DSLConfiguration.class)).scriptOf("super(config)");

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
            insertInto.returning("new {}<>(getConfig(), table, toArr({}))", importedImpl, names);
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
