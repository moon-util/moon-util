package com.moon.processing.decl;

import com.moon.accessor.annotation.domain.TableColumn;
import com.moon.accessor.annotation.domain.TableFieldPolicy;
import com.moon.accessor.annotation.domain.TableModel;
import com.moon.accessor.annotation.domain.TableModelPolicy;
import com.moon.accessor.annotation.domain.Tables;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.processing.JavaDeclarable;
import com.moon.processing.JavaProvider;
import com.moon.processing.file.FileEnumImpl;
import com.moon.processing.file.JavaField;
import com.moon.processing.holder.BaseHolder;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.*;

import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
public class TableDeclared extends BaseHolder implements JavaProvider {

    private final VarHelper varHelper = new VarHelper();
    /** 所有字段名 + 静态列名 + 成员列名 + 表名的枚举 & 所有字段名 */
    private final Set<String> allColumnsName, propertiesName;
    /** 所有列名 & 成员列名 */
    private final Collection<String> staticCols, memberCols;
    /** 所属{@link Tables} & 实际表名 & 表名枚举值 */
    private final String tables, tableName, tableEnumVal;
    /** 生成的表的报名，类名（{@link #tableName}的大写） */
    private final String packageName, simpleClassName;
    /** 别名: aliasGroup.alias */
    private final AliasDeclared aliasDeclared;
    private final TypeDeclared typeDeclared;
    /** propertyName : columnDeclared */
    private final Map<String, ColumnDeclared> columnsMap;

    private TableDeclared(Holders holders, TypeDeclared typeDeclared) {
        super(holders);
        final Supplier<String> tableEnumRef = this::getTableEnumVal;

        TypeElement thisElement = typeDeclared.getTypeElement();

        String simpleName = Element2.getSimpleName(thisElement);
        Tables tables = policyHelper().withTables(thisElement);
        TableModel tableModel = policyHelper().withTableModel(thisElement);
        TableModelPolicy modelPolicy = policyHelper().withModelPolicy(thisElement);
        TableFieldPolicy fieldPolicy = policyHelper().withFieldPolicy(thisElement);

        final Set<String> propertiesName = typeDeclared.getAllPropertiesName();
        final Set<String> allColumnsName = new LinkedHashSet<>(propertiesName);
        final List<String> staticColsName = new ArrayList<>();
        final List<String> memberColsName = new ArrayList<>();
        this.columnsMap = collectColsMap(tableEnumRef, fieldPolicy, typeDeclared, staticColsName, memberColsName);

        allColumnsName.addAll(memberColsName);
        allColumnsName.addAll(staticColsName);
        String tableEnumVal = Table2.deduceTableField(allColumnsName);
        allColumnsName.add(tableEnumVal);

        this.allColumnsName = allColumnsName;
        this.propertiesName = propertiesName;
        this.staticCols = staticColsName;
        this.memberCols = memberColsName;
        this.tableEnumVal = tableEnumVal;
        this.typeDeclared = typeDeclared;

        this.tables = String2.isBlank(tables.value()) ? null : tables.value().trim();
        this.tableName = Table2.toDeclaredTableName(simpleName, modelPolicy, tableModel);

        this.aliasDeclared = AliasDeclared.parseAlias(tableModel);
        this.packageName = Element2.getPackageName(thisElement);
        this.simpleClassName = tableName.toUpperCase();
    }

    private final static Set<String> SORTS = new LinkedHashSet<>();

    static {
        SORTS.addAll(String2.split("id|primaryKey|name|idCard|idCardNo|username|logName|loginName|mobile|email", '|'));
    }

    private static Map<String, ColumnDeclared> collectColsMap(
        final Supplier<String> tableEnumRef,
        TableFieldPolicy fieldPolicy,
        TypeDeclared typeDeclared,
        Collection<String> staticCols,
        Collection<String> memberCols
    ) {
        // 定义数据表列
        Map<String, ColumnDeclared> declaredMap = new LinkedHashMap<>();
        Map<String, PropertyDeclared> properties = typeDeclared.getCopiedProperties();
        for (Map.Entry<String, PropertyDeclared> propertyEntry : properties.entrySet()) {
            PropertyDeclared prop = propertyEntry.getValue();
            if (!prop.isReadable() && !prop.isWriteable()) {
                continue;
            }
            PropertyFieldDeclared field = prop.getFieldDeclared();
            if (field == null) {
                // 字段不存在的情况不考虑
                continue;
            } else if (field.isStatic() || field.isTransient()) {
                // 静态字段 & transient 不是数据库列
                continue;
            }
            TableColumn tableColumn = field.getFieldAnnotation(TableColumn.class);
            if (tableColumn != null && tableColumn.ignored()) {
                // 被忽略的字段考虑
                continue;
            }
            ColumnDeclared column = ColumnDeclared.nullableOf(tableEnumRef, fieldPolicy, prop);
            if (column != null) {
                declaredMap.put(propertyEntry.getKey(), column);
            }
        }

        // 排序
        Map<String, ColumnDeclared> columnsMap = new LinkedHashMap<>();
        for (String sortField : SORTS) {
            ColumnDeclared declared = declaredMap.get(sortField);
            if (declared != null) {
                columnsMap.put(sortField, declared);
                staticCols.add(declared.getConstColumnName());
                memberCols.add(declared.getColumnName());
            }
        }

        for (Map.Entry<String, ColumnDeclared> declaredEntry : declaredMap.entrySet()) {
            if (columnsMap.containsKey(declaredEntry.getKey())) {
                continue;
            }
            ColumnDeclared declared = declaredEntry.getValue();
            columnsMap.put(declaredEntry.getKey(), declared);
            staticCols.add(declared.getConstColumnName());
            memberCols.add(declared.getColumnName());
        }

        return Collections.unmodifiableMap(columnsMap);
    }

    public static TableDeclared of(Holders holders, TypeDeclared typeDeclared) {
        return new TableDeclared(holders, typeDeclared);
    }

    public String getTableClassname() { return packageName + "." + simpleClassName; }

    public String getTableEnumVal() { return tableEnumVal; }

    public String getTableEnumRef(String tableImported) { return tableImported + "." + getTableEnumVal(); }

    public AliasDeclared getAliasDeclared() { return aliasDeclared; }

    public String getTableName() { return tableName; }

    public String getTablesFor() { return tables; }

    public String getModelClassname() {
        return getTypeDeclared().getTypeClassname();
    }

    public TypeDeclared getTypeDeclared() { return typeDeclared; }

    public ColumnDeclared getColumnDeclared(String propertyName) { return columnsMap.get(propertyName); }

    public <T> T reduce(BiConsumer<ColumnDeclared, T> consumer, T totalValue) {
        for (ColumnDeclared value : columnsMap.values()) {
            consumer.accept(value, totalValue);
        }
        return totalValue;
    }

    @Override
    public JavaDeclarable getJavaDeclare() {
        FileEnumImpl enumFile = new FileEnumImpl(packageName, simpleClassName);
        enumFile.enumOf(tableEnumVal);

        enumFile.implementOf("{}<{}, {}>", Table.class, typeDeclared.getTypeClassname(), enumFile.getClassname());

        declareTableColumns(enumFile);
        overrideTableMethods(enumFile);
        return enumFile;
    }

    private void declareTableColumns(FileEnumImpl enumFile) {
        final String entityName = typeDeclared.getTypeClassname();
        final String entityVar = varHelper.next(allColumnsName);

        Refer<JavaField> firstRefer = Refer.of();
        for (ColumnDeclared declared : columnsMap.values()) {
            declared.declareFinalField(enumFile, entityVar);
            firstRefer.setIfAbsent(declared.declareConstField(enumFile));
        }

        enumFile.privateFinalField(entityVar, "{}<{}>", Class.class, entityName)
            .valueOf(value -> value.classOf(entityName))
            .withForceInline();

        // 字段快捷引用注释块儿
        firstRefer.useIfPresent(field -> {
            List<String> comments = new ArrayList<>();
            List<String> staticComments = new ArrayList<>();
            List<String> tablesComments = new ArrayList<>();

            for (String staticCol : this.staticCols) {
                staticComments.add(toConstRef(staticCol));
            }

            for (String memberCol : this.memberCols) {
                String tablesRef = toTablesRef(memberCol);
                if (String2.isNotBlank(tablesRef)) {
                    tablesComments.add(tablesRef);
                }
                String aliasRef = toAliasRef(memberCol);
                if (String2.isNotBlank(aliasRef)) {
                    comments.add(aliasRef + ',');
                }
            }

            if (!comments.isEmpty()) {
                comments.add("");
            }
            comments.addAll(staticComments);

            if (!comments.isEmpty()) {
                comments.add("");
            }
            comments.addAll(tablesComments);

            field.blockCommentOf(comments.toArray());
        });
    }

    private String toTablesRef(String name) {
        String tables = getTablesFor();
        if (String2.isNotBlank(tables)) {
            return tables + '.' + getTableName() + '.' + name + ',';
        }
        return null;
    }

    private String toConstRef(String name) { return simpleClassName + '.' + name + ','; }

    private String toAliasRef(String name) {
        AliasDeclared alias = getAliasDeclared();
        return alias == null ? null : alias.toAliasRef(name);
    }

    private void overrideTableMethods(FileEnumImpl enumFile) {
        TypeElement typeElement = typeDeclared.getTypeElement();
        String entityName = typeDeclared.getTypeClassname();

        // getEntityClass()
        enumFile.publicMethod("getEntityClass")
            .typeOf("{}<{}>", Class.class, entityName)
            .override()
            .returning(enumFile.onImported(entityName) + ".class");

        // getTableFieldsCount()
        enumFile.publicMethod("getTableFieldsCount").typeOf("int").override().returning(columnsMap.size());

        // getTableFields()
        enumFile.publicMethod("getTableFields")
            .override()
            .typeOf("{}<?, {}, {}>[]", TableField.class, entityName, enumFile.getClassname())
            .returnTypeFormatted("new {}[]{{}}", enumFile.onImported(TableField.class),

                columnsMap.values().stream().map(ColumnDeclared::getColumnName).collect(Collectors.joining(", ")));

        // getTableName()
        enumFile.publicMethod("getTableName")
            .typeOf(String.class)
            .override()
            .returnTypeFormatted("\"{}\"", getTableName());

        // getComment() & getFirstComment()
        String comment = Comment2.resolveComment(typeElement);
        String firstComment = Comment2.resolveFirstComment(typeElement);
        enumFile.publicMethod("getComment").override().typeOf(String.class).returnStringify(comment);
        if (!Objects.equals(firstComment, comment)) {
            enumFile.publicMethod("getFirstComment").override().typeOf(String.class).returnStringify(firstComment);
        }
    }
}
