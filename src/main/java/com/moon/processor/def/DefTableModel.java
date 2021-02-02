package com.moon.processor.def;

import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.annotation.TableModel;
import com.moon.accessor.annotation.TableModelPolicy;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclField;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.model.ValueRef;
import com.moon.processor.utils.Comment2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.String2;
import com.sun.istack.Nullable;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import java.util.*;

import static com.moon.processor.utils.String2.format;

/**
 * @author benshaoye
 */
public class DefTableModel implements JavaFileWriteable {

    private final Elements utils = Environment2.getUtils();

    private final Map<String, DefTableField> defFields;

    @Nullable
    private final String aliasGroup, alias, tables;
    /**
     * 类名，表格枚举字段名
     */
    private final String pkg, simpleClassName, tableEnumFieldVal;
    private final String pojoClassname, fullClassname;
    /**
     * 表名
     */
    private final String tableName;

    private final TableFieldPolicy columnPolicy;

    private final DeclJavaFile tableJava;

    public DefTableModel(
        DeclaredPojo declaredPojo, TableModelPolicy policy, TableFieldPolicy columnPolicy, TableModel tableModel
    ) {
        TypeElement thisElement = declaredPojo.getDeclareElement();
        String clsName = Element2.getSimpleName(thisElement);
        String tableName = Table2.toDeclaredTableName(clsName, policy, tableModel);

        String pkg = Element2.getPackageName(thisElement);
        String simpleClassName = tableName.toUpperCase();
        String tableEnumFieldVal = Table2.deduceTableField(declaredPojo);

        this.pkg = pkg;
        this.simpleClassName = simpleClassName;
        this.tableEnumFieldVal = tableEnumFieldVal;
        this.tableName = tableName;

        this.pojoClassname = declaredPojo.getThisClassname();
        this.fullClassname = pkg + '.' + simpleClassName;

        String[] aliases = Table2.toAliases(tableModel);
        this.aliasGroup = aliases[0];
        this.alias = aliases[1];
        this.tables = policy.tables();

        this.columnPolicy = columnPolicy;

        DeclJavaFile table = DeclJavaFile.enumOf(pkg, simpleClassName).enumNamesOf(tableEnumFieldVal);
        String tableType = Table.class.getCanonicalName() + "<{}, {}>";
        table.implement(format(tableType, getPojoClassname(), simpleClassName));
        this.defFields = defineTableModel(declaredPojo, table);
        this.tableJava = table;

        TypeElement elem = declaredPojo.getDeclareElement();
        declare(table, pojoClassname, simpleClassName);
        declareComment(table, elem);
    }

    private static String getTableFields(
        Map<String, DefTableField> defFields
    ) {
        int index = 0;
        String[] fields = new String[defFields.size()];
        for (Map.Entry<String, DefTableField> entry : defFields.entrySet()) {
            fields[index++] = entry.getValue().getFieldName();
        }
        return String.join(", ", fields);
    }

    private void declareComment(DeclJavaFile table, TypeElement elem) {
        DeclParams params = DeclParams.of();
        String comment = String2.strWrappedIfNonNull(Comment2.resolveComment(elem));
        String firstComment = String2.strWrappedIfNonNull(Comment2.resolveFirstComment(elem));
        String firstReturning = Objects.equals(comment, firstComment) ? "getComment()" : firstComment;
        table.publicMethod("getComment", params).override().returnTypeof(String.class).returning(comment);
        table.publicMethod("getFirstComment", params).override().returnTypeof(String.class).returning(firstReturning);
    }

    private void declare(DeclJavaFile table, String pojoClass, String simpleName) {
        DeclParams empty = DeclParams.of();

        // impl method of: getEntityClass
        String classType = Class.class.getCanonicalName() + "<{}>";
        String returnType = format(classType, pojoClass);
        DeclMethod getEntityClass = table.publicMethod("getEntityClass", empty).returnTypeof(returnType);
        getEntityClass.override().returning(table.onImported(pojoClass) + ".class");

        // impl method of: getEntityClassname
        DeclMethod getEntityClassname = table.publicMethod("getEntityClassname", empty).returnTypeof(String.class);
        getEntityClassname.override().returning(format("\"{}\"", pojoClass));

        // impl method of: getTableName
        DeclMethod getTableName = table.publicMethod("getTableName", empty).returnTypeof(String.class);
        getTableName.override().returning('"' + getTableName() + '"');

        // TableField<?, R, TB>[] getTableFields();
        DeclMethod getFields = table.publicMethod("getTableFields", empty).override();
        getFields.returnTypeof("{}<?, {}, {}>[]", TableField.class, pojoClass, simpleName);
        Object[] returning = {table.onImported(TableField.class), getTableFields(defFields)};
        getFields.returning("new {}[]{{}}", returning);

        // getTableFieldsCount
        DeclMethod getCount = table.publicMethod("getTableFieldsCount", empty).override();
        getCount.returnTypeof("int").returning(String.valueOf(defFields.size()));
    }

    private final static String[] SORTS = {
        "id", "name", "idCard", "idCardNo", "username", "logName", "loginName", "mobile", "email"
    };

    private Map<String, DefTableField> defineTableModel(
        DeclaredPojo declaredPojo, DeclJavaFile tableJava
    ) {
        final int size = declaredPojo.size();
        List<String> commentForAliases = new ArrayList<>(size);
        List<String> commentForFields = new ArrayList<>(size);
        List<String> commentForConsts = new ArrayList<>(size);
        Map<String, DeclareProperty> copiedPojoFields = new LinkedHashMap<>(declaredPojo);
        Map<String, DefTableField> definedTableFields = new LinkedHashMap<>();
        // 第一个字段
        final ValueRef<DeclField> refer = new ValueRef<>();

        // 优先字段
        for (String sort : SORTS) {
            DeclareProperty property = copiedPojoFields.remove(sort);
            if (property != null) {
                DefTableField defField = toDefTableField(sort, property);
                if (defField != null) {
                    refer.setIfAbsent(defField.declareTableField(tableJava, getCanonicalName()));
                    asColumnRef(defField, commentForAliases, commentForFields, commentForConsts);
                    definedTableFields.put(sort, defField);
                }
            }
        }
        for (Map.Entry<String, DeclareProperty> prop : copiedPojoFields.entrySet()) {
            DefTableField defField = toDefTableField(prop.getKey(), prop.getValue());
            if (defField != null) {
                refer.setIfAbsent(defField.declareTableField(tableJava, getCanonicalName()));
                asColumnRef(defField, commentForAliases, commentForFields, commentForConsts);
                definedTableFields.put(prop.getKey(), defField);
            }
        }
        refer.useIfPresent(first -> {
            // 表字段列举注释
            List<String> comments = new ArrayList<>(2 * (size + 1));
            comments.addAll(commentForFields);
            comments.add("");
            comments.addAll(commentForConsts);
            if (!commentForAliases.isEmpty()) {
                comments.add("");
                comments.addAll(commentForAliases);
            }
            first.commentOf(comments);
        });
        return definedTableFields;
    }

    private void asColumnRef(DefTableField field, List<String> aliases, List<String> fields, List<String> constants) {
        fields.add(toColumnRef(field.getFieldName()));
        constants.add(toConstRef(field.getConstName()));
        String aliasRef = toAliasRef(field.getFieldName());
        if (aliasRef != null) {
            aliases.add(aliasRef);
        }
    }

    private String toAliasRef(String columnName) {
        String group = getAliasGroup(), alias = getAlias();
        if (String2.isNotBlank(group) && String2.isNotBlank(alias)) {
            return format("{}.{}.{},", group, alias, columnName);
        }
        return null;
    }

    private String toColumnRef(String columnName) {
        return format("{}.{}.{},", getTables(), getTableName(), columnName);
    }

    private String toConstRef(String constName) { return format("{}.{},", getSimpleClassName(), constName); }

    private DefTableField toDefTableField(String propName, DeclareProperty property) {
        VariableElement element = property.getField();
        if (element == null) {
            return null;
        }
        com.moon.accessor.annotation.TableField fieldAnnotated//
            = Table2.getTableFieldAnnotation(element);

        // 列名
        String fieldName = Table2.toColumnName(fieldAnnotated, propName, columnPolicy);
        // 字段类型
        String propType = String2.toGeneralizableType(property.getActualType());

        String handlerType = Table2.getNullableTypeHandler(fieldAnnotated);

        String getterName = null, setterName = null;
        if (property.getGetter() != null) {
            getterName = property.getGetter().getName();
        }
        if (property.getSetter() != null) {
            setterName = property.getSetter().getName();
        }
        String constName = Table2.toRefColumnName(fieldName);
        String constValue = format("{}.{}", getTableEnumFieldVal(), fieldName);
        return new DefTableField(getPojoClassname(),
            propName,
            fieldName,
            constName,
            propType,
            handlerType,
            constValue,
            getterName,
            setterName,
            Comment2.resolveFirstComment(element),
            Comment2.resolveComment(element),
            fieldAnnotated);
    }

    public String getPkg() { return pkg; }

    public String getSimpleClassName() { return simpleClassName; }

    public String getTableEnumFieldVal() { return tableEnumFieldVal; }

    public String getPojoClassname() { return pojoClassname; }

    public String getAliasGroup() { return aliasGroup; }

    public String getAlias() { return alias; }

    public String getTables() { return tables; }

    public String getTableName() { return tableName; }

    public String getCanonicalName() { return fullClassname; }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        tableJava.writeJavaFile(writer);
    }
}
