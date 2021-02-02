package com.moon.accessor.meta;

import com.moon.accessor.PropertyGetter;
import com.moon.accessor.PropertySetter;
import com.moon.accessor.exception.Exception2;
import com.moon.accessor.type.TypeHandler;

import java.lang.reflect.Modifier;

import static com.moon.accessor.type.TypeHandlerRegistry.DEFAULT;

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

    private static <T> TypeHandler<T> usingHandler(Class<T> propertyType, Class<TypeHandler<T>> handlerClass) {
        if (handlerClass != null && !Modifier.isAbstract(handlerClass.getModifiers()) && !handlerClass.isInterface()) {
            try {
                return handlerClass.newInstance();
            } catch (InstantiationException e) {
                throw Exception2.with(e, "实例化错误: " + handlerClass.getCanonicalName());
            } catch (IllegalAccessException e) {
                throw Exception2.with(e, "不可访问: " + handlerClass.getCanonicalName());
            }
        }
        return DEFAULT.findFor(propertyType);
    }

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
        String comment
    ) {
        this(table,
            domainClass,
            propertyType,
            usingHandler(propertyType, handlerClass),
            getter,
            setter,
            propertyName,
            columnName,
            firstComment,
            comment,
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
        this.typeHandler = typeHandler;
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
