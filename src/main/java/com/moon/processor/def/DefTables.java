package com.moon.processor.def;

import com.moon.accessor.meta.Table;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.utils.String2;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class DefTables implements JavaFileWriteable {

    private final static String PKG = Table.class.getPackage().getName();

    private final String classname, name;

    private final Set<DefEntityModel> modelSet = new LinkedHashSet<>();

    public DefTables(String name) {
        this.classname = String.join(".", PKG, name);
        this.name = name;
    }

    public void with(DefEntityModel entityModel) {
        modelSet.add(entityModel);
    }

    private DefJavaFiler getDefJavaFiler() {
        DefJavaFiler filer = DefJavaFiler.enumOf(PKG, classname).component(false);
        modelSet.forEach(entityModel -> {
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
