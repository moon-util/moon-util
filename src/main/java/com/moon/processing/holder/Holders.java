package com.moon.processing.holder;

import com.moon.processing.JavaFiler;
import com.moon.processing.util.Processing2;
import com.moon.processor.utils.Environment2;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author benshaoye
 */
public enum Holders {
    INSTANCE;

    private final static ThreadLocal<ProcessingEnvironment> ENV = new ThreadLocal<>();
    private final NameHolder nameHelper;
    private final TypeHolder typeHolder;
    private final MatchingHolder matchingHolder;
    private final CopierHolder copierHolder;
    private final MapperHolder mapperHolder;
    private final TableHolder tableHolder;
    private final RecordHolder recordHolder;
    private final TablesHolder tablesHolder;
    private final AliasesHolder aliasesHolder;
    private final AccessorHolder accessorHolder;
    private final DslHelper dslHelper;

    Holders() {
        this.nameHelper = new NameHolder();
        this.dslHelper = new DslHelper();
        this.tablesHolder = new TablesHolder();
        this.aliasesHolder = new AliasesHolder();
        this.matchingHolder = new MatchingHolder();
        this.typeHolder = new TypeHolder(nameHelper);
        this.recordHolder = new RecordHolder(typeHolder);
        this.copierHolder = new CopierHolder(recordHolder);
        this.mapperHolder = new MapperHolder(copierHolder);
        this.tableHolder = new TableHolder(typeHolder, tablesHolder, aliasesHolder);
        this.accessorHolder = new AccessorHolder(nameHelper, typeHolder, tableHolder);
    }

    public void init(ProcessingEnvironment processingEnv) {
        ENV.set(processingEnv);
        Processing2.initialize(processingEnv);
        Environment2.initialize(processingEnv);
    }

    public NameHolder getNameHelper() { return nameHelper; }

    public TypeHolder getTypeHolder() { return typeHolder; }

    public MatchingHolder getMatchingHolder() { return matchingHolder; }

    public CopierHolder getCopierHolder() { return copierHolder; }

    public MapperHolder getMapperHolder() { return mapperHolder; }

    public TableHolder getTableHolder() { return tableHolder; }

    public RecordHolder getRecordHolder() { return recordHolder; }

    public TablesHolder getTablesHolder() { return tablesHolder; }

    public AliasesHolder getAliasesHolder() { return aliasesHolder; }

    public AccessorHolder getAccessorHolder() { return accessorHolder; }

    public DslHelper getDslHelper() { return dslHelper; }

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
