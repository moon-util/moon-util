package com.moon.processing.file;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class JavaAnnotation extends BaseImportable implements Appender {

    public final static JavaAnnotation NONE = new None();

    private final static String[] EMPTY_STRINGS = {};

    /**
     * 注解完整名：java.lang.Override
     */
    private final String annotationName;

    private final Map<String, JavaAnnotationValue> valuesMap = new LinkedHashMap<>();

    public JavaAnnotation(Importer importer, String annotationName) {
        super(importer);
        this.annotationName = annotationName;
    }

    public JavaAnnotation classOf(String method, Class<?> classValue, Class<?>... classValues) {
        return classOf(method, classValue.getCanonicalName(), mapClassName(classValues));
    }

    public JavaAnnotation classOf(String method, String classname, String... classnames) {
        putVal(method, JavaAnnotationValueType.CLASS, classname, classnames);
        return this;
    }

    public JavaAnnotation stringOf(String method, String value, String... values) {
        putVal(method, JavaAnnotationValueType.STRING, value, values);
        return this;
    }

    public JavaAnnotation longOf(String method, long value, long... values) {
        String[] valuesStringify;
        if (values == null) {
            valuesStringify = EMPTY_STRINGS;
        } else {
            valuesStringify = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                valuesStringify[i] = String.valueOf(values[i]);
            }
        }
        putVal(method, JavaAnnotationValueType.PRIMITIVE, String.valueOf(value), valuesStringify);
        return this;
    }

    public JavaAnnotation doubleOf(String method, double value, double... values) {
        String[] valuesStringify;
        if (values == null) {
            valuesStringify = EMPTY_STRINGS;
        } else {
            valuesStringify = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                valuesStringify[i] = String.valueOf(values[i]);
            }
        }
        putVal(method, JavaAnnotationValueType.PRIMITIVE, String.valueOf(value), valuesStringify);
        return this;
    }

    public JavaAnnotation booleanOf(String method, boolean value) {
        putVal(method, JavaAnnotationValueType.PRIMITIVE, String.valueOf(value));
        return this;
    }

    public JavaAnnotation trueOf(String method) { return booleanOf(method, true); }

    public JavaAnnotation falseOf(String method) { return booleanOf(method, false); }

    public JavaAnnotationValue with(String method) {
        JavaAnnotationValue value = valuesMap.get(method);
        return value == null ? putVal(method, null, null) : value;
    }

    private JavaAnnotationValue putVal(String method, JavaAnnotationValueType type, String value, String... values) {
        JavaAnnotationValue val = new JavaAnnotationValue(getImporter(), method, type, value, values);
        valuesMap.put(method, val);
        return val;
    }

    /**
     * 格式说明: 每个注解一定单起一行
     *
     * @param addr
     */
    @Override
    public void appendTo(JavaAddr addr) {
        addr.newAdd('@').add(onImported(annotationName));
        if (valuesMap.isEmpty()) {
            return;
        }
        @SuppressWarnings("all")
        List<String> values = valuesMap.values().stream()//
            .filter(JavaAnnotationValue::isAvailable)//
            .map(Object::toString)//
            .collect(Collectors.toList());
        if (values.size() == 1) {
            addr.add("(").add(values.get(0)).add(")");
        } else {
            String valuesJoined = String.join(", ", values);
            if (addr.willOverLength(valuesJoined)) {
                addr.add("(").start();
                int lastIndex = values.size() - 1, index = 0;
                for (String value : values) {
                    addr.newAdd(value);
                    if ((index++) < lastIndex) {
                        addr.add(",");
                    }
                }
                addr.newEnd(")");
            } else {
                addr.add("(").add(valuesJoined).add(")");
            }
        }
    }

    private final static class None extends JavaAnnotation {

        public None() { super(new Importer(), null); }

        @Override
        public void appendTo(JavaAddr addr) { }
    }

    private static String[] mapClassName(Class<?>... classes) {
        if (classes == null) {
            return new String[0];
        }
        String[] names = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            names[i] = classes[i].getCanonicalName();
        }
        return names;
    }
}
