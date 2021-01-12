package com.moon.accessor.meta;

/**
 * @author benshaoye
 */
public class FieldDetail<T, R, TB extends Table<R>> implements Field<T, R, TB> {

    private final Class<R> domainClass;
    private final Class<T> propertyType;
    private final String propertyName;
    private final String columnName;
    private final String alias;
    private final TB table;
    private final PropertyGetter<R, T> getter;
    private final PropertySetter<R, T> setter;

    public FieldDetail(
        TB table,
        Class<R> domainClass,
        Class<T> propertyType,
        PropertyGetter<R, T> getter,
        PropertySetter<R, T> setter,
        String propertyName,
        String columnName
    ) { this(table, domainClass, propertyType, getter, setter, propertyName, columnName, null); }

    public FieldDetail(
        TB table,
        Class<R> domainClass,
        Class<T> propertyType,
        PropertyGetter<R, T> getter,
        PropertySetter<R, T> setter,
        String propertyName,
        String columnName,
        String alias
    ) {
        this.domainClass = domainClass;
        this.propertyType = propertyType;
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.alias = alias;
        this.table = table;
        this.getter = getter;
        this.setter = setter;
    }

    public FieldDetail(FieldDetail<T, R, TB> detail, String alias) {
        this(detail.table,
            detail.domainClass,
            detail.propertyType,
            detail.getter,
            detail.setter,
            detail.propertyName,
            detail.columnName,
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

    @Override
    public Field<T, R, TB> as(String alias) { return new FieldDetail<>(this, alias); }
}
