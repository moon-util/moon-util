package com.moon.more.data.registry;



import com.moon.spring.data.DataAccessor;

import java.util.HashMap;
import java.util.Map;

import static com.moon.more.data.registry.LayerEnum.*;


/**
 * @author benshaoye
 */
public class LayerRegistry {

    private final static Map<Class, DataAccessor> repositories = new HashMap<>();
    private final static Map<Class, DataAccessor> services = new HashMap<>();
    private final static Map<Class, DataAccessor> controllers = new HashMap<>();
    private final static Map<Class, DataAccessor> mappers = new HashMap<>();
    private final static Map<Class, DataAccessor> suppliers = new HashMap<>();

    public static void register(LayerEnum layer, Class domainClass, DataAccessor accessor) {
        layer.registry(domainClass, accessor);
    }

    public static DataAccessor get(LayerEnum layer, Class domainClass) {
        return layer.get(domainClass);
    }

    public static void registerRepository(Class domainClass, DataAccessor accessor) {
        register(REPOSITORY, domainClass, accessor);
    }

    public static DataAccessor getRepository(Class domainClass) {
        return get(REPOSITORY, domainClass);
    }

    public static void registerService(Class domainClass, DataAccessor accessor) {
        register(SERVICE, domainClass, accessor);
    }

    public static DataAccessor getService(Class domainClass) {
        return get(SERVICE, domainClass); }

    public static void registerController(Class domainClass, DataAccessor accessor) {
        register(CONTROLLER, domainClass, accessor);
    }

    public static DataAccessor getController(Class domainClass) { return get(CONTROLLER, domainClass); }

    public static void registerMapper(Class domainClass, DataAccessor accessor) { register(MAPPER, domainClass, accessor); }

    public static DataAccessor getMapper(Class domainClass) { return get(MAPPER, domainClass); }

    public static void registerSupplier(Class domainClass, DataAccessor accessor) {
        register(SUPPLIER, domainClass, accessor);
    }

    public static DataAccessor getSupplier(Class domainClass) { return get(SUPPLIER, domainClass); }
}
