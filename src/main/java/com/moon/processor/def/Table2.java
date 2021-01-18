package com.moon.processor.def;

import com.moon.accessor.annotation.CasePolicy;
import com.moon.accessor.annotation.TableModel;
import com.moon.accessor.annotation.TableModelPolicy;
import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.VariableElement;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.moon.processor.utils.String2.camelcaseToHyphen;

/**
 * @author benshaoye
 */
enum Table2 {
    ;
    @SuppressWarnings("all")
    private final static Class<com.moon.accessor.annotation.TableField> TABLE_FIELD_CLASS//
        = com.moon.accessor.annotation.TableField.class;
    private final static String TABLE_FIELD_NAME = "TABLE";

    static String toRefColumnName(String column) {
        return String2.isAny(column, Character::isLowerCase) ? column.toUpperCase() : column.toLowerCase();
    }

    static String toDeclaredTableName(String entityName, TableModelPolicy policy, TableModel model) {
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

    static String deducePlacedName(CasePolicy policy, String inputName, String pattern) {
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

    static String toColumnName(VariableElement fieldDecl, String propertyName, TableFieldPolicy columnPolicy) {
        com.moon.accessor.annotation.TableField field = fieldDecl.getAnnotation(TABLE_FIELD_CLASS);
        if (field != null) {
            String name = field.name().trim();
            if (String2.isNotBlank(name)) {
                return name;
            }
        }
        return deducePlacedName(columnPolicy.casePolicy(), propertyName, columnPolicy.pattern());
    }

    static String deduceTableField(DeclaredPojo pojo) {
        Set<String> fields = new HashSet<>(pojo.keySet());
        Set<String> upperFields = fields.stream().map(String::toUpperCase).collect(Collectors.toSet());
        if (upperFields.contains(TABLE_FIELD_NAME)) {
            final String name = "DATA_" + TABLE_FIELD_NAME;
            String tableField = name;
            for (int i = 0; upperFields.contains(tableField); i++) {
                tableField = name + "_" + i;
            }
            return tableField;
        } else {
            return TABLE_FIELD_NAME;
        }
    }

    static String[] toAliases(TableModel model) {
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
}
