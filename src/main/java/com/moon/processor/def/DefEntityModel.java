package com.moon.processor.def;

import com.moon.accessor.annotation.CasePolicy;
import com.moon.accessor.annotation.TableColumnPolicy;
import com.moon.accessor.annotation.TablePolicy;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;
import com.moon.accessor.meta.TableFieldDetail;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.file.DeclMethod;
import com.moon.processor.file.DeclParams;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class DefEntityModel implements JavaFileWriteable {

    private final static String TABLE = "TABLE";
    private final static String DOT_CLS = ".class";

    private final TableColumnPolicy columnPolicy;
    private final DeclaredPojo declaredPojo;
    private final String pkg, classname, simpleClassname;
    private final String tableName, tableField, pojoClassname;

    public DefEntityModel(DeclaredPojo declaredPojo, TablePolicy policy, TableColumnPolicy columnPolicy) {
        this.declaredPojo = declaredPojo;
        TypeElement thisElement = declaredPojo.getDeclareElement();
        String clsName = Element2.getSimpleName(thisElement);

        String tableName = toDeclaredTableName(clsName, policy);
        this.tableName = tableName;
        this.simpleClassname = tableName.toUpperCase();
        this.tableField = deduceTableField(declaredPojo);
        this.pkg = Element2.getPackageName(thisElement);
        this.classname = pkg + "." + simpleClassname;
        this.pojoClassname = declaredPojo.getThisClassname();
        this.columnPolicy = columnPolicy;
    }

    private static String toDeclaredTableName(String entityName, TablePolicy policy) {
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
                resultName = String2.camelcaseToHyphen(inputName, '_', false);
                resultName = resultName.toLowerCase();
                break;
            case LOWERCASE:
                resultName = inputName.toLowerCase();
                break;
            case UNDERSCORE_UPPERCASE:
                resultName = String2.camelcaseToHyphen(inputName, '_', false);
                resultName = resultName.toUpperCase();
                break;
            case UPPERCASE:
                resultName = inputName.toUpperCase();
                break;
            default:
                resultName = inputName;
                break;
        }
        String effectPattern = String2.defaultIfBlank(pattern, "{}");
        return String2.replaceAll(effectPattern, "{}", resultName);
    }

    private String toColumnName(String propertyName) {
        return deducePlacedName(columnPolicy.casePolicy(), propertyName, columnPolicy.pattern());
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

    private String getPackage() { return pkg; }

    public String getClassname() { return classname; }

    public String getPojoClassname() { return pojoClassname; }

    public String getPkg() { return pkg; }

    public String getSimpleClassname() { return simpleClassname; }

    public String getTableName() { return tableName; }

    public String getTableField() { return tableField; }

    private void declareFields(DeclJavaFile tableFile) {
        declaredPojo.forEach((fieldName, prop) -> {
            if (prop.getField() == null) {
                return;
            }

            String tableFieldName = TableField.class.getCanonicalName();
            String importedPojoName = tableFile.onImported(getPojoClassname());
            String propertyType = String2.toGeneralizableType(prop.getActualType());
            String fieldType = String2
                .format("{}<{}, {}, {}>", tableFieldName, propertyType, getPojoClassname(), getClassname());
            String fieldNameString = String2.format("\"{}\"", fieldName);
            String columnName = toColumnName(fieldName);
            String columnNameString = String2.format("\"{}\"", columnName);
            String getter = null, setter = null;
            if (prop.getGetter() != null) {
                getter = String2.format("{}::{}", importedPojoName, prop.getGetter().getName());
            }
            if (prop.getSetter() != null) {
                setter = String2.format("{}::{}", importedPojoName, prop.getSetter().getName());
            }
            String fieldValue = String2.format("new {}<>(this, {}, {}, {}, {}, {}, {});",
                tableFile.onImported(TableFieldDetail.class.getCanonicalName()),
                String2.dotClass(importedPojoName),
                String2.dotClass(tableFile.onImported(propertyType)),
                getter,
                setter,
                fieldNameString,
                columnNameString);
            String constName = columnName.toUpperCase();
            String constValue = String2.format("{}.{}", getTableField(), columnName);
            tableFile.publicFinalField(columnName, fieldType).valueOf(fieldValue);
            tableFile.publicConstField(constName, fieldType).valueOf(constValue);
        });
    }

    private DeclJavaFile getTableDeclJavaFile() {
        // class declare
        String simpleName = getSimpleClassname();
        DeclJavaFile table = DeclJavaFile.enumOf(getPackage(), simpleName);
        String tableType = Table.class.getCanonicalName() + "<{}>";
        table.implement(String2.format(tableType, getPojoClassname()));

        table.enumNamesOf(getTableField());

        // 声明字段
        declareFields(table);

        DeclParams empty = DeclParams.of();
        // impl method of: getEntityType
        String classType = Class.class.getCanonicalName() + "<{}>";
        String returnType = String2.format(classType, getPojoClassname());
        DeclMethod getEntityType = table.publicMethod("getEntityType", empty).returnTypeof(returnType);
        getEntityType.override().returning(table.onImported(getPojoClassname()) + DOT_CLS);
        // impl method of: getEntityClassname
        DeclMethod getEntityClassname = table.publicMethod("getEntityClassname", empty).returnTypeof(String.class);
        getEntityClassname.override().returning(String2.format("\"{}\"", getPojoClassname()));
        // impl method of: getTableName
        DeclMethod getTableName = table.publicMethod("getTableName", empty).returnTypeof(String.class);
        getTableName.override().returning('"' + getTableName() + '"');

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
