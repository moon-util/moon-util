package com.moon.processor.manager;

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
public class AccessorManager implements JavaFileWriteable {

    private final CopierManager copierManager;
    private final ModelManager modelManager;
    private final PojoManager pojoManager;
    private final NameManager nameManager;

    private final Map<String, DefAccessor> accessorHashMap = new HashMap<>();

    public AccessorManager(
        CopierManager copierManager, PojoManager pojoManager, ModelManager modelManager, NameManager nameManager
    ) {
        this.copierManager = copierManager;
        this.modelManager = modelManager;
        this.pojoManager = pojoManager;
        this.nameManager = nameManager;
    }

    public void with(TypeElement accessorElem, TypeElement modelElem) {
        String classname = Element2.getQualifiedName(accessorElem);
        modelManager.with(modelElem, classname);
        accessorHashMap.computeIfAbsent(classname, k -> new DefAccessor(accessorElem));
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {

    }
}
