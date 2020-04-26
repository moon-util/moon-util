package com.moon.more.data.registry;



import com.moon.spring.data.RecordAccessor;

import java.util.HashMap;
import java.util.Map;

import static com.moon.more.data.registry.LayerEnum.*;


/**
 * @author benshaoye
 */
public class LayerRegistry {

    private final static Map<Class, RecordAccessor> repositories = new HashMap<>();
    private final static Map<Class, RecordAccessor> services = new HashMap<>();
    private final static Map<Class, RecordAccessor> controllers = new HashMap<>();
    private final static Map<Class, RecordAccessor> mappers = new HashMap<>();
    private final static Map<Class, RecordAccessor> suppliers = new HashMap<>();

    public static void register(LayerEnum layer, Class domainClass, RecordAccessor accessor) {
        layer.registry(domainClass, accessor);
    }

    public static RecordAccessor get(LayerEnum layer, Class domainClass) {
        return layer.get(domainClass);
    }

    public static void registerRepository(Class domainClass, RecordAccessor accessor) {
        register(REPOSITORY, domainClass, accessor);
    }

    public static RecordAccessor getRepository(Class domainClass) {
        return get(REPOSITORY, domainClass);
    }

    public static void registerService(Class domainClass, RecordAccessor accessor) {
        register(SERVICE, domainClass, accessor);
    }

    public static RecordAccessor getService(Class domainClass) {
        return get(SERVICE, domainClass); }

    public static void registerController(Class domainClass, RecordAccessor accessor) {
        register(CONTROLLER, domainClass, accessor);
    }

    public static RecordAccessor getController(Class domainClass) { return get(CONTROLLER, domainClass); }

    public static void registerMapper(Class domainClass, RecordAccessor accessor) { register(MAPPER, domainClass, accessor); }

    public static RecordAccessor getMapper(Class domainClass) { return get(MAPPER, domainClass); }

    public static void registerSupplier(Class domainClass, RecordAccessor accessor) {
        register(SUPPLIER, domainClass, accessor);
    }

    public static RecordAccessor getSupplier(Class domainClass) { return get(SUPPLIER, domainClass); }
}
