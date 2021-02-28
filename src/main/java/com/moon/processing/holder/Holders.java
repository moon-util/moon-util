package com.moon.processing.holder;

import com.moon.processing.JavaFiler;
import com.moon.processing.util.Processing2;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author benshaoye
 */
public enum Holders {
    /** holder default instance */
    INSTANCE;

    private final static ThreadLocal<ProcessingEnvironment> ENV = new ThreadLocal<>();
    private final DslHelper dslHelper;
    private final NameHolder nameHelper;
    private final TypeHolder typeHolder;
    private final TableHolder tableHolder;
    private final CopierHolder copierHolder;
    private final MapperHolder mapperHolder;
    private final RecordHolder recordHolder;
    private final TablesHolder tablesHolder;
    private final PolicyHelper policyHelper;
    private final AliasesHolder aliasesHolder;
    private final AccessorHolder accessorHolder;
    private final MatchingHolder matchingHolder;

    Holders() {
        this.dslHelper = new DslHelper(this);
        this.nameHelper = new NameHolder(this);
        this.typeHolder = new TypeHolder(this);
        this.tableHolder = new TableHolder(this);
        this.policyHelper = new PolicyHelper(this);
        this.tablesHolder = new TablesHolder(this);
        this.recordHolder = new RecordHolder(this);
        this.copierHolder = new CopierHolder(this);
        this.mapperHolder = new MapperHolder(this);
        this.aliasesHolder = new AliasesHolder(this);
        this.matchingHolder = new MatchingHolder(this);
        this.accessorHolder = new AccessorHolder(this);
    }

    public final void init(ProcessingEnvironment processingEnv) {
        ENV.set(processingEnv);
        Processing2.initialize(processingEnv);
    }

    public final ProcessingEnvironment getEnvironment() { return ENV.get(); }

    public final Elements getUtils() {return getEnvironment().getElementUtils();}

    public final Types getTypes() {return getEnvironment().getTypeUtils();}

    public final DslHelper getDslHelper() { return dslHelper; }

    public final NameHolder getNameHelper() { return nameHelper; }

    public final TypeHolder getTypeHolder() { return typeHolder; }

    public final TableHolder getTableHolder() { return tableHolder; }

    public final CopierHolder getCopierHolder() { return copierHolder; }

    public final MapperHolder getMapperHolder() { return mapperHolder; }

    public final RecordHolder getRecordHolder() { return recordHolder; }

    public final TablesHolder getTablesHolder() { return tablesHolder; }

    public final PolicyHelper getPolicyHelper() { return policyHelper; }

    public final AliasesHolder getAliasesHolder() { return aliasesHolder; }

    public final AccessorHolder getAccessorHolder() { return accessorHolder; }

    public final MatchingHolder getMatchingHolder() { return matchingHolder; }

    public void writeJavaFile(JavaFiler filer) {
        recordHolder.write(filer);
        copierHolder.write(filer);
        mapperHolder.write(filer);
        tableHolder.write(filer);
        tablesHolder.write(filer);
        aliasesHolder.write(filer);
        accessorHolder.write(filer);
        dslHelper.write(filer);
    }
}
