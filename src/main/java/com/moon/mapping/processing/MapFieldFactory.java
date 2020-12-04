package com.moon.mapping.processing;

import java.util.concurrent.atomic.AtomicInteger;

import static com.moon.mapping.processing.ElemUtils.getSimpleName;

/**
 * @author benshaoye
 */
final class MapFieldFactory {

    public String doConvertField(final Manager manager) {
        manager.getMapping().withPropertyType(ModeStrategy.FINALLY);
        String mapped = MappingField.doMapping(manager);
        return onDeclareCompleted(mapped, manager.getModel());
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

    private AtomicInteger getIndexer() { return indexer; }

    private String nextVarName() { return nextVarName(getIndexer()); }

    private static String nextVarName(AtomicInteger indexer) { return "var" + indexer.getAndIncrement(); }
}
