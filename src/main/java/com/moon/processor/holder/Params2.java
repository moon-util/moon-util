package com.moon.processor.holder;

import com.moon.processor.file.DeclParams;

/**
 * @author benshaoye
 */
enum Params2 {
    ;

    static DeclParams declParamsForValuesRecord1() {
        return DeclParams.of().addGeneralization("record", "R", Object.class);
    }

    static DeclParams declParamsForValuesRecordC() {
        return DeclParams.of().addActual("records", "java.util.Collection<? extends R>");
    }

    static DeclParams declParamsForValuesRecordN() {
        DeclParams recordParamsN = DeclParams.of();
        recordParamsN.addGeneralization("record1", "R", Object.class);
        recordParamsN.addGeneralization("record2", "R", Object.class);
        return recordParamsN.addGeneralization("records", "R...", Object.class);
    }
}
