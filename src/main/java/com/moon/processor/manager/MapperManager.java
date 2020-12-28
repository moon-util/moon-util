package com.moon.processor.manager;

import com.moon.processor.model.DeclareClass;
import com.moon.processor.model.DefMapper;
import com.moon.processor.utils.Element2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class MapperManager {

    private final Map<String, DefMapper> registryMap = new HashMap<>();

    public MapperManager() {
    }

    public DefMapper mapperOf(DeclareClass thisClass, DeclareClass thatClass) {
        String mapperKey = toMapperKey(thisClass, thatClass);
        DefMapper registry = registryMap.get(mapperKey);
        if (registry == null) {
            registry = buildMapperWriterDef(thisClass, thatClass);
            registryMap.put(mapperKey, registry);
        }
        return registry;
    }

    private static DefMapper buildMapperWriterDef(DeclareClass thisClass, DeclareClass thatClass) {
        String pkg = Element2.getPackageName(thisClass.getDeclareElement());

        // String classname =
        return null;
    }

    private static String toMapperKey(DeclareClass thisClass, DeclareClass thatClass) {
        return thisClass.getThisClassname() + ":" + thatClass.getThisClassname();
    }
}
