package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class JavaAnnotation extends BaseImportable implements Appender {

    private final static String[] EMPTY_STRINGS = {};

    /**
     * 注解完整名：java.lang.Override
     */
    private final String annotationName;

    private final Map<String, AnnotationValue> valuesMap = new HashMap<>();

    public JavaAnnotation(Importer importer, String annotationName) {
        super(importer);
        this.annotationName = annotationName;
    }

    public JavaAnnotation classOf(String method, Class<?> classValue, Class<?>... classValues) {
        return classOf(method, classValue.getCanonicalName(), mapClassName(classValues));
    }

    public JavaAnnotation classOf(String method, String classname, String... classnames) {
        putVal(method, ValueType.CLASS, classname, classnames);
        return this;
    }

    public JavaAnnotation stringOf(String method, String value, String... values) {
        putVal(method, ValueType.STRING, value, values);
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
        putVal(method, ValueType.PRIMITIVE, String.valueOf(value), valuesStringify);
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
        putVal(method, ValueType.PRIMITIVE, String.valueOf(value), valuesStringify);
        return this;
    }

    public JavaAnnotation booleanOf(String method, boolean value) {
        putVal(method, ValueType.PRIMITIVE, String.valueOf(value));
        return this;
    }

    public JavaAnnotation trueOf(String method) { return booleanOf(method, true); }

    public JavaAnnotation falseOf(String method) { return booleanOf(method, false); }

    private void putVal(String method, ValueType type, String value, String... values) {
        valuesMap.put(method, new AnnotationValue(getImporter(), type, value, values));
    }

    @Override
    public void appendTo(JavaAddr addr) {
        addr.newAdd('@').add(onImported(annotationName));
        if (valuesMap.isEmpty()) {
            return;
        }
        List<String> values = valuesMap.values().stream().map(Object::toString).collect(Collectors.toList());
        if (values.size() == 1) {
            addr.add("(").add(values.get(0)).add(")");
        } else {
            String valuesJoined = String.join(", ", values);
            if (addr.willOverLength(valuesJoined)) {
                addr.add("(").start();
                int lastIndex = values.size() - 1, index = 0;
                for (String value : values) {
                    addr.newAdd(value);
                    if (index < lastIndex) {
                        addr.add(",");
                    }
                }
                addr.newEnd(")");
            } else {
                addr.add("(").add(values.get(0)).add(")");
            }
        }
    }

    private static class AnnotationValue {

        private final Importer importer;
        private final ValueType type;
        private final List<String> values;

        private AnnotationValue(Importer importer, ValueType type, String value, String... values) {
            this.importer = importer;
            this.type = type;
            this.values = new ArrayList<>();
            this.values.add(value);
            values = values == null ? EMPTY_STRINGS : values;
            for (String val : values) {
                this.values.add(val);
            }
        }

        @Override
        public String toString() {
            List<String> values = this.values;
            if (values.size() == 1) {
                return toValue(type, values.get(0));
            }
            StringJoiner joiner = new StringJoiner(", ", "{", "}");
            for (String value : values) {
                joiner.add(toValue(type, value));
            }
            return joiner.toString();
        }

        private String toValue(ValueType type, String value) {
            switch (type) {
                case STRING:
                    return "\"" + value + '"';
                case ENUM:
                case PRIMITIVE:
                    return value;
                case CLASS:
                    return importer.onImported(value) + ".class";
                default:
                    throw new IllegalStateException();
            }
        }
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

    @SuppressWarnings("all")
    private enum ValueType {
        CLASS,
        ENUM,
        STRING,
        PRIMITIVE
    }
}
