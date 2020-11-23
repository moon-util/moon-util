package com.moon.mapping.processing;

import static com.moon.mapping.processing.ElementUtils.getSimpleName;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;

/**
 * @author benshaoye
 */
final class WarningManager {

    private final static String TEMPLATE = "【已忽略】'{}.{}({})' 不兼容 '{}.{}()' 返回值类型: {}";

    private boolean warned;
    private final MappingModel model;

    public WarningManager(MappingModel model) { this.model = model; }

    public void reset() { this.warned = true; }

    public boolean isWarned() { return warned; }

    public String doWarningIgnored() {
        String fromType = getSimpleName(model.getFromClassname());
        String toType = getSimpleName(model.getToClassname());
        @SuppressWarnings(("all")) Object[] values = {
            toType, model.getSetterName(), model.getSetterFinalType(),//
            fromType, model.getGetterName(), model.getGetterFinalType()
        };
        if (!isWarned()) {
            Logger.onLevel(MANDATORY_WARNING, () -> StringUtils.format(true, TEMPLATE, values));
        }
        this.warned = true;
        return null;
    }
}
