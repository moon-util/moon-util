package com.moon.processing.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author benshaoye
 */
public class JavaAnnotationValue extends BaseImportable {

    private final static String[] EMPTY_STRINGS = {};
    private final String method;
    private final List<String> values;
    private JavaAnnotationValueType type;

    JavaAnnotationValue(
        Importer importer, String method, JavaAnnotationValueType type, String value, String... values
    ) {
        super(importer);
        this.method = method;
        this.values = new ArrayList<>();
        if (type != null) {
            this.type = type;
            this.values.add(value);
            values = values == null ? EMPTY_STRINGS : values;
            this.values.addAll(Arrays.asList(values));
        }
    }

    public boolean isAvailable() { return type != null; }

    @Override
    public String toString() {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(method).append(" = ");
        List<String> values = this.values;
        if (values.size() == 1) {
            builder.append(toValue(type, values.get(0)));
        } else {
            StringJoiner joiner = new StringJoiner(", ", "{", "}");
            for (String value : values) {
                joiner.add(toValue(type, value));
            }
            builder.append(joiner.toString());
        }
        return builder.toString();
    }

    private String toValue(JavaAnnotationValueType type, String value) {
        switch (type) {
            case STRING:
                return with("\"", value, "\"");
            case ENUM:
            case PRIMITIVE:
                return with(value);
            case CLASS:
                return with(onImported(value), ".class");
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
