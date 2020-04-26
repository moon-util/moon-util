package com.moon.more.data.registry;



import com.moon.more.data.access.BaseAccessor;

import java.util.HashMap;
import java.util.Map;

import static com.moon.more.data.registry.LayerEnum.*;


/**
 * @author benshaoye
 */
public class LayerRegistry {

    private final static Map<Class, BaseAccessor> repositories = new HashMap<>();
    private final static Map<Class, BaseAccessor> services = new HashMap<>();
    private final static Map<Class, BaseAccessor> controllers = new HashMap<>();
    private final static Map<Class, BaseAccessor> mappers = new HashMap<>();
    private final static Map<Class, BaseAccessor> suppliers = new HashMap<>();

    public static void register(LayerEnum layer, Class domainClass, BaseAccessor accessor) {
        layer.registry(domainClass, accessor);
    }

    public static BaseAccessor get(LayerEnum layer, Class domainClass) {
        return layer.get(domainClass);
    }

    public static void registerRepository(Class domainClass, BaseAccessor accessor) {
        register(REPOSITORY, domainClass, accessor);
    }

    public static BaseAccessor getRepository(Class domainClass) {
        return get(REPOSITORY, domainClass);
    }

    public static void registerService(Class domainClass, BaseAccessor accessor) {
        register(SERVICE, domainClass, accessor);
    }

    public static BaseAccessor getService(Class domainClass) {
        return get(SERVICE, domainClass); }

    public static void registerController(Class domainClass, BaseAccessor accessor) {
        register(CONTROLLER, domainClass, accessor);
    }

    public static BaseAccessor getController(Class domainClass) { return get(CONTROLLER, domainClass); }

    public static void registerMapper(Class domainClass, BaseAccessor accessor) { register(MAPPER, domainClass, accessor); }

    public static BaseAccessor getMapper(Class domainClass) { return get(MAPPER, domainClass); }

    public static void registerSupplier(Class domainClass, BaseAccessor accessor) {
        register(SUPPLIER, domainClass, accessor);
    }

    public static BaseAccessor getSupplier(Class domainClass) { return get(SUPPLIER, domainClass); }
}
