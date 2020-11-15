package com.moon.mapping.processing;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
final class ImportManager {

    private final Map<String, String> shortNameCached = new HashMap<>();
    private final Map<String, String> importCached = new HashMap<>();

    public ImportManager() {}

    public String onImported(Class<?> classname) {
        return onImported(classname.getCanonicalName());
    }

    public String onImported(String classname) {
        if (StringUtils.isBlank(classname)) {
            return "";
        }
        String shortName = shortNameCached.get(classname);
        if (shortName != null) {
            return shortName;
        }
        shortName = ElementUtils.getSimpleName(classname);
        if (importCached.containsKey(shortName)) {
            return classname;
        } else if (StringUtils.isPrimitive(classname)) {
            shortNameCached.put(classname, classname);
            return classname;
        } else {
            if (!(classname.startsWith("java.lang.") && classname.split("\\.").length == 3)) {
                importCached.put(shortName, "import " + classname + ";");
            }
            shortNameCached.put(classname, shortName);
            return shortName;
        }
    }

    @Override
    public String toString() { return String.join("", importCached.values()); }
}
