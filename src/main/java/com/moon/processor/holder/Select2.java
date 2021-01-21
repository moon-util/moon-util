package com.moon.processor.holder;

import com.moon.accessor.Conditional;
import com.moon.accessor.dml.Done;
import com.moon.accessor.dml.InsertInto;
import com.moon.accessor.dml.InsertIntoValues;
import com.moon.accessor.meta.Table;
import com.moon.processor.file.DeclInterFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

/**
 * @author benshaoye
 */
enum Select2 {

    ;
    private static final String INSERT_INTO_PKG;
    private static final String INSERT_INTO;

    static {
        INSERT_INTO = String2.format("{}<R, TB>", InsertInto.class.getCanonicalName());
        INSERT_INTO_PKG = Element2.getPackageName(InsertInto.class);
    }

    static DeclInterFile getForSelectCols(String joined, int i, String genericUsingPlaced, DeclInterFile from) {
        String selectName = String2.format("SelectCol{}", i);
        DeclInterFile selectCols = DeclInterFile.interfaceOf(INSERT_INTO_PKG, selectName);
        selectCols.genericOf(String2.format("<{}>", joined));
        String implemented = String2.format(genericUsingPlaced, from.getCanonicalName());

        // from
        DeclParams params = DeclParams.of().addGeneralization("table", "TB", Table.class);
        DeclMethod fromMethod = selectCols.publicMethod("from", params);
        fromMethod.genericOf("<R, TB extends {}<R, TB>>", Table.class);
        fromMethod.returnTypeof(implemented);

        return selectCols;
    }

    static DeclInterFile getForFrom(
        String genericDeclPlaced, int i, String genericUsingPlaced, DeclInterFile where
    ) {
        DeclInterFile from = interfaceForN("SelectCol{}From", genericDeclPlaced, i);
        String implemented = String2.format(genericUsingPlaced, where.getCanonicalName());

        // orderBy
        DeclParams params = DeclParams.of("conditional", Conditional.class);
        from.publicMethod("where", params).returnTypeof(implemented);

        from.implement(implemented);
        return from;
    }

    static DeclInterFile getForWhere(
        String genericDeclPlaced, int i, String genericUsingPlaced, DeclInterFile groupBy
    ) {
        DeclInterFile where = interfaceForN("SelectCol{}Where", genericDeclPlaced, i);
        String implemented = String2.format(genericUsingPlaced, groupBy.getCanonicalName());

        DeclParams conditionalParams = DeclParams.of("conditional", Conditional.class);
        where.publicMethod("and", conditionalParams).returnTypeof(genericUsingPlaced, where.getCanonicalName());
        where.publicMethod("or", conditionalParams).returnTypeof(genericUsingPlaced, where.getCanonicalName());

        // groupBy
        DeclParams params = DeclParams.of();
        where.publicMethod("groupBy", params).returnTypeof(implemented);

        where.implement(implemented);
        return where;
    }

    static DeclInterFile getForGroupBy(
        String genericDeclPlaced, int i, String genericUsingPlaced, DeclInterFile orderBy, DeclInterFile having
    ) {
        DeclInterFile groupBy = interfaceForN("SelectCol{}GroupBy", genericDeclPlaced, i);
        String implemented = String2.format(genericUsingPlaced, orderBy.getCanonicalName());
        String havingImpl = String2.format(genericUsingPlaced, having.getCanonicalName());

        // orderBy
        DeclParams params = DeclParams.of();
        groupBy.publicMethod("orderBy", params).returnTypeof(implemented);

        // having
        groupBy.publicMethod("having", params).returnTypeof(havingImpl);

        groupBy.implement(implemented);
        return groupBy;
    }

    static DeclInterFile getForHaving(
        String genericDeclPlaced, int i, String genericUsingPlaced, DeclInterFile orderBy
    ) {
        DeclInterFile having = interfaceForN("SelectCol{}OrderBy", genericDeclPlaced, i);
        String implemented = String2.format(genericUsingPlaced, orderBy.getCanonicalName());

        // orderBy
        DeclParams params = DeclParams.of();
        having.publicMethod("orderBy", params).returnTypeof(implemented);

        having.implement(implemented);
        return having;
    }

    static DeclInterFile getForOrderBy(
        String genericDeclPlaced, int i, String genericUsingPlaced, DeclInterFile limit
    ) {
        DeclInterFile orderBy = interfaceForN("SelectCol{}OrderBy", genericDeclPlaced, i);
        String implemented = String2.format(genericUsingPlaced, limit.getCanonicalName());

        // limit 1
        DeclParams one = DeclParams.of("count", "int");
        orderBy.publicMethod("limit", one).returnTypeof(implemented);

        // limit 2
        DeclParams two = DeclParams.of("offset", "int").addActual("count", "int");
        orderBy.publicMethod("limit", two).returnTypeof(implemented);

        orderBy.implement(implemented);
        return orderBy;
    }

    static DeclInterFile getForLimit(String genericDeclPlaced, int i, String genericUsingPlaced) {
        return interfaceForN("SelectCol{}Limit", genericDeclPlaced, i);
    }

    private final static String DONE_CLASS = Done.class.getCanonicalName();
    private final static String INSERT_INTO_VAL_N_PLACED = "InsertIntoVal{}";

    static DeclInterFile forInsertCols(
        String genericDeclPlaced, int i, String genericUsingPlaced, DeclParams thisInterValuesArgs
    ) {
        String simpleName = String2.format(INSERT_INTO_VAL_N_PLACED, i);
        DeclInterFile inter = interfaceForN("InsertIntoCol{}", genericDeclPlaced, i);
        inter.implement(INSERT_INTO);

        // values
        DeclMethod values = inter.publicMethod("values", thisInterValuesArgs);
        values.returnTypeof(genericUsingPlaced, simpleName);

        DeclMethod record1 = inter.publicMethod("valuesRecord", Params2.declParamsForValuesRecord1());
        record1.returnTypeof(genericUsingPlaced, simpleName).override();
        DeclMethod recordC = inter.publicMethod("valuesRecord", Params2.declParamsForValuesRecordC());
        recordC.returnTypeof(genericUsingPlaced, simpleName).override();
        DeclMethod recordN = inter.publicMethod("valuesRecord", Params2.declParamsForValuesRecordN());
        recordN.returnTypeof(genericUsingPlaced, simpleName).override();

        return inter;
    }

    static DeclInterFile forInsertVals(
        String genericDeclPlaced, int i, String genericUsingPlaced
    ) {
        DeclInterFile inter = interfaceForN(INSERT_INTO_VAL_N_PLACED, genericDeclPlaced, i);
        String colsClassName = String2.format(genericUsingPlaced, getInsertIntoColsClassname(i));
        String insertIntoValues = String2.format("{}<R, TB>", InsertIntoValues.class.getCanonicalName());
        return inter.implement(insertIntoValues, colsClassName, DONE_CLASS);
    }

    private static String getInsertIntoColsClassname(int i) {
        return INSERT_INTO_PKG + ".InsertIntoCol" + i;
    }

    private static DeclInterFile interfaceForN(String interNamePattern, String genericDeclPlaced, int i) {
        String simpleName = String2.format(interNamePattern, i);
        DeclInterFile inter = DeclInterFile.interfaceOf(INSERT_INTO_PKG, simpleName);
        return inter.genericOf(genericDeclPlaced, Table.class);
    }
}
