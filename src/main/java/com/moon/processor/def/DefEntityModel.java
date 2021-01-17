package com.moon.processor.def;

import com.moon.accessor.annotation.CasePolicy;
import com.moon.accessor.annotation.TableEntity;
import com.moon.accessor.annotation.TableEntityPolicy;
import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.accessor.meta.TableFieldDetail;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclField;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.model.ValueRef;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;
import java.util.stream.Collectors;

import static com.moon.processor.utils.String2.camelcaseToHyphen;

/**
 * @author benshaoye
 */
public class DefEntityModel implements JavaFileWriteable {

    private final static Class<com.moon.accessor.annotation.TableField> TABLE_FIELD_CLASS
        = com.moon.accessor.annotation.TableField.class;

    private final static String TABLE = "TABLE";
    private final static String DOT_CLS = ".class";

    private final TableFieldPolicy columnPolicy;
    private final DeclaredPojo declaredPojo;
    private final String pkg, classname, simpleClassname;
    private final String tables, tableName, tableField, pojoClassname;
    private final String aliasGroup, alias;

    public DefEntityModel(
        DeclaredPojo declaredPojo, TableEntityPolicy policy, TableEntity tableModel, TableFieldPolicy columnPolicy
    ) {
        this.declaredPojo = declaredPojo;
        TypeElement thisElement = declaredPojo.getDeclareElement();
        String clsName = Element2.getSimpleName(thisElement);
        String tableName = toDeclaredTableName(clsName, policy, tableModel);

        this.tables = policy.tables();
        this.tableName = tableName;
        this.simpleClassname = tableName.toUpperCase();
        this.tableField = deduceTableField(declaredPojo);
        this.pkg = Element2.getPackageName(thisElement);
        this.classname = pkg + "." + simpleClassname;
        this.pojoClassname = declaredPojo.getThisClassname();
        this.columnPolicy = columnPolicy;

        String[] aliases = toAliases(tableModel);
        this.aliasGroup = aliases[0];
        this.alias = aliases[1];
    }

    private static String[] toAliases(TableEntity model) {
        String alias = model.alias().trim();
        if (String2.isBlank(alias)) {
            return new String[]{null, null};
        }
        int idx = alias.indexOf('.');
        if (idx < 0) {
            String name = toQualifiedAlias(alias, alias);
            return new String[]{"Aliases", name};
        } else {
            requiredAlias(alias.indexOf('.', idx + 1) > 0, alias);
            String namespace = toQualifiedAlias(alias.substring(0, idx), alias);
            String aliasName = toQualifiedAlias(alias.substring(idx + 1), alias);
            return new String[]{namespace, aliasName};
        }
    }

    private static String toQualifiedAlias(String name, String source) {
        String resultName = name.trim();
        requiredAlias(!Test2.isVar(resultName), source);
        return resultName.trim();
    }

    private static void requiredAlias(boolean tested, String alias) {
        if (tested) {
            String info = String2.format("错误表别名: {}", alias);
            throw new IllegalStateException(info);
        }
    }

    private static String toDeclaredTableName(String entityName, TableEntityPolicy policy, TableEntity model) {
        if (String2.isNotBlank(model.name())) {
            return model.name().trim();
        }
        String[] prefixes = policy.trimEntityPrefix();
        String[] suffixes = policy.trimEntitySuffix();
        for (String prefix : prefixes) {
            if (entityName.startsWith(prefix)) {
                entityName = entityName.substring(prefix.length());
                break;
            }
        }
        for (String suffix : suffixes) {
            if (entityName.endsWith(suffix)) {
                entityName = entityName.substring(0, entityName.lastIndexOf(suffix));
                break;
            }
        }
        return deducePlacedName(policy.casePolicy(), entityName, policy.pattern());
    }

    private static String deducePlacedName(CasePolicy policy, String inputName, String pattern) {
        String resultName;
        switch (policy) {
            case UNDERSCORE_LOWERCASE:
                resultName = camelcaseToHyphen(inputName, '_', false);
                resultName = resultName.toLowerCase();
                break;
            case UNDERSCORE_UPPERCASE:
                resultName = camelcaseToHyphen(inputName, '_', false);
                resultName = resultName.toUpperCase();
                break;
            case LOWERCASE:
                resultName = inputName.toLowerCase();
                break;
            case UPPERCASE:
                resultName = inputName.toUpperCase();
                break;
            case UNDERSCORE:
                resultName = camelcaseToHyphen(inputName, '_', false, false);
                break;
            default:
                resultName = inputName;
                break;
        }
        String effectPattern = String2.defaultIfBlank(pattern, "{}");
        return String2.replaceAll(effectPattern, "{}", resultName);
    }

    private String toColumnName(VariableElement fieldDecl, String propertyName) {
        com.moon.accessor.annotation.TableField field = fieldDecl.getAnnotation(TABLE_FIELD_CLASS);
        if (field != null) {
            String name = field.name().trim();
            if (String2.isNotBlank(name)) {
                return name;
            }
        }
        return deducePlacedName(columnPolicy.casePolicy(), propertyName, columnPolicy.pattern());
    }

    private static String toRefColumnName(String column) {
        return String2.isAny(column, Character::isLowerCase) ? column.toUpperCase() : column.toLowerCase();
    }

    private static String deduceTableField(DeclaredPojo pojo) {
        Set<String> fields = new HashSet<>(pojo.keySet());
        Set<String> upperFields = fields.stream().map(String::toUpperCase).collect(Collectors.toSet());
        if (upperFields.contains(TABLE)) {
            final String name = "DATA_" + TABLE;
            String tableField = name;
            for (int i = 0; upperFields.contains(tableField); i++) {
                tableField = name + "_" + i;
            }
            return tableField;
        } else {
            return TABLE;
        }
    }

    public String getAliasGroup() { return aliasGroup; }

    public String getAlias() { return alias; }

    public String getTables() { return tables; }

    private String getPackage() { return pkg; }

    public String getClassname() { return classname; }

    public String getPojoClassname() { return pojoClassname; }

    public String getPkg() { return pkg; }

    public String getSimpleClassname() { return simpleClassname; }

    public String getTableName() { return tableName; }

    public String getTableField() { return tableField; }

    private final static String[] SORTS = {
        "id", "name", "idCard", "idCardNo", "username", "logName", "loginName", "mobile", "email"
    };

    private Map<String, DeclareProperty> sortedProps() {
        DeclaredPojo pojo = this.declaredPojo;
        LinkedHashMap<String, DeclareProperty> props = new LinkedHashMap<>();
        for (String sort : SORTS) {
            DeclareProperty prop = pojo.get(sort);
            if (prop != null) {
                props.put(sort, prop);
            }
        }
        for (Map.Entry<String, DeclareProperty> propertyEntry : pojo.entrySet()) {
            props.putIfAbsent(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return props;
    }

    private List<String> declareGetMemberFields(DeclJavaFile tableFile) {
        List<String> constFields = new ArrayList<>();
        List<String> tableFields = new ArrayList<>();

        final ValueRef<DeclField> refer = new ValueRef<>();
        final String pojoClass = getPojoClassname();
        final String importedPojo = tableFile.onImported(pojoClass);
        final String tableFieldName = TableField.class.getCanonicalName();
        String importedTbFieldDetail = tableFile.onImported(TableFieldDetail.class);
        sortedProps().forEach((fieldName, prop) -> {
            VariableElement propField = prop.getField();
            if (propField == null) { return; }

            String propertyType = String2.toGeneralizableType(prop.getActualType());
            String fieldType = String2
                .format("{}<{}, {}, {}>", tableFieldName, propertyType, pojoClass, getClassname());
            String fieldStringify = String2.strWrapped(fieldName);
            String columnName = toColumnName(propField, fieldName);
            String columnStringify = String2.strWrapped(columnName);
            String getter = null, setter = null;
            if (prop.getGetter() != null) {
                getter = String2.format("{}::{}", importedPojo, prop.getGetter().getName());
            }
            if (prop.getSetter() != null) {
                setter = String2.format("{}::{}", importedPojo, prop.getSetter().getName());
            }
            String fieldValue = String2.format("new {}<>(this, {}, {}, {}, {}, {}, {});",
                importedTbFieldDetail,
                String2.dotClass(importedPojo),
                String2.dotClass(tableFile.onImported(propertyType)),
                getter,
                setter,
                fieldStringify,
                columnStringify);
            String constName = toRefColumnName(columnName);
            String constValue = String2.format("{}.{}", getTableField(), columnName);
            tableFile.publicFinalField(columnName, fieldType).valueOf(fieldValue);

            refer.setIfAbsent(tableFile.publicConstField(constName, fieldType).valueOf(constValue));

            tableFields.add(columnName);
            constFields.add(constName);
        });
        refer.useIfPresent(first -> {
            // 表字段列举注释
            List<String> comments = new ArrayList<>();
            for (String field : tableFields) {
                comments.add(toColumnRef(field));
            }
            comments.add("");
            for (String field : constFields) {
                comments.add(toConstRef(field));
            }
            first.commentOf(comments);
        });
        return constFields;
    }

    private String toColumnRef(String columnName) {
        return String2.format("{}.{}.{},", getTables(), getTableName(), columnName);
    }

    private String toConstRef(String constName) {
        return String2.format("{}.{},", getSimpleClassname(), constName);
    }

    private DeclJavaFile getTableDeclJavaFile() {
        // class declare
        String pojoClass = getPojoClassname();
        String simpleName = getSimpleClassname();
        DeclJavaFile table = DeclJavaFile.enumOf(getPackage(), simpleName);
        String tableType = Table.class.getCanonicalName() + "<{}, {}>";
        table.implement(String2.format(tableType, pojoClass, simpleName));

        table.enumNamesOf(getTableField());

        // 声明字段
        List<String> tableFields = declareGetMemberFields(table);

        DeclParams empty = DeclParams.of();

        // impl method of: getEntityClass
        String classType = Class.class.getCanonicalName() + "<{}>";
        String returnType = String2.format(classType, pojoClass);
        DeclMethod getEntityClass = table.publicMethod("getEntityClass", empty).returnTypeof(returnType);
        getEntityClass.override().returning(table.onImported(pojoClass) + DOT_CLS);

        // impl method of: getEntityClassname
        DeclMethod getEntityClassname = table.publicMethod("getEntityClassname", empty).returnTypeof(String.class);
        getEntityClassname.override().returning(String2.format("\"{}\"", pojoClass));

        // impl method of: getTableName
        DeclMethod getTableName = table.publicMethod("getTableName", empty).returnTypeof(String.class);
        getTableName.override().returning('"' + getTableName() + '"');

        // TableField<?, R, TB>[] getTableFields();
        DeclMethod getFields = table.publicMethod("getTableFields", empty).override();
        getFields.returnTypeof("{}<?, {}, {}>[]", TableField.class, pojoClass, simpleName);
        Object[] returning = {table.onImported(TableField.class), String.join(", ", tableFields)};
        getFields.returning("new {}[]{{}}", returning);

        // getTableFieldsCount
        DeclMethod getCount = table.publicMethod("getTableFieldsCount", empty).override();
        getCount.returnTypeof("int").returning(String.valueOf(tableFields.size()));
        return table;
    }

    private List<DeclJavaFile> getDeclJavaFileAll() {
        List<DeclJavaFile> filers = new ArrayList<>();
        filers.add(getTableDeclJavaFile());
        return filers;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        for (DeclJavaFile filer : getDeclJavaFileAll()) {
            filer.writeJavaFile(writer);
        }
    }
}
