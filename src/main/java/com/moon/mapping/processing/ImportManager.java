package com.moon.mapping.processing;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
final class ImportManager {

    private final Map<String, String> importCached = new HashMap<>();
    private final Map<String, String> shortNameCached = new HashMap<>();

    public ImportManager() {}

    public String onImported(Class<?> classname) {
        return onImported(classname.getCanonicalName());
    }

    public String onImported(String classname) {
        String imported = importCached.get(classname);
        if (imported != null) {
            return imported;
        }
        String shortName = ElementUtils.getSimpleName(classname);
        if (shortNameCached.containsKey(shortName)) {
            return classname;
        } else if (StringUtils.isPrimitive(classname)) {
            importCached.put(classname, classname);
            return classname;
        } else {
            String script = "import " + classname + ";";
            if (classname.startsWith("java.lang.") && classname.split("\\.").length == 3) {
                script = "";
            }
            shortNameCached.put(shortName, script);
            importCached.put(classname, shortName);
            return shortName;
        }
    }

    @Override
    public String toString() {
        return String.join("", shortNameCached.values());
    }
}
