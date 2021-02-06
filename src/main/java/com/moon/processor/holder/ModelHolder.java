package com.moon.processor.holder;

import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.annotation.TableModel;
import com.moon.accessor.annotation.TableModelPolicy;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.def.DefTableModel;
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

    private final Map<String, DefTableModel> modelMap = new HashMap<>();

    public ModelHolder(
        PojoHolder pojoHolder, TablesHolder tablesHolder, AliasesHolder aliasesHolder, PolicyHolder policyHolder
    ) {
        this.aliasesHolder = aliasesHolder;
        this.tablesHolder = tablesHolder;
        this.policyHolder = policyHolder;
        this.pojoHolder = pojoHolder;
    }

    public DefTableModel with(TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        // 一个实体可以按业务区分成多个访问器
        DefTableModel entityModel = modelMap.get(classname);
        if (entityModel == null) {
            DeclaredPojo pojo = pojoHolder.with(element);
            TableModelPolicy policy = policyHolder.with(element);
            TableModel tableModel = policyHolder.withTableModel(element);
            TableFieldPolicy columnPolicy = policyHolder.withColumnPolicy(element);

            entityModel = new DefTableModel(pojo, policy, columnPolicy, tableModel);

            tablesHolder.with(element, entityModel);
            aliasesHolder.with(element, entityModel);
        }
        modelMap.put(classname, entityModel);
        return entityModel;
    }

    public DefTableModel with(TypeElement element, String accessorClassname) {
        // 一个被 @Accessor 注解的访问器只对应一个主表（实体）
        DefTableModel entityModel;
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

    public DefTableModel get(String modelClassname) {
        return modelMap.get(modelClassname);
    }

    public boolean contains(String modelClassname) {
        return modelMap.containsKey(modelClassname);
    }

    @Override
    public void writeJavaFile(JavaWriter writer) {
        modelMap.forEach((n, model) -> model.writeJavaFile(writer));
    }
}
