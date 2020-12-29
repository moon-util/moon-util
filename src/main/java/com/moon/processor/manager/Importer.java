package com.moon.processor.manager;

import com.moon.core.lang.StringUtil;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class Importer {

    private final static String EMPTY = "";

    private final Map<String, String> shortNameCached = new HashMap<>();
    private final Map<String, String> importCached = new HashMap<>();

    public Importer() {
        shortNameCached.put("byte[]", EMPTY);
        shortNameCached.put("short[]", EMPTY);
        shortNameCached.put("int[]", EMPTY);
        shortNameCached.put("long[]", EMPTY);
        shortNameCached.put("float[]", EMPTY);
        shortNameCached.put("double[]", EMPTY);
        shortNameCached.put("char[]", EMPTY);
        shortNameCached.put("boolean[]", EMPTY);
    }

    public String onImported(Class<?> classname) { return onImported(classname.getCanonicalName()); }

    public String onImported(TypeElement element) { return onImported(element.asType()); }

    public String onImported(TypeMirror mirror) { return onImported(mirror.toString()); }

    public String onImported(String classname) {
        int length = classname.length();
        StringBuilder result = new StringBuilder();
        StringBuilder cache = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char ch = classname.charAt(i);
            switch (ch) {
                case '<':
                case '>':
                case ',':
                case '[':
                case ']':
                    result.append(doImported(cache.toString()));
                    cache.setLength(0);
                    result.append(ch);
                    if (ch == ',') {
                        result.append(' ');
                    }
                    break;
                default:
                    cache.append(ch);
            }
        }

        if (cache.length() > 0) {
            result.append(doImported(cache.toString()));
        }

        return result.toString();
    }

    private String doImported(String classname) {
        if (StringUtil.isBlank(classname)) {
            return EMPTY;
        }
        String shortName = shortNameCached.get(classname);
        if (shortName != null) {
            return shortName;
        }
        if (classname.endsWith("[]")) {
            classname = classname.substring(0, classname.length() - 2);
        }
        shortName = Element2.getSimpleName(classname);
        if (importCached.containsKey(shortName)) {
            return classname;
        } else if (Test2.isPrimitive(classname) || "void".equals(classname)) {
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
    public String toString() { return toString(EMPTY); }

    public String toString(String delimiter) {
        return String.join(delimiter, importCached.values());
    }
}
