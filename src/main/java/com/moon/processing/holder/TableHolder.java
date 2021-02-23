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
        TypeHolder typeHolder, TablesHolder tablesHolder, AliasesHolder aliasesHolder
    ) {
        this.policyHelper = new PolicyHelper();
        this.tablesHolder = tablesHolder;
        this.aliasesHolder = aliasesHolder;
        this.typeHolder = typeHolder;
    }

    public TableDeclared get(String classname) { return tableDeclaredMap.get(classname); }

    public TableDeclared get(TypeElement tableElement) { return get(Element2.getQualifiedName(tableElement)); }

    public TableDeclared with(TypeElement tableElement) {
        TableDeclared tableDeclared = get(tableElement);
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
        writer.write(tableDeclaredMap.values());
    }
}