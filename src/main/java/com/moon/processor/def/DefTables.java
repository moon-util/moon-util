package com.moon.processor.def;

import com.moon.accessor.meta.Table;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.utils.String2;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DefTables implements JavaFileWriteable {

    private final static String PKG = Table.class.getPackage().getName();

    private final String classname;

    private final Map<String, DefEntityModel> modelSet = new LinkedHashMap<>();

    public DefTables(String name) {
        this.classname = String.join(".", PKG, name);
    }

    public void with(DefEntityModel entityModel) {
        String tableName = entityModel.getTableName();
        modelSet.put(tableName, entityModel);
    }

    private DefJavaFiler getDefJavaFiler() {
        DefJavaFiler filer = DefJavaFiler.enumOf(PKG, classname).component(false);
        modelSet.forEach((name, entityModel) -> {
            String tableName = entityModel.getTableName();
            String tableType = entityModel.getClassname();
            String tableField = entityModel.getTableField();
            String value = String2.format("{}.{};", filer.onImported(tableType), tableField);
            filer.publicConstField(tableType, tableName, value);
        });
        return filer;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        getDefJavaFiler().writeJavaFile(writer);
    }
}
