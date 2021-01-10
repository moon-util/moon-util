package com.moon.processor.manager;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefEntityModel;
import com.moon.processor.utils.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class ModelManager implements JavaFileWriteable {

    private final NameManager nameManager;
    private final PojoManager pojoManager;

    private final Map<String, DefEntityModel> modelMap = new HashMap<>();

    public ModelManager(PojoManager pojoManager, NameManager manager) {
        this.pojoManager = pojoManager;
        this.nameManager = manager;
    }

    public DefEntityModel with(TypeElement element, String accessorClassname) {
        // 一个被 @Accessor 注解的访问器只对应一个主表（实体）
        DefEntityModel entityModel = modelMap.get(accessorClassname);
        if (entityModel != null) {
            return entityModel;
        }
        String classname = Element2.getQualifiedName(element);
        // 一个实体可以按业务区分成多个访问器
        entityModel = modelMap.get(classname);
        if (entityModel == null) {
            entityModel = new DefEntityModel(pojoManager.with(element));
            modelMap.put(accessorClassname, entityModel);
        }
        modelMap.put(classname, entityModel);
        return entityModel;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        modelMap.forEach((n, model) -> model.writeJavaFile(writer));
    }
}
