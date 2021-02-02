package com.moon.accessor.meta;

import com.moon.accessor.PropertyGetter;
import com.moon.accessor.PropertySetter;
import com.moon.accessor.type.JdbcType;
import com.moon.accessor.type.TypeHandler;

import static com.moon.accessor.type.TypeHandlerRegistry.findEffectiveJdbcType;
import static com.moon.accessor.type.TypeHandlerRegistry.findEffectiveHandler;

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
    private final TypeHandler<T> typeHandler;
    private final JdbcType columnJdbcType;
    private final int columnLength;
    private final int columnPrecision;

    public TableFieldDetail(
        TB table,
        Class<R> domainClass,
        Class<T> propertyType,
        Class<TypeHandler<T>> handlerClass,
        PropertyGetter<R, T> getter,
        PropertySetter<R, T> setter,
        String propertyName,
        String columnName,
        String firstComment,
        String comment,
        JdbcType columnJdbcType,
        int columnLength,
        int columnPrecision
    ) {
        this(table,
            domainClass,
            propertyType,
            findEffectiveHandler(handlerClass, propertyType, columnJdbcType),
            getter,
            setter,
            propertyName,
            columnName,
            firstComment,
            comment,
            findEffectiveJdbcType(propertyType, columnJdbcType),
            columnLength,
            columnPrecision,
            null);
    }

    public TableFieldDetail(
        TB table,
        Class<R> domainClass,
        Class<T> propertyType,
        TypeHandler<T> typeHandler,
        PropertyGetter<R, T> getter,
        PropertySetter<R, T> setter,
        String propertyName,
        String columnName,
        String firstComment,
        String comment,
        JdbcType columnJdbcType,
        int columnLength,
        int columnPrecision,
        String alias
    ) {
        assert columnJdbcType != JdbcType.AUTO : "不准确的 Jdbc 类型:" + columnJdbcType;
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
        this.typeHandler = typeHandler;
        this.columnJdbcType = columnJdbcType;
        this.columnLength = columnLength;
        this.columnPrecision = columnPrecision;
    }

    public TableFieldDetail(TableFieldDetail<T, R, TB> detail, String alias) {
        this(detail.table,
            detail.domainClass,
            detail.propertyType,
            detail.typeHandler,
            detail.getter,
            detail.setter,
            detail.propertyName,
            detail.columnName,
            detail.firstComment,
            detail.comment,
            detail.columnJdbcType,
            detail.columnLength,
            detail.columnPrecision,
            alias);
    }

    @Override
    public Class<R> getDomainClass() { return domainClass; }

    @Override
    public Class<T> getPropertyType() { return propertyType; }

    @Override
    public TypeHandler<T> getTypeHandler() { return typeHandler; }

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

    /**
     * {@inheritDoc}
     *
     * @return 该字段下的第一行注释
     *
     * @see #getComment() 该字段所有文档注释
     */
    @Override
    public String getFirstComment() { return firstComment == null ? getComment() : firstComment; }

    /**
     * {@inheritDoc}
     *
     * @return 该字段的文档注释
     *
     * @see #getFirstComment() 该字段的第一行注释
     */
    @Override
    public String getComment() { return comment; }

    @Override
    public TableField<T, R, TB> as(String alias) { return new TableFieldDetail<>(this, alias); }
}
