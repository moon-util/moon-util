package com.moon.processor.manager;

import com.moon.accessor.annotation.TablePolicy;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefEntityModel;
import com.moon.processor.def.DefTables;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class TablesManager implements JavaFileWriteable {

    private final PolicyManager policyManager;
    private final Map<String, DefTables> tablesMap = new HashMap<>();

    public TablesManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    private DefTables getTables(TypeElement thisElement) {
        // 默认 Tables 将来可通过注解自定义，
        // 沿着 thisElement 父类向上追寻，返回第一个声明的 tables 名称，
        TablePolicy policy = policyManager.with(thisElement);
        String tablesName = policy.tables().trim();
        if (String2.isBlank(tablesName)) {
            return null;
        }
        DefTables tables = tablesMap.get(tablesName);
        if (tables == null) {
            tables = new DefTables(tablesName);
            tablesMap.put(tablesName, tables);
        }
        return tables;
    }

    public void with(TypeElement thisElement, DefEntityModel entityModel) {
        DefTables tables = getTables(thisElement);
        if (tables != null) {
            tables.with(entityModel);
        }
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        tablesMap.values().forEach(tables -> tables.writeJavaFile(writer));
    }
}
