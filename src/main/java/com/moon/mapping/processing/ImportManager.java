package com.moon.mapping.processing;

import java.util.HashMap;
import java.util.Map;

/**
 * import 管理器
 *
 * @author benshaoye
 */
final class ImportManager {

    private final Map<String, String> shortNameCached = new HashMap<>();
    private final Map<String, String> importCached = new HashMap<>();

    public ImportManager() {
        shortNameCached.put("byte[]", "");
        shortNameCached.put("short[]", "");
        shortNameCached.put("int[]", "");
        shortNameCached.put("long[]", "");
        shortNameCached.put("float[]", "");
        shortNameCached.put("double[]", "");
        shortNameCached.put("char[]", "");
        shortNameCached.put("boolean[]", "");
    }

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
        if (classname.endsWith("[]")) {
            classname = classname.substring(0, classname.length() - 2);
        }
        shortName = ElemUtils.getSimpleName(classname);
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
