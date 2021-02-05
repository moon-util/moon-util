package com.moon.processor.def;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.DeclJavaFile;
import com.moon.processor.holder.CopierHolder;
import com.moon.processor.holder.ModelHolder;
import com.moon.processor.holder.NameHolder;
import com.moon.processor.holder.PojoHolder;
import com.moon.processor.parser.AccessorParser;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
public class DefAccessor implements JavaFileWriteable {


    private final CopierHolder copierHolder;
    private final ModelHolder modelHolder;
    private final PojoHolder pojoHolder;
    private final NameHolder nameHolder;

    private final TypeElement thisAccessor;
    private final DefTableModel tableModel;
    private final String packageName;
    private final String simpleName;

    public DefAccessor(
        CopierHolder copierHolder,
        ModelHolder modelHolder,
        PojoHolder pojoHolder,
        NameHolder nameHolder,
        TypeElement thisAccessor,
        DefTableModel model
    ) {
        this.copierHolder = copierHolder;
        this.modelHolder = modelHolder;
        this.pojoHolder = pojoHolder;
        this.nameHolder = nameHolder;
        this.thisAccessor = thisAccessor;
        this.tableModel = model;
        this.packageName = Element2.getPackageName(thisAccessor);
        String accessorImpl = nameHolder.getImplClassname(thisAccessor);
        this.simpleName = Element2.getSimpleName(accessorImpl);
    }

    public CopierHolder getCopierHolder() { return copierHolder; }

    public ModelHolder getModelHolder() { return modelHolder; }

    public PojoHolder getPojoHolder() { return pojoHolder; }

    public NameHolder getNameHolder() { return nameHolder; }

    public TypeElement getThisAccessor() { return thisAccessor; }

    public String getPackageName() { return packageName; }

    public String getSimpleName() { return simpleName; }

    public DefTableModel getTableModel() { return tableModel; }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        DeclJavaFile file = AccessorParser.toAccessorJavaFile(this);
        if (file != null) {
            file.writeJavaFile(writer);
        }
    }
}
