package com.moon.more.data.registry;


import com.moon.spring.data.BaseAccessor;

import static com.moon.more.data.registry.LayerEnum.*;


/**
 * @author moonsky
 */
public class LayerRegistry {

    public static void registry(LayerEnum layer, Class domainClass, BaseAccessor accessor) {
        if (layer != null && domainClass != null && accessor != null) {
            layer.registry(domainClass, accessor);
        }
    }

    public static BaseAccessor get(LayerEnum layer, Class domainClass) {
        return layer == null ? null : layer.get(domainClass);
    }

    public static void registerRepository(Class domainClass, BaseAccessor accessor) {
        registry(REPOSITORY, domainClass, accessor);
    }

    public static BaseAccessor getRepository(Class domainClass) { return get(REPOSITORY, domainClass); }

    public static void registerService(Class domainClass, BaseAccessor accessor) {
        registry(SERVICE, domainClass, accessor);
    }

    public static BaseAccessor getService(Class domainClass) {
        return get(SERVICE, domainClass);
    }

    public static void registerController(Class domainClass, BaseAccessor accessor) {
        registry(CONTROLLER, domainClass, accessor);
    }

    public static BaseAccessor getController(Class domainClass) { return get(CONTROLLER, domainClass); }

    public static void registerMapper(Class domainClass, BaseAccessor accessor) {
        registry(MAPPER, domainClass, accessor);
    }

    public static BaseAccessor getMapper(Class domainClass) { return get(MAPPER, domainClass); }

    public static void registerSupplier(Class domainClass, BaseAccessor accessor) {
        registry(SUPPLIER, domainClass, accessor);
    }

    public static BaseAccessor getSupplier(Class domainClass) { return get(SUPPLIER, domainClass); }
}
