package com.moon.processing.decl;

import com.moon.accessor.annotation.TableField;
import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.processor.def.Table2;

import javax.lang.model.element.VariableElement;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author benshaoye
 */
public enum Column2 {
    ;

    private final static Map<PropertyDeclared, String> COLUMN_NAME_MAP = new WeakHashMap<>();

    public static String deduceTableColumnName(
        TableFieldPolicy fieldPolicy, String propName, PropertyDeclared property
    ) {
        String name = COLUMN_NAME_MAP.get(property);
        if (name != null) {
            return name;
        }
        PropertyFieldDeclared fieldDeclared = property.getFieldDeclared();
        if (fieldDeclared == null) {
            return null;
        }
        VariableElement element = fieldDeclared.getFieldElement();
        if (element == null) {
            return null;
        }
        TableField fieldAnnotated = Table2.getTableFieldAnnotation(element);
        String columnName = Table2.toColumnName(fieldAnnotated, propName, fieldPolicy);
        COLUMN_NAME_MAP.put(property, columnName);
        return columnName;
    }
}
