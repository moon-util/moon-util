package com.moon.processor.manager;

import com.moon.accessor.annotation.TableColumnPolicy;
import com.moon.accessor.annotation.TablePolicy;
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

    private final PolicyManager policyManager;
    private final PojoManager pojoManager;
    private final TablesManager tablesManager;

    private final Map<String, DefEntityModel> modelMap = new HashMap<>();

    public ModelManager(PojoManager pojoManager, TablesManager tablesManager, PolicyManager policyManager) {
        this.tablesManager = tablesManager;
        this.policyManager = policyManager;
        this.pojoManager = pojoManager;
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
            TablePolicy policy = policyManager.with(element);
            TableColumnPolicy columnPolicy = policyManager.withColumnPolicy(element);
            entityModel = new DefEntityModel(pojoManager.with(element), policy, columnPolicy);
            modelMap.put(accessorClassname, entityModel);
            tablesManager.with(element, entityModel);
        }
        modelMap.put(classname, entityModel);
        return entityModel;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        modelMap.forEach((n, model) -> model.writeJavaFile(writer));
    }
}
