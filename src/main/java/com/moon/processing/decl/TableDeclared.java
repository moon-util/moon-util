package com.moon.processing.decl;

import com.moon.processing.holder.TableAlias;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class TableDeclared extends TableAlias {

    private final TypeDeclared typeDeclared;
    private final Map<String, ColumnDeclared> columnDeclaredMap;

    private TableDeclared(TypeDeclared typeDeclared, String group, String name) {
        super(group, name);
        this.typeDeclared = typeDeclared;
        Map<String, ColumnDeclared> columnsMap = new LinkedHashMap<>();
        Map<String, PropertyDeclared> properties = typeDeclared.getCopiedProperties();
        for (Map.Entry<String, PropertyDeclared> propertyEntry : properties.entrySet()) {
            PropertyDeclared prop = propertyEntry.getValue();
            if (prop.isReadable() || prop.isWriteable()) {
                columnsMap.put(propertyEntry.getKey(), new ColumnDeclared(prop));
            }
        }
        this.columnDeclaredMap = Collections.unmodifiableMap(columnsMap);
    }

    public static TableDeclared of(TypeDeclared typeDeclared) {
        // Aliasing aliasing  = Table2.parseAlias()
        return new TableDeclared(typeDeclared, null, null);
    }

    public String getTableName() {
        throw new UnsupportedOperationException();
    }

    public String getTablesFor() {
        throw new UnsupportedOperationException();
    }
}
