package com.moon.processor.holder;

import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.annotation.TableEntity;
import com.moon.accessor.annotation.TableEntityPolicy;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefEntityModel;
import com.moon.processor.model.DeclaredPojo;
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
    private final AliasesHolder aliasesHolder;

    private final Map<String, DefEntityModel> modelMap = new HashMap<>();

    public ModelHolder(
        PojoHolder pojoHolder, TablesHolder tablesHolder, AliasesHolder aliasesHolder, PolicyHolder policyHolder
    ) {
        this.aliasesHolder = aliasesHolder;
        this.tablesHolder = tablesHolder;
        this.policyHolder = policyHolder;
        this.pojoHolder = pojoHolder;
    }

    public DefEntityModel with(TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        // 一个实体可以按业务区分成多个访问器
        DefEntityModel entityModel = modelMap.get(classname);
        if (entityModel == null) {
            DeclaredPojo pojo = pojoHolder.with(element);
            TableEntityPolicy policy = policyHolder.with(element);
            TableEntity tableModel = policyHolder.withTableModel(element);
            TableFieldPolicy columnPolicy = policyHolder.withColumnPolicy(element);

            entityModel = new DefEntityModel(pojo, policy, tableModel, columnPolicy);

            tablesHolder.with(element, entityModel);
            aliasesHolder.with(element, entityModel);
        }
        modelMap.put(classname, entityModel);
        return entityModel;
    }

    public DefEntityModel with(TypeElement element, String accessorClassname) {
        // 一个被 @Accessor 注解的访问器只对应一个主表（实体）
        DefEntityModel entityModel;
        if (accessorClassname != null) {
            entityModel = modelMap.get(accessorClassname);
            if (entityModel != null) {
                return entityModel;
            }
        }
        entityModel = with(element);
        if (accessorClassname != null) {
            modelMap.put(accessorClassname, entityModel);
        }
        return entityModel;
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        modelMap.forEach((n, model) -> model.writeJavaFile(writer));
    }
}
