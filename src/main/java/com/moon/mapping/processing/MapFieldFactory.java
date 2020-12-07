package com.moon.mapping.processing;

import java.util.concurrent.atomic.AtomicInteger;

import static com.moon.mapping.processing.ElemUtils.getSimpleName;

/**
 * @author benshaoye
 */
final class MapFieldFactory {

    public String doConvertField(final Manager manager) {
        return onDeclareCompleted(doMappingField(manager), manager.getModel());
    }

    private String doMappingField(final Manager manager) {
        // 转换优先级顺序: setter 转换器、getter 转换器、内置默认转换器
        String template = "{toName}.{setterName}({fromName}.{getterName}());";
        String convertMethod = manager.findConverterMethod();
        if (convertMethod != null) {
            return Replacer.setterName.replace(template, convertMethod);
        }
        String provideMethod = manager.findProviderMethod();
        if (provideMethod != null) {
            return Replacer.getterName.replace(template, provideMethod);
        }

        manager.getMapping().withPropertyType(ModeStrategy.FINALLY);
        return MappingField.doMapping(manager);
    }

    private String onDeclareCompleted(String t0, PropertyModel model) {
        if (t0 != null) {
            t0 = Replacer.fromName.replace(t0, model.getFromName());
            t0 = Replacer.toName.replace(t0, model.getToName());
            t0 = Replacer.setterName.replace(t0, model.getSetterName());
            t0 = Replacer.getterName.replace(t0, model.getGetterName());
            t0 = Replacer.var.replace(t0, nextVarName());
            return t0;
        } else {
            return warningAndIgnored(model);
        }
    }

    private String warningAndIgnored(final PropertyModel model) {
        String fromType = getSimpleName(model.getFromClassname());
        String toType = getSimpleName(model.getToClassname());
        @SuppressWarnings("all")
        Object[] values = {
            toType, model.getSetterName(), model.getSetterFinalType(),//
            fromType, model.getGetterName(), model.getGetterFinalType()
        };
        Logger.printWarn(TEMPLATE, values);
        return null;
    }

    private final static String TEMPLATE = "【已忽略】'{}.{}({})' 不兼容 '{}.{}()' 返回值类型: {}";

    private final AtomicInteger indexer = new AtomicInteger();

    private String nextVarName() { return "var" + indexer.getAndIncrement(); }
}
