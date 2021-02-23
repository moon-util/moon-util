package com.moon.processing.decl;

import com.moon.accessor.annotation.TableField;
import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.meta.TableFieldDetail;
import com.moon.accessor.type.JdbcType;
import com.moon.processing.file.BaseImportable;
import com.moon.processing.file.FileEnumImpl;
import com.moon.processing.file.JavaField;
import com.moon.processor.def.Table2;
import com.moon.processor.utils.Comment2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.VariableElement;
import java.util.Objects;
import java.util.function.Supplier;

import static com.moon.processor.utils.String2.format;

/**
 * @author benshaoye
 */
public class ColumnDeclared extends BaseColumnDeclared {

    /** 表名 */
    private final String thisClassname;
    private final Supplier<String> tableEnumRef;
    private final String fieldName;
    private final String columnName;
    private final String constColumnName;
    private final String fieldClass;
    private final String nullableHandlerClass;
    private final String getterName, setterName;
    private final TableFieldPolicy fieldPolicy;
    private final TableField tableField;
    private final PropertyDeclared property;
    private final String firstComment, comment;

    private ColumnDeclared(
        final Supplier<String> tableEnumRef, String fieldClass, TableFieldPolicy fieldPolicy, PropertyDeclared property
    ) {
        this.thisClassname = property.getThisClassname();
        this.tableEnumRef = tableEnumRef;
        this.fieldPolicy = fieldPolicy;
        this.property = property;

        PropertyFieldDeclared fieldDeclared = property.getFieldDeclared();
        VariableElement element = fieldDeclared.getFieldElement();
        TableField tableField = Table2.getTableFieldAnnotation(element);

        this.tableField = tableField;
        this.fieldName = fieldDeclared.getName();
        this.columnName = Column2.deduceTableColumnName(fieldPolicy, fieldName, property);
        this.fieldClass = fieldClass;
        this.constColumnName = Table2.toConstColumnName(columnName);
        this.nullableHandlerClass = Table2.getNullableTypeHandler(tableField);

        String getterName = null, setterName = null;
        if (property.getGetterMethod() != null) {
            getterName = property.getGetterMethod().getMethodName();
        }
        if (property.getSetterMethod() != null) {
            setterName = property.getSetterMethod().getMethodName();
        }

        this.getterName = getterName;
        this.setterName = setterName;
        this.firstComment = Comment2.resolveFirstComment(element);
        this.comment = Comment2.resolveComment(element);
    }

    public static ColumnDeclared nullableOf(
        final Supplier<String> tableEnumRef, TableFieldPolicy fieldPolicy, PropertyDeclared property
    ) {
        PropertyFieldDeclared fieldDeclared = property.getFieldDeclared();
        if (fieldDeclared == null) {
            return null;
        }
        VariableElement element = fieldDeclared.getFieldElement();
        if (element == null) {
            return null;
        }
        // 字段类型
        String fieldClass = String2.toGeneralizableType(property.getActualType());
        if (fieldClass == null) {
            return null;
        }
        return new ColumnDeclared(tableEnumRef, fieldClass, fieldPolicy, property);
    }

    public String getConstColumnRef() { return format("{}.{}", getTableEnumVal(), columnName); }

    public String getColumnName() { return columnName; }

    public boolean isWriteable() { return property.isWriteable(); }

    public boolean isReadable() { return property.isReadable(); }

    public String getThisClassname() { return thisClassname; }

    public String getTableEnumVal() { return tableEnumRef.get(); }

    public String getFieldName() { return fieldName; }

    public String getConstColumnName() { return constColumnName; }

    public String getFieldClass() { return fieldClass; }

    public String getNullableHandlerClass() { return nullableHandlerClass; }

    public String getGetterName() { return getterName; }

    public String getSetterName() { return setterName; }

    public TableFieldPolicy getFieldPolicy() { return fieldPolicy; }

    public PropertyDeclared getProperty() { return property; }

    public String getFirstComment() { return firstComment; }

    public String getComment() { return comment; }

    public String getGetterRef(BaseImportable importable) {
        String getterName = getGetterName();
        if (getterName == null) {
            return null;
        }
        String imported = importable.onImported(getProperty().getThisClassname());
        return String2.format("{}::{}", imported, getterName);
    }

    public String getSetterRef(BaseImportable importable) {
        String setterName = getSetterName();
        if (setterName == null) {
            return null;
        }
        String imported = importable.onImported(getProperty().getThisClassname());
        return String2.format("{}::{}", imported, setterName);
    }

    public String getJdbcTypeRef(BaseImportable importable) {
        return format("{}.{}", importable.onImported(JdbcType.class), tableField.jdbcType());
    }

    public String getFieldLength() { return String.valueOf(tableField.length()); }

    public String getFieldPrecision() { return String.valueOf(tableField.precision()); }

    public void declareFinalField(FileEnumImpl enumFile, String entityVar) {
        enumFile.privateFinalField(columnName,
            "{}<{}, {}, {}>",
            TABLE_FIELD_CLASS,
            getFieldClass(),
            getThisClassname(),
            enumFile.getClassname()).valueOf(value -> value.formattedOf(NEW_TABLE_FIELD,
            value.onImported(TableFieldDetail.class),
            entityVar,
            classRef(value, getFieldClass()),
            classRef(value, getNullableHandlerClass()),
            getGetterRef(value),
            getSetterRef(value),
            stringOf(getFieldName()),
            stringOf(getColumnName()),
            getJdbcTypeRef(value),
            getFieldLength(),
            getFieldPrecision(),
            stringOf(getFirstComment()),
            stringOf(getComment()))).withPublic();
    }

    public JavaField declareConstField(FileEnumImpl enumFile) {
        JavaField constField = enumFile.privateConstField(getConstColumnName(),
            "{}<{}, {}, {}>",
            TABLE_FIELD_CLASS,
            getFieldClass(),
            getThisClassname(),
            enumFile.getClassname());
        constField.valueOf(v -> v.thisEnumRef(getTableEnumVal(), columnName)).withPublic();
        return constField;
    }

    private static String stringOf(String value) { return value == null ? "null" : ("\"" + value + "\""); }

    private static String classRef(BaseImportable importable, String classname) {
        return classname == null ? null : (importable.onImported(classname) + ".class");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ColumnDeclared that = (ColumnDeclared) o;
        return Objects.equals(getThisClassname(), that.getThisClassname()) &&
            Objects.equals(getFieldName(), that.getFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getThisClassname(), getFieldName());
    }
}
