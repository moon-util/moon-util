package com.moon.processor.def;

import com.moon.accessor.meta.TableField;
import com.moon.accessor.meta.TableFieldDetail;
import com.moon.accessor.type.JdbcType;
import com.moon.processor.file.DeclField;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.utils.String2;

import java.util.Objects;

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
    private final String handlerType;
    private final String constValue;
    private final String getterName;
    private final String setterName;
    private final String firstComment;
    private final String comment;
    private final String stringifyPropName;
    private final String stringifyFieldName;
    private final com.moon.accessor.annotation.TableField field;

    public DefTableField(
        String pojoClass,
        String propName,
        String fieldName,
        String constName,
        String propType,
        String handlerType,
        String constValue,
        String getterName,
        String setterName,
        String firstComment,
        String comment,
        com.moon.accessor.annotation.TableField field
    ) {
        this.pojoClass = pojoClass;
        this.propName = propName;
        this.fieldName = fieldName;
        this.constName = constName;
        this.propType = propType;
        this.handlerType = handlerType;
        this.constValue = constValue;
        this.getterName = getterName;
        this.setterName = setterName;
        this.firstComment = firstComment;
        this.comment = comment;
        this.stringifyPropName = String2.strWrapped(propName);
        this.stringifyFieldName = String2.strWrapped(fieldName);
        this.field = field;
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
        String firstComment = getFirstComment(), comment = getDeclareComment();
        return format("new {}<>(this, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {});",
            table.onImported(TableFieldDetail.class),
            String2.dotClass(importedPojo),
            String2.dotClass(table.onImported(propType)),
            getUsableHandlerType(table, handlerType),
            getGetterRef(importedPojo),
            getSetterRef(importedPojo),
            getStringifyPropName(),
            getStringifyFieldName(),
            Objects.equals(firstComment, comment) ? null : firstComment,
            getDeclareComment(),
            format("{}.{}", table.onImported(JdbcType.class), field.jdbcType()),
            field.length(),
            field.precision());
    }

    private String getUsableHandlerType(DeclJavaFile table, String handlerType) {
        return String2.isBlank(handlerType) ? null : String2.dotClass(table.onImported(handlerType));
    }

    private String getFirstComment() { return toComment(this.firstComment); }

    private String getDeclareComment() { return toComment(this.comment); }

    private static String toComment(String comment) {
        if (comment == null) {
            return null;
        }
        if (comment.charAt(0) == '"' && comment.charAt(comment.length() - 1) == '"') {
            return comment;
        }
        return String2.strWrapped(comment);
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
