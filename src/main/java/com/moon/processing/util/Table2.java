package com.moon.processing.util;

import com.moon.accessor.annotation.table.CasePolicy;
import com.moon.accessor.annotation.table.TableFieldPolicy;
import com.moon.accessor.annotation.table.TableModel;
import com.moon.accessor.annotation.table.TableModelPolicy;
import com.moon.accessor.annotation.column.TableColumn;
import com.moon.accessor.type.TypeHandler;
import com.moon.processing.defaults.TableColumnEnum;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.moon.processing.util.String2.camelcaseToHyphen;

/**
 * @author benshaoye
 */
public enum Table2 {
    ;
    @SuppressWarnings("all")
    private final static Class<TableColumn> TABLE_FIELD_CLASS//
        = TableColumn.class;
    private final static String TABLE_FIELD_NAME = "TABLE", RECORD = "RECORD";
    final static String TYPE_HANDLER_NAME = TypeHandler.class.getCanonicalName();

    public static String toConstColumnName(String column) {
        return String2.isAny(column, Character::isLowerCase) ? column.toUpperCase() : column.toLowerCase();
    }

    public static String toDeclaredTableName(String entitySimpleName, TableModelPolicy policy, TableModel model) {
        if (String2.isNotBlank(model.name())) {
            return model.name().trim();
        }
        String[] prefixes = policy.trimEntityPrefix();
        String[] suffixes = policy.trimEntitySuffix();
        for (String prefix : prefixes) {
            if (entitySimpleName.startsWith(prefix)) {
                entitySimpleName = entitySimpleName.substring(prefix.length());
                break;
            }
        }
        for (String suffix : suffixes) {
            if (entitySimpleName.endsWith(suffix)) {
                entitySimpleName = entitySimpleName.substring(0, entitySimpleName.lastIndexOf(suffix));
                break;
            }
        }
        return deducePlacedName(policy.casePolicy(), entitySimpleName, policy.pattern());
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

    public static String getNullableTypeHandler(TableColumn field) {
        if (field == null) {
            return null;
        }
        String name = Element2.getClassname(field, TableColumn::typeHandler);
        TypeElement element = Processing2.getUtils().getTypeElement(name);
        if (element == null) {
            return null;
        }
        if (TYPE_HANDLER_NAME.equals(name)) {
            return null;
        }
        ElementKind kind = element.getKind();
        if (kind == ElementKind.INTERFACE) {
            throw new IllegalStateException("JDBC type handler can not be an interface: " + name);
        }
        if (kind == ElementKind.ANNOTATION_TYPE) {
            throw new IllegalStateException("JDBC type handler can not be an annotation: " + name);
        }
        if (kind == ElementKind.CLASS) {
            throw new IllegalStateException("JDBC type handler must be a class: " + name);
        }
        // future: record class
        if (RECORD.equals(kind.name())) {
            throw new IllegalStateException("JDBC type handler can not be a record: " + name);
        }
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.ABSTRACT)) {
            throw new IllegalStateException("JDBC type handler can not be an abstract class: " + name);
        }
        if (!modifiers.contains(Modifier.PUBLIC)) {
            throw new IllegalStateException("JDBC type handler must be modified by public: " + name);
        }
        return name;
    }

    public static TableColumn getTableFieldAnnotation(VariableElement element) {
        TableColumn fieldAnnotated//
            = element.getAnnotation(TABLE_FIELD_CLASS);
        return fieldAnnotated == null ? TableColumnEnum.INSTANCE : fieldAnnotated;
    }

    public static String toColumnName(
        TableColumn field, String propertyName, TableFieldPolicy columnPolicy
    ) {
        if (field != null) {
            String name = field.name().trim();
            if (String2.isNotBlank(name)) {
                return name;
            }
        }
        return deducePlacedName(columnPolicy.casePolicy(), propertyName, columnPolicy.pattern());
    }

    public static String deduceTableField(Set<String> columnsName) {
        Set<String> fields = new HashSet<>(columnsName);
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
}
