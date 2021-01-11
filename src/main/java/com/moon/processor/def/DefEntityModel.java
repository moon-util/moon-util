package com.moon.processor.def;

import com.moon.accessor.annotation.TablePolicy;
import com.moon.accessor.meta.Field;
import com.moon.accessor.meta.FieldDetail;
import com.moon.accessor.meta.Table;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Holder;
import com.moon.processor.utils.HolderGroup;
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
    private final static String TABLE_PREFIX = "TB_";
    private final static String DOT_CLS = ".class";

    private final DefParameters EMPTY = DefParameters.of();
    private final DeclaredPojo declaredPojo;
    private final String pkg, classname, simpleClassname;
    private final String tableName, tableField, pojoClassname;

    public DefEntityModel(DeclaredPojo declaredPojo, TablePolicy policy) {
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
        String tableName;
        switch (policy.caseMode()) {
            case UNDERSCORE_LOWERCASE:
                tableName = String2.camelcaseToHyphen(entityName, '_', false);
                tableName = tableName.toLowerCase();
                break;
            case LOWERCASE:
                tableName = entityName.toLowerCase();
                break;
            case UNDERSCORE_UPPERCASE:
                tableName = String2.camelcaseToHyphen(entityName, '_', false);
                tableName = tableName.toUpperCase();
                break;
            case UPPERCASE:
                tableName = entityName.toUpperCase();
                break;
            default:
                tableName = entityName;
                break;
        }
        String pattern = String2.defaultIfBlank(policy.pattern(), "{}");
        return String2.replaceAll(pattern, "{}", tableName);
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

    private final static HolderGroup GROUP = Holder.of(Holder.type, Holder.type0, Holder.type1, Holder.name);

    private void declareFields(DefJavaFiler tableFiler) {
        declaredPojo.forEach((name, prop) -> {
            if (prop.getField() == null) {
                return;
            }
            String propertyType = String2.toGeneralizableType(prop.getActualType());
            String fieldType = String2.format("{}<{}, {}, {}>",
                Field.class.getCanonicalName(),
                propertyType,
                getPojoClassname(),
                getClassname());
            String fieldNameString = String2.format("\"{}\"", name);
            String fieldValue = String2.format("new {}<>(this, {}, {}, {}, {});",
                tableFiler.onImported(FieldDetail.class.getCanonicalName()),
                tableFiler.onImported(getPojoClassname()) + DOT_CLS,
                tableFiler.onImported(propertyType) + DOT_CLS,
                fieldNameString,
                fieldNameString);
            String fieldName = String2.camelcaseToHyphen(name, '_', false).toUpperCase();
            String finalName = String2.format("{}.{}", getTableField(), name);
            tableFiler.publicFinalField(fieldType, name, fieldValue);
            tableFiler.publicConstField(fieldType, fieldName, finalName);
        });
    }

    private DefJavaFiler getTableDefJavaFiler() {
        // class declare
        String classname = getClassname();
        DefJavaFiler table = DefJavaFiler.enumOf(getPackage(), classname).component(false);
        String tableType = Table.class.getCanonicalName() + "<{}>";
        table.implement(String2.format(tableType, getPojoClassname()));

        // String tableConst = String2.format("new {}()", table.onImported(classname));
        // table.publicConstField(classname, getTableField(), tableConst);
        table.enumsOf(getTableField());

        // 声明字段
        declareFields(table);

        // impl method of: getEntityType
        String classType = Class.class.getCanonicalName() + "<{}>";
        String returnType = String2.format(classType, getPojoClassname());
        DefMethod getEntityType = table.publicMethod(false, "getEntityType", returnType, EMPTY);
        getEntityType.override().returning(table.onImported(getPojoClassname()) + DOT_CLS);
        // impl method of: getTableName
        DefMethod getTableName =table.publicMethod("getTableName", String.class, EMPTY);
        getTableName.override().returning('"' + getTableName() + '"');

        return table;
    }

    private List<DefJavaFiler> getDefJavaFilerAll() {
        List<DefJavaFiler> filers = new ArrayList<>();
        filers.add(getTableDefJavaFiler());
        return filers;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        for (DefJavaFiler filer : getDefJavaFilerAll()) {
            filer.writeJavaFile(writer);
        }
    }
}
