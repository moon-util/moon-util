package com.moon.processor.holder;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefAccessor;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class AccessorHolder implements JavaFileWriteable {

    private final CopierHolder copierHolder;
    private final ModelHolder modelHolder;
    private final PojoHolder pojoHolder;
    private final NameHolder nameHolder;

    private final Map<String, DefAccessor> accessorHashMap = new HashMap<>();

    public AccessorHolder(
        CopierHolder copierHolder, PojoHolder pojoHolder, ModelHolder modelHolder, NameHolder nameHolder
    ) {
        this.copierHolder = copierHolder;
        this.modelHolder = modelHolder;
        this.pojoHolder = pojoHolder;
        this.nameHolder = nameHolder;
    }

    public void with(TypeElement accessorElem, TypeElement modelElem) {
        String classname = Element2.getQualifiedName(accessorElem);
        modelHolder.with(modelElem, classname);
        accessorHashMap.computeIfAbsent(classname, k -> new DefAccessor(accessorElem));
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {

    }
}
