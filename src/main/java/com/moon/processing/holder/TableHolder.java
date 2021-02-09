package com.moon.processing.holder;

import com.moon.processing.decl.TableDeclared;
import com.moon.processing.decl.TypeDeclared;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import java.util.*;

/**
 * @author benshaoye
 */
public class TableHolder {

    private final TypeHolder typeHolder;

    private final Map<String, Set<String>> tablesMap = new HashMap<>();
    private final Map<String, Map<String, TableDeclared>> aliasesMap = new HashMap<>();
    private final Map<String, TableDeclared> tableDeclaredMap = new HashMap<>();

    public TableHolder(TypeHolder typeHolder) {
        this.typeHolder = typeHolder;
    }

    public TableDeclared with(TypeElement tableElement) {
        String classname = Element2.getQualifiedName(tableElement);
        TableDeclared tableDeclared = tableDeclaredMap.get(classname);
        return tableDeclared == null ? newTableDeclared(tableElement) : tableDeclared;
    }

    private TableDeclared newTableDeclared(TypeElement tableElement) {
        TypeDeclared typeDeclared = typeHolder.with(tableElement);
        TableDeclared tableDeclared = TableDeclared.of(typeDeclared);

        // 处理表公共引用
        String tables = tableDeclared.getTablesFor();
        Set<String> registeredTables = tablesMap.get(tables);
        if (registeredTables == null) {
            tablesMap.put(tables, registeredTables = new LinkedHashSet<>());
        }
        registeredTables.add(tableDeclared.getTableName());

        // 处理表别名
        String name = tableDeclared.getAliasName();
        if (String2.isNotBlank(name)) {
            // 并非所有表都需要别名，但有别名的一定存在别名组
            String aliasGroup = tableDeclared.getAliasGroup();
            Map<String, TableDeclared> aliasDeclaredMap = aliasesMap.get(aliasGroup);
            if (aliasDeclaredMap == null) {
                aliasDeclaredMap = new LinkedHashMap<>();
                aliasesMap.put(aliasGroup, aliasDeclaredMap);
            }
            aliasDeclaredMap.put(name, tableDeclared);
        }

        return tableDeclared;
    }
}
