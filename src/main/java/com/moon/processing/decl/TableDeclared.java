package com.moon.processing.decl;

import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.annotation.TableModel;
import com.moon.accessor.annotation.TableModelPolicy;
import com.moon.accessor.annotation.Tables;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.processing.JavaDeclarable;
import com.moon.processing.JavaProvider;
import com.moon.processing.file.FileEnumImpl;
import com.moon.processing.file.JavaField;
import com.moon.processing.holder.PolicyHelper;
import com.moon.processing.holder.TableAlias;
import com.moon.processing.holder.TableModel2;
import com.moon.processor.def.Table2;
import com.moon.processor.utils.Comment2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Refer;
import com.moon.processor.utils.String2;

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
public class TableDeclared implements JavaProvider {

    private final VarHelper varHelper = new VarHelper();
    private final Set<String> allColumnsName;
    private final Collection<String> staticCols, memberCols;
    private final String packageName, simpleClassName;
    private final String tables;
    private final String tableName;
    private final String tableEnumVal;
    private final TableAlias tableAlias;
    private final TypeDeclared typeDeclared;
    private final Map<String, ColumnDeclared> columnDeclaredMap;

    private TableDeclared(PolicyHelper policyHelper, TypeDeclared typeDeclared) {
        final Supplier<String> tableEnumRef = this::getTableEnumVal;

        TypeElement thisElement = typeDeclared.getTypeElement();

        String simpleName = Element2.getSimpleName(thisElement);
        Tables tables = policyHelper.withTables(thisElement);
        TableModel tableModel = policyHelper.withTableModel(thisElement);
        TableModelPolicy modelPolicy = policyHelper.withModelPolicy(thisElement);
        TableFieldPolicy fieldPolicy = policyHelper.withFieldPolicy(thisElement);

        final Set<String> allColumnsName = typeDeclared.getAllPropertiesName();
        final List<String> staticCols = new ArrayList<>();
        final List<String> memberCols = new ArrayList<>();
        this.columnDeclaredMap = collectColumnMap(tableEnumRef, fieldPolicy, typeDeclared, staticCols, memberCols);

        allColumnsName.addAll(memberCols);
        allColumnsName.addAll(staticCols);
        String tableEnumVal = Table2.deduceTableField(allColumnsName);
        allColumnsName.add(tableEnumVal);

        this.typeDeclared = typeDeclared;
        this.allColumnsName = allColumnsName;
        this.tableEnumVal = tableEnumVal;
        this.staticCols = staticCols;
        this.memberCols = memberCols;

        this.tables = String2.isBlank(tables.value()) ? null : tables.value().trim();
        this.tableName = Table2.toDeclaredTableName(simpleName, modelPolicy, tableModel);

        this.tableAlias = TableModel2.parseAlias(tableModel);
        this.packageName = Element2.getPackageName(thisElement);
        this.simpleClassName = tableName.toUpperCase();
    }

    private final static Set<String> SORTS = new LinkedHashSet<>();

    static {
        SORTS.addAll(String2.split("id|name|idCard|idCardNo|username|logName|loginName|mobile|email", '|'));
    }

    private static Map<String, ColumnDeclared> collectColumnMap(
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

    public static TableDeclared of(PolicyHelper policyHelper, TypeDeclared typeDeclared) {
        return new TableDeclared(policyHelper, typeDeclared);
    }

    public String getTableClassname() { return packageName + "." + simpleClassName; }

    public String getTableEnumVal() { return tableEnumVal; }

    public String getTableEnumRef(String tableImported) {
        return tableImported + "." + getTableEnumVal();
    }

    public TableAlias getTableAlias() { return tableAlias; }

    public String getTableName() { return tableName; }

    public String getTablesFor() { return tables; }

    public String getModelClassname() {
        return getTypeDeclared().getTypeClassname();
    }

    public TypeDeclared getTypeDeclared() { return typeDeclared; }

    public ColumnDeclared getColumnDeclared(String propertyName) {
        return columnDeclaredMap.get(propertyName);
    }

    public <T> T reduce(BiConsumer<ColumnDeclared, T> consumer, T totalValue) {
        for (ColumnDeclared value : columnDeclaredMap.values()) {
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
        for (ColumnDeclared declared : columnDeclaredMap.values()) {
            declared.declareFinalField(enumFile, entityVar);
            firstRefer.setIfAbsent(declared.declareConstField(enumFile));
        }

        enumFile.privateFinalField(entityVar, "{}<{}>", Class.class, entityName)
            .valueOf(value -> value.classOf(entityName))
            .withForceInline();

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
        TableAlias alias = getTableAlias();
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
        enumFile.publicMethod("getTableFieldsCount").typeOf("int").override().returning(columnDeclaredMap.size());

        // getTableFields()
        enumFile.publicMethod("getTableFields")
            .override()
            .typeOf("{}<?, {}, {}>[]", TableField.class, entityName, enumFile.getClassname())
            .returnTypeFormatted("new {}[]{{}}",
                enumFile.onImported(TableField.class),
                columnDeclaredMap.values()
                    .stream()
                    .map(ColumnDeclared::getColumnName)
                    .collect(Collectors.joining(", ")));

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
