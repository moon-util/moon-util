package com.moon.processor.holder;

import com.moon.accessor.config.DSLConfiguration;
import com.moon.accessor.dml.InsertIntoColsImpl;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.processor.file.*;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import java.util.*;

import static com.moon.processor.utils.String2.format;

/**
 * @author benshaoye
 */
enum InsertImpl2 {
    ;

    private final static String PACKAGE = Element2.getPackageName(InsertIntoColsImpl.class);
    private final static String COL_SIMPLE_NAME_PLACED = "InsertIntoCol{}Impl";
    private final static String VAL_SIMPLE_NAME_PLACED = "InsertIntoVal{}Impl";
    private final static String COL_CANONICAL_NAME_PLACED = PACKAGE + '.' + COL_SIMPLE_NAME_PLACED;
    private final static String VAL_CANONICAL_NAME_PLACED = PACKAGE + '.' + VAL_SIMPLE_NAME_PLACED;

    private static String getColImplSimpleName(int i) {
        return format(COL_SIMPLE_NAME_PLACED, i);
    }

    private static String getColImplCanonicalName(int i) {
        return format(COL_CANONICAL_NAME_PLACED, i);
    }

    private static String getValImplSimpleName(int i) {
        return format(VAL_SIMPLE_NAME_PLACED, i);
    }

    private static String getValImplCanonicalName(int i) {
        return format(VAL_CANONICAL_NAME_PLACED, i);
    }

    private static class MaxLengthVal {

        private String value;

        public void setIfLonger(String value) {
            if (String2.length(this.value) < String2.length(value)) {
                this.value = value;
            }
        }

        public String getValue() { return value; }
    }

    private static String toUsingByJoined(Class<?> cls, String joined) {
        return toUsingByJoined(cls.getCanonicalName(), joined);
    }

    private static String toUsingByJoined(String classname, String joined) {
        return classname + "<" + joined + ", R, TB>";
    }

    public static DeclJavaFile forColImpl(int endVal, Map<String, DeclInterFile> insertCols) {
        String simpleName = getColImplSimpleName(endVal);
        MaxLengthVal maxLengthVal = new MaxLengthVal();
        TreeSet<String> typesJoined = new TreeSet<>(Comparator.comparingInt(String2::length));

        // declare class and implements interfaces
        DeclJavaFile colImpl = newAndImplInterfaces(simpleName, maxLengthVal, typesJoined, insertCols);

        // extends superclass of: InsertIntoColsImpl<T1, T2>
        String usingJoined = maxLengthVal.getValue();
        colImpl.extend(toUsingByJoined(InsertIntoColsImpl.class, Holder2.TYPES_JOINED));

        // <T1, T2, R, TB>
        colImpl.genericOf(Holder2.toGenericDeclWithJoined(usingJoined), Table.class);

        // public InsertIntoCol2Impl(DSLConfiguration config, TB table, TableTable<?, R, TB>... fields) { super(config, table, fields); }
        publicConstruct(colImpl, false);

        // private InsertIntoVal2Impl<T1, T2, R, TB> insertValuesOf(List<Object[]> values) { /* ... */ }
        String insertValuesImpl = getValImplCanonicalName(endVal);
        String importedValImpl = colImpl.onImported(insertValuesImpl);
        String insertValuesJoined = toUsingByJoined(getValImplCanonicalName(endVal), usingJoined);
        DeclParams params = DeclParams.of("values", "java.lang.Object...");
        DeclMethod insertValuesOf = colImpl.publicMethod("insertValuesOf", params).withPrivate();
        insertValuesOf.returnTypeof(insertValuesJoined);
        insertValuesOf.returning("new {}(getConfig(), getTable(), getFields(), values)", importedValImpl);

        // public InsertIntoVal2Impl<T1, T2, R, TB> values(T1 t1, T2 t2) { /* ... */ }
        for (String joined : typesJoined) {
            List<String> typesAll = String2.split(joined, ',');
            DeclParams valuesParams = declParamsBy(typesAll);
            DeclMethod valuesMethod = colImpl.publicMethod("values", valuesParams);
            valuesMethod.override().returnTypeof(insertValuesJoined);
            String argsJoined = String.join(", ", valuesParams.keySet());
            valuesMethod.returning("insertValuesOf({})", argsJoined);
        }

        return colImpl;
    }

    public static DeclJavaFile forValImpl(int endVal, Map<String, DeclInterFile> insertVals) {
        String simpleName = getValImplSimpleName(endVal);
        MaxLengthVal maxLengthVal = new MaxLengthVal();
        TreeSet<String> typesJoined = new TreeSet<>(Comparator.comparingInt(String2::length));

        // declare class and implements interfaces
        DeclJavaFile valImpl = newAndImplInterfaces(simpleName, maxLengthVal, typesJoined, insertVals);

        String usingJoined = maxLengthVal.getValue();
        String superclass = getColImplCanonicalName(endVal);

        // <T1, T2, ... Tn,  R, TB>
        valImpl.genericOf(Holder2.toGenericDeclWithJoined(usingJoined), Table.class);

        // extends superclass of: InsertIntoCol2Impl<T1, T2>
        valImpl.extend(toUsingByJoined(superclass, usingJoined));

        // private final List<Object[]> values = new ArrayList<>(4);
        valImpl.privateField("values", "{}<{}[]>", List.class, Object.class)
            .withFinal()
            .withGetterMethod(m -> {})
            .valueOf("new {}<>({})", valImpl.onImported(ArrayList.class), 4);

        // constructor
        publicConstruct(valImpl, true);

        // public InsertIntoVal2Impl<T1, T2, R, TB> values(T1 t1, T2 t2) { /* ... */ }
        String thisUsingJoined = toUsingByJoined(valImpl.getCanonicalName(), usingJoined);
        for (String joined : typesJoined) {
            List<String> typesAll = String2.split(joined, ',');
            DeclParams valuesParams = declParamsBy(typesAll);
            DeclMethod valuesMethod = valImpl.publicMethod("values", valuesParams);
            valuesMethod.override().returnTypeof(thisUsingJoined);
            String argsJoined = String.join(", ", valuesParams.keySet());
            valuesMethod.scriptOf("requireAddAll(this.getValues(), {})", argsJoined);
            valuesMethod.returning("this");
        }

        // public int done() { return 0; }
        valImpl.publicMethod("done", DeclParams.of()).returnTypeof("int").returning("0").override();

        return valImpl;
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

    private static void publicConstruct(DeclJavaFile impl, boolean withValues) {
        String fieldsType = String2.format("{}<?, R, TB>[]", TableField.class.getCanonicalName());
        DeclParams constructParams = DeclParams.of("config", DSLConfiguration.class);
        constructParams.addGeneralization("table", "TB", Table.class);
        constructParams.addActual("fields", fieldsType);
        if (withValues) {
            constructParams.addActual("values", "java.lang.Object...");
        }
        DeclConstruct construct = impl.construct(constructParams);
        construct.scriptOf("super(config, table, fields)");
        if (withValues) {
            construct.scriptOf("requireAddAll(this.getValues(), values)");
        }
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
