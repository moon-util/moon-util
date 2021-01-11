package com.moon.accessor.meta;

/**
 * @author benshaoye
 */
public class FieldDetail<T, R, TB extends Table<R>> implements Field<T, R, TB> {

    private final Class<R> domainClass;
    private final Class<T> propertyType;
    private final String propertyName;
    private final String columnName;
    private final TB table;

    public FieldDetail(TB table, Class<R> domainClass, Class<T> propertyType, String propertyName, String columnName) {
        this.domainClass = domainClass;
        this.propertyType = propertyType;
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.table = table;
    }

    @Override
    public Class<R> getDomainClass() { return domainClass; }

    @Override
    public Class<T> getPropertyType() { return propertyType; }

    @Override
    public String getPropertyName() { return propertyName; }

    @Override
    public String getColumnName() { return columnName; }

    @Override
    public TB getTable() { return table; }
}
