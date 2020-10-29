package com.moon.data.registry;


import com.moon.core.enums.Level;
import com.moon.core.lang.ref.FinalAccessor;
import com.moon.data.accessor.BaseAccessor;


/**
 * @author moonsky
 */
public class LayerRegistry {

    public static void registry(LayerEnum layer, Class domainClass, BaseAccessor accessor) {
        if (layer != null) {
            layer.registry(domainClass, accessor);
        }
    }

    public static BaseAccessor get(LayerEnum layer, Class domainClass) {
        return layer == null ? null : layer.get(domainClass);
    }

    public static BaseAccessor pullTopLevelBy(LayerEnum layerEnum, Class domainClass) {
        final Level level = layerEnum.getLevel();
        FinalAccessor<BaseAccessor> accessor = FinalAccessor.of();
        LayerEnum.forEachReversed(layer -> {
            if (layer.getLevel().isBefore(level)) {
                accessor.setIfAbsent(layer.get(domainClass));
            }
            return accessor.isAbsent();
        });
        return accessor.get();
    }
}
