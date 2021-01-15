package com.moon.processor.holder;

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
public class ModelHolder implements JavaFileWriteable {

    private final PolicyHolder policyHolder;
    private final PojoHolder pojoHolder;
    private final TablesHolder tablesHolder;

    private final Map<String, DefEntityModel> modelMap = new HashMap<>();

    public ModelHolder(PojoHolder pojoHolder, TablesHolder tablesHolder, PolicyHolder policyHolder) {
        this.tablesHolder = tablesHolder;
        this.policyHolder = policyHolder;
        this.pojoHolder = pojoHolder;
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
            TablePolicy policy = policyHolder.with(element);
            TableColumnPolicy columnPolicy = policyHolder.withColumnPolicy(element);
            entityModel = new DefEntityModel(pojoHolder.with(element), policy, columnPolicy);
            modelMap.put(accessorClassname, entityModel);
            tablesHolder.with(element, entityModel);
        }
        modelMap.put(classname, entityModel);
        return entityModel;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        modelMap.forEach((n, model) -> model.writeJavaFile(writer));
    }
}
