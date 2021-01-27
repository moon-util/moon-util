package com.moon.accessor.meta;

import com.moon.accessor.PropertyGetter;
import com.moon.accessor.PropertySetter;

/**
 * @author benshaoye
 */
public class TableFieldDetail<T, R, TB extends Table<R, TB>> implements TableField<T, R, TB> {

    private final Class<R> domainClass;
    private final Class<T> propertyType;
    private final String propertyName;
    private final String columnName;
    private final String firstComment;
    private final String comment;
    private final String alias;
    private final TB table;
    private final PropertyGetter<R, T> getter;
    private final PropertySetter<R, T> setter;

    public TableFieldDetail(
        TB table,
        Class<R> domainClass,
        Class<T> propertyType,
        PropertyGetter<R, T> getter,
        PropertySetter<R, T> setter,
        String propertyName,
        String columnName,
        String firstComment,
        String comment
    ) { this(table, domainClass, propertyType, getter, setter, propertyName, columnName, firstComment,comment, null); }

    public TableFieldDetail(
        TB table,
        Class<R> domainClass,
        Class<T> propertyType,
        PropertyGetter<R, T> getter,
        PropertySetter<R, T> setter,
        String propertyName,
        String columnName,
        String firstComment,
        String comment,
        String alias
    ) {
        this.domainClass = domainClass;
        this.propertyType = propertyType;
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.firstComment = firstComment;
        this.comment = comment;
        this.alias = alias;
        this.table = table;
        this.getter = getter;
        this.setter = setter;
    }

    public TableFieldDetail(TableFieldDetail<T, R, TB> detail, String alias) {
        this(detail.table,
            detail.domainClass,
            detail.propertyType,
            detail.getter,
            detail.setter,
            detail.propertyName,
            detail.columnName,
            detail.firstComment,
            detail.comment,
            alias);
    }

    @Override
    public Class<R> getDomainClass() { return domainClass; }

    @Override
    public Class<T> getPropertyType() { return propertyType; }

    @Override
    public PropertyGetter<R, T> getPropertyGetter() { return getter; }

    @Override
    public PropertySetter<R, T> getPropertySetter() { return setter; }

    @Override
    public String getPropertyName() { return propertyName; }

    @Override
    public String getColumnName() { return columnName; }

    @Override
    public String getAliasName() { return alias; }

    @Override
    public TB getTable() { return table; }

    public String getFirstComment() { return firstComment == null ? getComment() : firstComment; }

    public String getComment() { return comment; }

    @Override
    public TableField<T, R, TB> as(String alias) { return new TableFieldDetail<>(this, alias); }
}
