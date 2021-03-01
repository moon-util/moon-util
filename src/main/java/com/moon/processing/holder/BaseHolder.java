package com.moon.processing.holder;

import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author benshaoye
 */
public abstract class BaseHolder {

    protected final Holders holders;

    protected BaseHolder(Holders holders) { this.holders = holders; }

    public final Holders getHolders() { return holders; }

    public final Types types() { return getHolders().getTypes(); }

    public final Elements utils() { return getHolders().getUtils(); }

    public final String getQualifiedName(QualifiedNameable nameable) {
        return nameable.getQualifiedName().toString();
    }

    public final DslHelper dslHelper() { return holders.getDslHelper(); }

    public final NameHolder nameHelper() { return holders.getNameHelper(); }

    public final TypeHolder typeHolder() { return holders.getTypeHolder(); }

    public final TableHolder tableHolder() { return holders.getTableHolder(); }

    public final PolicyHelper policyHelper() { return holders.getPolicyHelper(); }

    public final CopierHolder copierHolder() { return holders.getCopierHolder(); }

    public final MapperHolder mapperHolder() { return holders.getMapperHolder(); }

    public final RecordHolder recordHolder() { return holders.getRecordHolder(); }

    public final TablesHolder tablesHolder() { return holders.getTablesHolder(); }

    public final AliasesHolder aliasesHolder() { return holders.getAliasesHolder(); }

    public final MatchingHolder matchingHolder() { return holders.getMatchingHolder(); }

    public final AccessorHolder accessorHolder() { return holders.getAccessorHolder(); }
}
