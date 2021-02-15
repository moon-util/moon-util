package com.moon.processing.decl;

import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.annotation.TableModel;
import com.moon.accessor.annotation.TableModelPolicy;
import com.moon.accessor.annotation.Tables;
import com.moon.processing.JavaDeclarable;
import com.moon.processing.JavaProvider;
import com.moon.processing.file.JavaEnumFile;
import com.moon.processing.holder.PolicyHelper;
import com.moon.processing.holder.TableAlias;
import com.moon.processing.holder.TableModel2;
import com.moon.processor.def.Table2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import java.util.*;

/**
 * 数据库表
 * <p>
 * 数据库表一般与一个模型类对应
 * <p>
 * 大多数情况下，与数据库字段对应的实体属性都会同时存在 field、setter、getter
 * 如果需要忽略某个字段可见忽略注解注解到字段（field）上，或者 setter、getter 上
 * <p>
 * 注解在 getter 上则不从数据库读取该字段的值
 * 注解在 setter 上则不会向数据库写该字段值
 * 以上两种情况还是会在生成字段时存在相应的字段，也就可以通过 DSL 读写该字段值
 * <p>
 * 但若在字段上注解了忽略，则不会生成该字段的映射，即不可通过 DSL 读写该字段
 * <p>
 * 当 getter/setter 只存在其一或两者忽略其一时，一般可能存在于数据库存在字段，但数据库自己会维护该字段值
 * 或者只向数据库写入值，缺不需要读取的情况，不过这种情况极少
 *
 * @author benshaoye
 */
public class TableDeclared implements JavaProvider {

    private final String packageName, simpleClassName;
    private final String tables;
    private final String tableName;
    private final String tableEnumVal;
    private final TableAlias tableAlias;
    private final TypeDeclared typeDeclared;
    private final Map<String, ColumnDeclared> columnDeclaredMap;

    private TableDeclared(PolicyHelper policyHelper, TypeDeclared typeDeclared) {
        this.typeDeclared = typeDeclared;

        TypeElement thisElement = typeDeclared.getTypeElement();

        String simpleName = Element2.getSimpleName(thisElement);
        Tables tables = policyHelper.withTables(thisElement);
        TableModel tableModel = policyHelper.withTableModel(thisElement);
        TableModelPolicy modelPolicy = policyHelper.withModelPolicy(thisElement);
        TableFieldPolicy fieldPolicy = policyHelper.withFieldPolicy(thisElement);

        final Set<String> columnsName = new HashSet<>();
        this.columnDeclaredMap = collectColumnMap(fieldPolicy, typeDeclared, columnsName);

        this.tables = String2.isBlank(tables.value()) ? null : tables.value().trim();
        this.tableName = Table2.toDeclaredTableName(simpleName, modelPolicy, tableModel);
        this.tableEnumVal = Table2.deduceTableField(columnsName);
        this.tableAlias = TableModel2.parseAlias(tableModel);
        this.packageName = Element2.getPackageName(thisElement);
        this.simpleClassName = tableName.toUpperCase();
    }

    private static Map<String, ColumnDeclared> collectColumnMap(
        TableFieldPolicy fieldPolicy, TypeDeclared typeDeclared, Set<String> columnsName
    ) {
        Map<String, ColumnDeclared> columnsMap = new LinkedHashMap<>();
        Map<String, PropertyDeclared> properties = typeDeclared.getCopiedProperties();
        for (Map.Entry<String, PropertyDeclared> propertyEntry : properties.entrySet()) {
            PropertyDeclared prop = propertyEntry.getValue();
            if (prop.isReadable() || prop.isWriteable()) {
                ColumnDeclared columnDeclared = new ColumnDeclared(fieldPolicy, prop);
                columnsMap.put(propertyEntry.getKey(), columnDeclared);
                columnsName.add(columnDeclared.getColumnName());
            }
        }
        return Collections.unmodifiableMap(columnsMap);
    }

    public static TableDeclared of(PolicyHelper policyHelper, TypeDeclared typeDeclared) {
        return new TableDeclared(policyHelper, typeDeclared);
    }

    public TableAlias getTableAlias() { return tableAlias; }

    public String getTableName() { return tableName; }

    public String getTablesFor() { return tables; }

    @Override
    public JavaDeclarable getJavaDeclare() {
        JavaEnumFile enumFile = new JavaEnumFile(packageName, simpleClassName);
        return enumFile;
    }
}
