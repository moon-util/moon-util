package com.moon.processor.holder;

import com.moon.accessor.config.Configuration;
import com.moon.accessor.dml.InsertIntoColsImpl;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.processor.file.*;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import java.util.*;

import static com.moon.processor.utils.String2.format;
import static java.util.Comparator.comparingInt;

/**
 * @author benshaoye
 */
enum InsertImpl2 {
    ;

    private final static String CALL_SUPER = "super(config, table, fields)";
    private final static String PACKAGE = Element2.getPackageName(InsertIntoColsImpl.class);
    private final static String COL_SIMPLE_NAME_PLACED = "InsertIntoCol{}Impl";
    private final static String VAL_SIMPLE_NAME_PLACED = "InsertIntoVal{}Impl";
    private final static String VAL_CANONICAL_NAME_PLACED = PACKAGE + '.' + VAL_SIMPLE_NAME_PLACED;

    private static String getColImplSimpleName(int i) { return format(COL_SIMPLE_NAME_PLACED, i); }

    private static String getValImplSimpleName(int i) { return format(VAL_SIMPLE_NAME_PLACED, i); }

    private static String getValImplCanonicalName(int i) { return format(VAL_CANONICAL_NAME_PLACED, i); }

    private static class MaxLengthVal {

        private String value;

        public void setIfLonger(String value) {
            if (String2.length(this.value) < String2.length(value)) {
                this.value = value;
            }
        }

        public String getValue() { return value; }
    }

    private static String toUsingByJoined(String classname, String joined) {
        if (String2.isBlank(joined)) {
            return classname + "<R, TB>";
        } else {
            return classname + "<" + joined + ", R, TB>";
        }
    }

    private final static String INSERT_INTO_BASE_CLASSNAME//
        = InsertIntoColsImpl.class.getSuperclass().getCanonicalName();

    public static DeclJavaFile forColImpl(int endVal, Map<String, DeclInterFile> insertCols) {
        String simpleName = getColImplSimpleName(endVal);
        String valImplClass = getValImplCanonicalName(endVal);
        MaxLengthVal maxLengthVal = new MaxLengthVal();
        TreeSet<String> typesJoined = new TreeSet<>(comparingInt(String2::length));

        // declare class and implements interfaces
        DeclJavaFile colImpl = newAndImplInterfaces(simpleName, maxLengthVal, typesJoined, insertCols);

        // extends superclass of: InsertIntoColsImpl<T1, T2>
        String usingJoined = maxLengthVal.getValue();
        colImpl.extend(toUsingByJoined(INSERT_INTO_BASE_CLASSNAME, null));

        // <T1, T2, R, TB>
        colImpl.genericOf(Holder2.toGenericDeclWithJoined(usingJoined), Table.class);

        // public InsertIntoCol2Impl(DSLConfiguration config, TB table, TableTable<?, R, TB>... fields) { super(config, table, fields); }
        colImpl.construct(declParamsForConfigTableFields()).scriptOf(CALL_SUPER);

        // private InsertIntoVal2Impl<T1, T2, R, TB> insertValuesOf(List<Object[]> values) { /* ... */ }
        String insertValuesImpl = getValImplCanonicalName(endVal);
        String importedValImpl = colImpl.onImported(insertValuesImpl);
        String insertValuesJoined = toUsingByJoined(valImplClass, usingJoined);
        DeclParams params = DeclParams.of("values", "java.lang.Object...");
        DeclMethod insertValuesOf = colImpl.publicMethod("insertValuesOf", params).withPrivate();
        insertValuesOf.returnTypeof(insertValuesJoined);
        insertValuesOf.returning("new {}(getConfig(), getTable(), getFields(), values)", importedValImpl);

        // public InsertIntoVal2Impl<T1, T2, R, TB> values(T1 t1, T2 t2) { /* ... */ }
        for (String joined : typesJoined) {
            DeclParams valuesParams = declParamsBy(String2.split(joined, ','));
            DeclMethod valuesMethod = colImpl.publicMethod("values", valuesParams);
            valuesMethod.override().returnTypeof(insertValuesJoined);
            String argsJoined = String.join(", ", valuesParams.keySet());
            valuesMethod.returning("insertValuesOf({})", argsJoined);
        }

        DeclParams recordParams1 = Params2.declParamsForValuesRecord1();
        DeclParams recordParamsL = Params2.declParamsForValuesRecordC();
        DeclParams recordParamsN = Params2.declParamsForValuesRecordN();
        valuesRecord(colImpl, recordParams1, importedValImpl, true).returnTypeof(insertValuesJoined);
        valuesRecord(colImpl, recordParamsL, importedValImpl, false).returnTypeof(insertValuesJoined);
        valuesRecord(colImpl, recordParamsN, importedValImpl, true).returnTypeof(insertValuesJoined);

        return colImpl;
    }

    private static DeclMethod valuesRecord(
        DeclJavaFile impl, DeclParams params, String importedValImpl, boolean asList
    ) {
        String recordsName = String.join(", ", params.keySet());
        DeclMethod method = impl.publicMethod("valuesRecord", params).override();
        String returning;
        if (asList) {
            returning = "new {}(getConfig(), getTable(), getFields(), asList({}))";
        } else {
            returning = "new {}(getConfig(), getTable(), getFields(), {})";
        }
        return method.returning(returning, importedValImpl, recordsName);
    }

    public static DeclJavaFile forValImpl(int endVal, Map<String, DeclInterFile> insertVals) {
        String simpleName = getValImplSimpleName(endVal);
        MaxLengthVal maxLengthVal = new MaxLengthVal();
        TreeSet<String> typesJoined = new TreeSet<>(comparingInt(String2::length));

        // declare class and implements interfaces
        DeclJavaFile valImpl = newAndImplInterfaces(simpleName, maxLengthVal, typesJoined, insertVals);

        String usingJoined = maxLengthVal.getValue();

        // <T1, T2, ... Tn,  R, TB>
        valImpl.genericOf(Holder2.toGenericDeclWithJoined(usingJoined), Table.class);

        // extends superclass of: InsertIntoCol2Impl<T1, T2>
        valImpl.extend(toUsingByJoined(INSERT_INTO_BASE_CLASSNAME, null));

        // private final List<Object[]> values = new ArrayList<>(4);
        String importedArrayList = valImpl.onImported(ArrayList.class);
        valImpl.privateField("values", "{}<{}[]>", List.class, Object.class)//
            .withFinal().withGetterMethod(m -> {})//
            .valueOf("new {}<>({})", importedArrayList, 4);

        // constructor
        DeclParams paramsL = declParamsForConfigTableFields().addActual("records", "java.util.Collection<? extends R>");
        DeclConstruct constructL = valImpl.construct(paramsL).scriptOf(CALL_SUPER);
        constructL.scriptOf("requireAddRecord(this.getValues(), records)");

        DeclParams paramsN = declParamsForConfigTableFields().addActual("values", "java.lang.Object...");
        DeclConstruct constructN = valImpl.construct(paramsN).scriptOf(CALL_SUPER);
        constructN.scriptOf("requireAddAll(this.getValues(), values)");

        String valImplClass = valImpl.getCanonicalName();
        // public InsertIntoVal2Impl<T1, T2, R, TB> values(T1 t1, T2 t2) { /* ... */ }
        String thisUsingJoined = toUsingByJoined(valImplClass, usingJoined);
        for (String joined : typesJoined) {
            DeclParams valuesParams = declParamsBy(String2.split(joined, ','));
            DeclMethod valuesMethod = valImpl.publicMethod("values", valuesParams);
            valuesMethod.override().returnTypeof(thisUsingJoined);
            String argsJoined = String.join(", ", valuesParams.keySet());
            valuesMethod.scriptOf("requireAddAll(this.getValues(), {})", argsJoined);
            valuesMethod.returning("this");
        }

        // public InsertIntoVal2Impl<T1, T2, R, TB> valuesRecord(R record) { /* ... */ }
        DeclParams recordParams1 = Params2.declParamsForValuesRecord1();
        DeclParams recordParamsL = Params2.declParamsForValuesRecordC();
        DeclParams recordParamsN = Params2.declParamsForValuesRecordN();
        valuesRecord(valImpl, recordParams1).returnTypeof(thisUsingJoined);
        valuesRecord(valImpl, recordParamsL).returnTypeof(thisUsingJoined);
        valuesRecord(valImpl, recordParamsN).returnTypeof(thisUsingJoined);

        // public int done() { return 0; }
        DeclMethod done = valImpl.publicMethod("done", DeclParams.of()).override();
        done.returnTypeof("int").returning("doInsert(getValues())");

        return valImpl;
    }

    private static DeclMethod valuesRecord(DeclJavaFile impl, DeclParams params) {
        String recordsName = String.join(", ", params.keySet());
        DeclMethod method = impl.publicMethod("valuesRecord", params).override();
        String addRecord = String2.format("requireAddRecord(this.getValues(), {})", recordsName);
        return method.scriptOf(addRecord).returning("this");
    }

    private static DeclJavaFile newAndImplInterfaces(
        String simpleName, MaxLengthVal maxLengthVal, Set<String> typesJoined, Map<String, DeclInterFile> interfaces
    ) {
        DeclJavaFile impl = DeclJavaFile.classOf(PACKAGE, simpleName);
        for (Map.Entry<String, DeclInterFile> colsInterface : interfaces.entrySet()) {
            DeclInterFile colInterface = colsInterface.getValue();
            String joined = colsInterface.getKey();
            impl.implement(toUsingByJoined(colInterface.getCanonicalName(), joined));
            maxLengthVal.setIfLonger(joined);
            typesJoined.add(joined);
        }
        return impl;
    }

    private static DeclParams declParamsForConfigTableFields() {
        String fieldsType = String2.format("{}<?, R, TB>[]", TableField.class.getCanonicalName());
        DeclParams constructParams = DeclParams.of("config", Configuration.class);
        constructParams.addGeneralization("table", "TB", Table.class);
        return constructParams.addActual("fields", fieldsType);
    }

    private static DeclParams declParamsBy(List<String> typesAll) {
        DeclParams params = DeclParams.of();
        for (String type : typesAll) {
            String trimmed = type.trim();
            params.addGeneralization(trimmed.toLowerCase(), trimmed, Object.class);
        }
        return params;
    }
}
