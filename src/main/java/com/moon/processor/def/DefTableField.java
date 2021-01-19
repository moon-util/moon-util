package com.moon.processor.def;

import com.moon.accessor.meta.TableField;
import com.moon.accessor.meta.TableFieldDetail;
import com.moon.processor.file.DeclField;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.holder.Importable;
import com.moon.processor.utils.String2;

import static com.moon.processor.utils.String2.format;

/**
 * @author benshaoye
 */
public class DefTableField {

    private final String pojoClass;
    private final String propName;
    private final String fieldName;
    private final String constName;
    private final String propType;
    private final String constValue;
    private final String getterName;
    private final String setterName;
    private final String stringifyPropName;
    private final String stringifyFieldName;

    public DefTableField(
        String pojoClass,
        String propName,
        String fieldName,
        String constName,
        String propType,
        String constValue,
        String getterName,
        String setterName
    ) {
        this.pojoClass = pojoClass;
        this.propName = propName;
        this.fieldName = fieldName;
        this.constName = constName;
        this.propType = propType;
        this.constValue = constValue;
        this.getterName = getterName;
        this.setterName = setterName;
        this.stringifyPropName = String2.strWrapped(propName);
        this.stringifyFieldName = String2.strWrapped(fieldName);
    }

    public String getPropName() { return propName; }

    public String getFieldName() { return fieldName; }

    public String getConstName() { return constName; }

    public String getPropType() { return propType; }

    private String getStringifyPropName() { return stringifyPropName; }

    private String getStringifyFieldName() { return stringifyFieldName; }

    public DeclField declareTableField(DeclJavaFile table, String tableClassname) {
        String fieldType = getFieldType(tableClassname);
        table.publicFinalField(getFieldName(), fieldType).valueOf(getFieldValue(table));
        return table.publicConstField(getConstName(), fieldType).valueOf(constValue);
    }

    private static final String FIELD_CLASS_NAME = TableField.class.getCanonicalName();

    private String getFieldType(String classname) {
        return format("{}<{}, {}, {}>", FIELD_CLASS_NAME, getPropType(), pojoClass, classname);
    }

    private String getFieldValue(DeclJavaFile table) {
        String importedPojo = table.onImported(pojoClass);
        return format("new {}<>(this, {}, {}, {}, {}, {}, {});",
            table.onImported(TableFieldDetail.class),
            String2.dotClass(importedPojo),
            String2.dotClass(table.onImported(propType)),
            getGetterRef(importedPojo),
            getSetterRef(importedPojo),
            getStringifyPropName(),
            getStringifyFieldName());
    }

    private String getGetterRef(String importedPojo) {
        String getterName = this.getterName;
        if (String2.isBlank(getterName)) {
            return null;
        }
        return String2.format("{}::{}", importedPojo, getterName);
    }

    private String getSetterRef(String importedPojo) {
        String setterName = this.setterName;
        if (String2.isBlank(setterName)) {
            return null;
        }
        return String2.format("{}::{}", importedPojo, setterName);
    }
}
