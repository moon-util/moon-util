package com.moon.processor.def;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class TablesManager implements JavaFileWriteable {

    private final Map<String, DefTables> tablesMap = new HashMap<>();

    public TablesManager() {
    }

    private DefTables getTables(TypeElement thisElement) {
        // 默认 Tables 将来可通过注解自定义，
        // 沿着 thisElement 父类向上追寻，返回第一个声明的 tables 名称，
        String tablesName = "Tables";
        DefTables tables = tablesMap.get(tablesName);
        if (tables == null) {
            tables = new DefTables(tablesName);
            tablesMap.put(tablesName, tables);
        }
        return tables;
    }

    public void with(TypeElement thisElement, DefEntityModel entityModel) {
        getTables(thisElement).with(entityModel);
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        tablesMap.values().forEach(tables -> tables.writeJavaFile(writer));
    }
}
