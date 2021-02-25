package com.moon.processing.holder;

import com.moon.processing.JavaFiler;
import com.moon.processing.JavaWritable;
import com.moon.processing.decl.TableDeclared;
import com.moon.processing.util.String2;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public class TablesHolder extends BaseHolder implements JavaWritable {

    /**
     * Tables 引用
     * <p>
     * Tables : t_user,t_order
     */
    private final Map<String, Set<String>> tablesMap = new HashMap<>();

    public TablesHolder(Holders holders) { super(holders); }

    public void with(TableDeclared tableDeclared) {// 处理表公共引用
        String tables = tableDeclared.getTablesFor();
        if (String2.isNotBlank(tables)) {
            Set<String> registeredTables = tablesMap.computeIfAbsent(tables, k -> new LinkedHashSet<>());
            registeredTables.add(tableDeclared.getTableName());
        }
    }

    @Override
    public void write(JavaFiler writer) {

    }
}
