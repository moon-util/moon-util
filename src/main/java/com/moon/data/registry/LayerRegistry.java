package com.moon.data.registry;


import com.moon.core.enums.Level;
import com.moon.core.lang.ref.FinalAccessor;
import com.moon.data.Record;
import com.moon.data.accessor.BaseAccessor;


/**
 * @author moonsky
 */
public class LayerRegistry {

    public static <ID, T extends Record<ID>> void registry(
        LayerEnum layer, Class<T> domainClass, BaseAccessor<T, ID> accessor
    ) {
        if (layer != null) {
            layer.registry(domainClass, accessor);
        }
    }

    public static <ID, T extends Record<ID>> BaseAccessor<T, ID> get(LayerEnum layer, Class<T> domainClass) {
        return layer == null ? null : layer.get(domainClass);
    }

    public static <ID, T extends Record<ID>> BaseAccessor<T, ID> pullTopLevelBy(
        LayerEnum layerEnum, Class<T> domainClass
    ) {
        final Level level = layerEnum.getLevel();
        FinalAccessor<BaseAccessor<T, ID>> accessor = FinalAccessor.of();
        LayerEnum.forEachReversed(layer -> {
            if (layer.getLevel().isBefore(level)) {
                accessor.setIfAbsent(layer.get(domainClass));
            }
            return accessor.isAbsent();
        });
        return accessor.get();
    }
}
