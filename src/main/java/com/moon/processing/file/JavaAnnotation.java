package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import java.util.*;
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

    private final Map<String, AnnotationValue> valuesMap = new LinkedHashMap<>();

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
        valuesMap.put(method, new AnnotationValue(getImporter(), method, type, value, values));
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
        List<String> values = valuesMap.values().stream().map(AnnotationValue::toString).collect(Collectors.toList());
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

    private static class AnnotationValue extends BaseImportable {

        private final ValueType type;
        private final String method;
        private final List<String> values;

        private AnnotationValue(Importer importer, String method, ValueType type, String value, String... values) {
            super(importer);
            this.type = type;
            this.method = method;
            this.values = new ArrayList<>();
            this.values.add(value);
            values = values == null ? EMPTY_STRINGS : values;
            this.values.addAll(Arrays.asList(values));
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
                    return with(method, " = \"", value, "\"");
                case ENUM:
                case PRIMITIVE:
                    return with(method, " = ", value);
                case CLASS:
                    return with(method, " = ", onImported(value), ".class");
                default:
                    throw new IllegalStateException();
            }
        }

        private static String with(String... values) {
            StringBuilder builder = new StringBuilder();
            for (String value : values) {
                builder.append(value);
            }
            return builder.toString();
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
