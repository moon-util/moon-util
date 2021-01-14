package com.moon.processor.def;

import com.moon.accessor.meta.Table;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.utils.String2;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DefTables implements JavaFileWriteable {

    private final static String PKG = Table.class.getPackage().getName();
    private final static String TABLE_NAME = Table.class.getCanonicalName();

    private final String classname, name;

    private final Map<String, DefEntityModel> modelSet = new LinkedHashMap<>();

    public DefTables(String name) {
        this.classname = String.join(".", PKG, name);
        this.name = name;
    }

    public void with(DefEntityModel entityModel) {
        String tableName = entityModel.getTableName();
        modelSet.put(tableName, entityModel);
    }

    private DeclJavaFile getDefJavaFiler() {
        DeclJavaFile javaFile = DeclJavaFile.enumOf(PKG, name);

        DefJavaFiler filer = DefJavaFiler.enumOf(PKG, classname).component(false);
        modelSet.forEach((name, entityModel) -> {
            String tableName = entityModel.getTableName();
            String tableType = entityModel.getClassname();
            String tableField = entityModel.getTableField();
            // String value = String2.format("{}.{};", filer.onImported(tableType), tableField);
            // filer.publicConstField(tableType, tableName, value);

            javaFile.publicConstField(tableName, tableType).valueOf("{}.{}", javaFile.onImported(tableType), tableField);
        });
        // impl method of: getTables
        String returning = String2.format("{}<?>[]", TABLE_NAME);
        DefMethod m = filer.publicMethod(true, "getTables", returning, DefParameters.of());
        String tables = String.join(", ", modelSet.keySet());
        m.returning(String2.format("new {}{{}};", filer.onImported(returning), tables));

        DeclMethod getTables = javaFile.publicMethod("getTables", DeclParams.of()).withStatic();
        getTables.returnTypeof("{}<?>[]",TABLE_NAME);
        getTables.returning("new {}<?>[]{{}}", javaFile.onImported(TABLE_NAME), tables);
        return javaFile;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        getDefJavaFiler().writeJavaFile(writer);
    }
}
