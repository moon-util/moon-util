package com.moon.processor.def;

import com.moon.accessor.meta.Table;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclField;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DefTables implements JavaFileWriteable {

    private final static String PKG = Table.class.getPackage().getName() + ".table";
    private final static String TABLE_NAME = Table.class.getCanonicalName();

    private final String name;

    private final Map<String, DefTableModel> modelSet = new LinkedHashMap<>();

    public DefTables(String name) { this.name = name; }

    public void with(DefTableModel model) {
        String tableName = model.getTableName();
        modelSet.put(tableName, model);
    }

    private DeclJavaFile getDefJavaFiler() {
        DeclJavaFile javaFile = DeclJavaFile.enumOf(PKG, name);

        modelSet.forEach((name, entityModel) -> {
            String tableName = entityModel.getTableName();
            String tableType = entityModel.getCanonicalName();
            String tableField = entityModel.getTableEnumFieldVal();

            DeclField field = javaFile.publicConstField(tableName, tableType);
            field.valueOf("{}.{}", javaFile.onImported(tableType), tableField);
        });
        // impl method of: getTables
        String tables = String.join(", ", modelSet.keySet());
        DeclMethod getTables = javaFile.publicMethod("getTables", DeclParams.of()).withStatic();
        getTables.returnTypeof("{}<?, ?>[]", TABLE_NAME);
        getTables.returning("new {}<?, ?>[]{{}}", javaFile.onImported(TABLE_NAME), tables);
        return javaFile;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        getDefJavaFiler().writeJavaFile(writer);
    }
}
