package com.moon.processor.holder;

import com.moon.core.lang.StringUtil;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author benshaoye
 */
public class Importer implements Importable {

    private final static Map<String, String> GENERICS_MAP = new HashMap<>();

    private final static String EMPTY = "";

    private final Map<String, String> shortNameCached = new HashMap<>();
    private final Map<String, String> importCached = new TreeMap<>();

    public Importer() { }

    @Override
    public String onImported(Class<?> classname) { return onImported(classname.getCanonicalName()); }

    public String onImported(TypeElement element) { return onImported(element.asType()); }

    public String onImported(TypeMirror mirror) { return onImported(mirror.toString()); }

    @Override
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
                case '?':
                case '&':
                    result.append(ch);
                    break;
                case ' ':
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

    private String doImported(String fullName) {
        if (StringUtil.isBlank(fullName)) {
            return EMPTY;
        }
        String classname = fullName.trim();
        String shortName = shortNameCached.get(classname);
        if (shortName != null) {
            return shortName;
        }
        if ("...".equals(classname)) {
            return classname;
        }
        shortName = Element2.getSimpleName(classname);
        if (importCached.containsKey(shortName)) {
            return classname;
        } else if (Test2.isPrimitive(classname) || "void".equals(classname)) {
            shortNameCached.put(classname, classname);
            return classname;
        } else {
            if (classname.indexOf('.') < 0) {
                if (GENERICS_MAP.containsKey(classname)) {
                    return shortName;
                }
                if (Environment2.getUtils().getTypeElement(classname) == null) {
                    GENERICS_MAP.put(classname, shortName);
                    return shortName;
                }
                importCached.put(shortName, "import " + classname + ";");
            } else if (!(classname.startsWith("java.lang.") && classname.split("\\.").length == 3)) {
                importCached.put(shortName, "import " + classname + ";");
            }
            shortNameCached.put(classname, shortName);
            return shortName;
        }
    }

    @Override
    public String toString() { return toString(EMPTY); }

    public String toString(String delimiter) { return String.join(delimiter, new TreeSet<>(importCached.values())); }
}
