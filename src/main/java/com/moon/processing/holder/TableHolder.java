package com.moon.processing.holder;

import com.moon.processing.JavaFiler;
import com.moon.processing.JavaWritable;
import com.moon.processing.decl.TableDeclared;
import com.moon.processing.decl.TypeDeclared;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class TableHolder implements JavaWritable {

    private final TypeHolder typeHolder;
    private final PolicyHelper policyHelper;
    private final TablesHolder tablesHolder;
    private final AliasesHolder aliasesHolder;
    private final Map<String, TableDeclared> tableDeclaredMap = new HashMap<>();

    public TableHolder(
        TypeHolder typeHolder, PolicyHelper policyHelper, TablesHolder tablesHolder, AliasesHolder aliasesHolder
    ) {
        this.policyHelper = policyHelper;
        this.tablesHolder = tablesHolder;
        this.aliasesHolder = aliasesHolder;
        this.typeHolder = typeHolder;
    }

    public TableDeclared with(TypeElement tableElement) {
        String classname = Element2.getQualifiedName(tableElement);
        TableDeclared tableDeclared = tableDeclaredMap.get(classname);
        return tableDeclared == null ? newTableDeclared(tableElement) : tableDeclared;
    }

    private TableDeclared newTableDeclared(TypeElement tableElement) {
        TypeDeclared typeDeclared = typeHolder.with(tableElement);
        TableDeclared tableDeclared = TableDeclared.of(policyHelper, typeDeclared);
        tableDeclaredMap.put(typeDeclared.getTypeClassname(), tableDeclared);
        // 处理表公共引用
        tablesHolder.with(tableDeclared);
        // 处理表别名
        aliasesHolder.with(tableDeclared);
        return tableDeclared;
    }

    @Override
    public void write(JavaFiler writer) {
        writer.write(tableDeclaredMap);
    }
}
