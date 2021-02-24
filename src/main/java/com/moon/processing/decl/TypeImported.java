package com.moon.processing.decl;

import com.moon.accessor.meta.JdbcParameters;
import com.moon.processing.file.BaseImportable;
import com.moon.processing.file.FileClassImpl;
import com.moon.processing.holder.TableHolder;
import com.moon.processing.holder.TypeHolder;
import com.moon.processing.util.Processing2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;

/**
 * @author benshaoye
 */
abstract class TypeImported extends BaseImportable {

    protected final TypeHolder typeHolder;
    protected final TableHolder tableHolder;
    protected final TypeElement accessorElement;
    protected final String accessorClassname;
    protected final AccessorDeclared accessorDeclared;
    protected final TypeDeclared accessorTypeDeclared;
    protected final TableDeclared tableDeclared;
    protected final FileClassImpl impl;
    protected final Map<String, GenericDeclared> thisGenericMap;
    protected final Elements utils;
    protected final Types types;

    private String tableImported;
    private String modelImported;
    private String paramsTypeImported;

    public TypeImported(FileClassImpl impl, AccessorDeclared declared) {
        super(impl.getImporter());
        TypeDeclared accessorTypeDeclared = declared.getTypeDeclared();
        this.thisGenericMap = accessorTypeDeclared.getGenericDeclaredMap();
        this.accessorClassname = declared.getAccessorClassname();
        this.accessorElement = declared.getAccessorElement();
        this.accessorTypeDeclared = accessorTypeDeclared;
        this.tableHolder = declared.getTableHolder();
        this.typeHolder = declared.getTypeHolder();
        this.tableDeclared = declared.getTableDeclared();
        this.impl = impl;
        this.accessorDeclared = declared;
        this.utils = Processing2.getUtils();
        this.types = Processing2.getTypes();
    }

    public final String getTableImported() {
        if (tableImported == null) {
            tableImported = onImported(tableDeclared.getTableClassname());
        }
        return tableImported;
    }

    public final String getModelImported() {
        if (modelImported == null) {
            modelImported = onImported(tableDeclared.getModelClassname());
        }
        return modelImported;
    }

    public final String getParamsTypeImported() {
        if (paramsTypeImported == null) {
            paramsTypeImported = onImported(JdbcParameters.class);
        }
        return paramsTypeImported;
    }
}
