package com.moon.processor.manager;

import com.moon.processor.model.DeclareClass;
import com.moon.processor.model.DefConverter;
import com.moon.processor.utils.Element2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class MapperManager {

    private final Map<String, DefConverter> registryMap = new HashMap<>();

    public MapperManager() {
    }

    public DefConverter mapperOf(DeclareClass thisClass, DeclareClass thatClass) {
        String mapperKey = toMapperKey(thisClass, thatClass);
        DefConverter registry = registryMap.get(mapperKey);
        if (registry == null) {
            registry = buildMapperWriterDef(thisClass, thatClass);
            registryMap.put(mapperKey, registry);
        }
        return registry;
    }

    private static DefConverter buildMapperWriterDef(DeclareClass thisClass, DeclareClass thatClass) {
        String pkg = Element2.getPackageName(thisClass.getDeclareElement());

        // String classname =
        return null;
    }

    private static String toMapperKey(DeclareClass thisClass, DeclareClass thatClass) {
        return thisClass.getThisClassname() + ":" + thatClass.getThisClassname();
    }
}
