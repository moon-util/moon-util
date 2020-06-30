package com.moon.spring.jpa.naming;

import org.hibernate.boot.model.naming.*;
import org.hibernate.boot.model.source.spi.AttributePath;

/**
 * @author moonsky
 */
public class DefaultImplicitNamingStrategy extends ImplicitNamingStrategyLegacyHbmImpl
    implements ImplicitNamingStrategy {

    /**
     * 表名
     *
     * @param source
     *
     * @return
     */
    @Override
    public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
        EntityNaming naming = source.getEntityNaming();
        String jpaName = naming.getJpaEntityName();
        String className = naming.getClassName();
        if (className.endsWith(jpaName)) {
            String tbName = Namings.toLowerUnderscore(jpaName);
            tbName = "tb" + tbName.replaceAll("_entity", "");
            return toIdentifier(tbName, source.getBuildingContext());
        } else {
            return toIdentifier(jpaName, source.getBuildingContext());
        }
    }

    @Override
    public Identifier determineJoinTableName(ImplicitJoinTableNameSource source) {
        return super.determineJoinTableName(source);
    }

    @Override
    public Identifier determineCollectionTableName(ImplicitCollectionTableNameSource source) {
        return super.determineCollectionTableName(source);
    }

    @Override
    public Identifier determineIdentifierColumnName(ImplicitIdentifierColumnNameSource source) {
        String jpaName = source.getEntityNaming().getJpaEntityName();
        return super.determineIdentifierColumnName(source);
    }

    @Override
    public Identifier determineDiscriminatorColumnName(ImplicitDiscriminatorColumnNameSource source) {
        return super.determineDiscriminatorColumnName(source);
    }

    @Override
    public Identifier determineTenantIdColumnName(ImplicitTenantIdColumnNameSource source) {
        return super.determineTenantIdColumnName(source);
    }

    /**
     * 基本字段名
     *
     * @param source
     *
     * @return
     */
    @Override
    public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
        AttributePath path = source.getAttributePath();
        String columnName = Namings.toLowerUnderscore(path.getFullPath());
        return toIdentifier(columnName, source.getBuildingContext());
    }

    @Override
    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
        return super.determineJoinColumnName(source);
    }

    @Override
    public Identifier determinePrimaryKeyJoinColumnName(ImplicitPrimaryKeyJoinColumnNameSource source) {
        return super.determinePrimaryKeyJoinColumnName(source);
    }

    @Override
    public Identifier determineAnyDiscriminatorColumnName(ImplicitAnyDiscriminatorColumnNameSource source) {
        return super.determineAnyDiscriminatorColumnName(source);
    }

    @Override
    public Identifier determineAnyKeyColumnName(ImplicitAnyKeyColumnNameSource source) {
        return super.determineAnyKeyColumnName(source);
    }

    @Override
    public Identifier determineMapKeyColumnName(ImplicitMapKeyColumnNameSource source) {
        return super.determineMapKeyColumnName(source);
    }

    @Override
    public Identifier determineListIndexColumnName(ImplicitIndexColumnNameSource source) {
        return super.determineListIndexColumnName(source);
    }

    /**
     * 外键
     *
     * @param source
     *
     * @return
     */
    @Override
    public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
        return Namings.toIdentifier("fk", source);
    }

    /**
     * 唯一键
     *
     * @param source
     *
     * @return
     */
    @Override
    public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource source) {
        return Namings.toIdentifier("uk", source);
    }

    /**
     * 索引
     *
     * @param source
     *
     * @return
     */
    @Override
    public Identifier determineIndexName(ImplicitIndexNameSource source) {
        return Namings.toIdentifier("idx", source);
    }
}
