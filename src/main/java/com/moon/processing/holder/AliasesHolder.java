package com.moon.processing.holder;

import com.moon.processing.JavaFiler;
import com.moon.processing.JavaWritable;
import com.moon.processing.decl.TableDeclared;
import com.moon.processor.utils.String2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class AliasesHolder implements JavaWritable {

    /**
     * 表别名
     * <p>
     * 别名组 : 别名 : 实际表引用
     * Aliases : user = t_user
     */
    private final Map<String, Map<String, TableDeclared>> aliasesMap = new HashMap<>();

    public AliasesHolder() {}

    public void with(TableDeclared tableDeclared) {
        TableAlias alias = tableDeclared.getTableAlias();
        String name = alias.getAliasName();
        if (String2.isNotBlank(name)) {
            // 并非所有表都需要别名，但有别名的一定存在别名组
            String aliasGroup = alias.getAliasGroup();
            Map<String, TableDeclared> aliasDeclaredMap = aliasesMap
                .computeIfAbsent(aliasGroup, k -> new LinkedHashMap<>());
            aliasDeclaredMap.put(name, tableDeclared);
        }
    }

    @Override
    public void write(JavaFiler writer) {

    }
}