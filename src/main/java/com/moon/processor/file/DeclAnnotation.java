package com.moon.processor.file;

import com.moon.processor.manager.Importable;
import com.moon.processor.utils.Imported;
import com.moon.processor.utils.String2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class DeclAnnotation implements ImporterAware, ScriptsProvider {

    private final static LocalDateTime NOW = LocalDateTime.now();
    public final static DeclAnnotation EMPTY = new None();

    private final Importable importable;
    private final String annotationClass;
    private final Map<String, String> values;

    public DeclAnnotation(Importable importable, Class<?> annotationClass) {
        this(importable, annotationClass.getCanonicalName());
    }

    public DeclAnnotation(Importable importable, String annotationClass) {
        this(importable, annotationClass, new LinkedHashMap<>());
    }

    public DeclAnnotation(Importable importable, String annotationClass, Map<String, String> values) {
        this.annotationClass = annotationClass;
        this.importable = importable;
        this.values = values;
    }

    public static DeclAnnotation ofGenerated(Importable importable) {
        if (Imported.GENERATED) {
            return new DeclAnnotation(importable, Generated.class).stringOf("value",
                DeclJavaFile.class.getCanonicalName()).stringOf("date", NOW);
        }
        return EMPTY;
    }

    public static DeclAnnotation ofComponent(Importable importable) {
        if (Imported.COMPONENT) {
            return new DeclAnnotation(importable, Component.class);
        }
        return EMPTY;
    }

    public static DeclAnnotation ofAutowired(Importable importable) {
        if (Imported.AUTOWIRED) {
            return new DeclAnnotation(importable, Autowired.class);
        }
        return EMPTY;
    }

    public static DeclAnnotation ofBean(Importable importable) {
        if (Imported.BEAN) {
            return new DeclAnnotation(importable, Bean.class);
        }
        return EMPTY;
    }

    public static DeclAnnotation ofOverride(Importable importable) {
        return new DeclAnnotation(importable, Override.class);
    }

    @Override
    public Importable getImportable() { return importable; }

    protected DeclAnnotation add(String method, String formattedValue) {
        values.put(method, formattedValue);
        return this;
    }

    public DeclAnnotation stringsOf(String method, Object... values) {
        if (values == null || values.length == 0) {
            return add(method, "{}");
        } else {
            String joined = Arrays.stream(values)
                .map(v -> String2.format("\"{}\"", v))
                .collect(Collectors.joining(", "));
            return add(method, String2.format("{{}}", joined));
        }
    }

    public DeclAnnotation stringOf(String method, Object value) {
        return add(method, String2.format("\"{}\"", value));
    }

    public DeclAnnotation enumOf(String method, Class<?> enumClass, String enumValue) {
        return enumOf(method, enumClass.getCanonicalName(), enumValue);
    }

    public DeclAnnotation enumOf(String method, String enumClass, String enumValue) {
        return add(method, String2.format("{}.{}", onImported(enumClass), enumValue));
    }

    public DeclAnnotation enumsOf(String method, Class<?> enumClass, String... enumValues) {
        return enumsOf(method, enumClass.getCanonicalName(), enumValues);
    }

    public DeclAnnotation enumsOf(String method, String enumClass, String... enumValues) {
        if (enumValues == null || enumValues.length == 0) {
            return add(method, "{}");
        } else {
            String imported = onImported(enumClass);
            String joined = Arrays.stream(enumValues)
                .map(v -> String2.format("{}.{}", imported, v))
                .collect(Collectors.joining(", "));
            return add(method, String2.format("{{}}", joined));
        }
    }

    public DeclAnnotation numberOf(String method, long value) {
        return add(method, String.valueOf(value));
    }

    public DeclAnnotation numberOf(String method, double value) {
        return add(method, String.valueOf(value));
    }

    public DeclAnnotation classOf(String method, Class<?> valueClass) {
        return classOf(method, valueClass.getCanonicalName());
    }

    public DeclAnnotation classOf(String method, String valueClass) {
        return add(method, onImported(valueClass));
    }

    public DeclAnnotation classesOf(String method, Class<?>... valueClasses) {
        if (valueClasses == null || valueClasses.length == 0) {
            return add(method, "{}");
        } else {
            String joined = Arrays.stream(valueClasses)
                .map(cls -> onImported(cls) + ".class")
                .collect(Collectors.joining(", "));
            return add(method, String2.format("{{}}", joined));
        }
    }

    @Override
    public List<String> getScripts() {
        String mark = String2.format("@{}", onImported(annotationClass));
        String declValues = getSimpleValues();
        if (declValues == null) {
            return Arrays.asList(mark);
        }
        if (Formatter2.isOverLength(mark.length() + declValues.length())) {
            LinkedList<String> declares = new LinkedList<>();
            declares.add(mark + '(');
            for (Map.Entry<String, String> entry : values.entrySet()) {
                declares.add("    " + toDeclared(entry) + ',');
            }
            // 去掉最后一个的末尾逗号
            String last = declares.removeLast();
            declares.add(last.substring(0, last.length() - 1));
            declares.add(")");
            return declares;
        } else {
            return Arrays.asList(mark + declValues);
        }
    }

    private String getSimpleValues() {
        if (values.isEmpty()) {
            return null;
        }
        int index = 0;
        String[] declares = new String[values.size()];
        for (Map.Entry<String, String> entry : values.entrySet()) {
            declares[index++] = toDeclared(entry);
        }
        return String2.format("({})", String.join(", ", declares));
    }

    private String toDeclared(Map.Entry<String, String> entry) {
        return String2.format("{} = {}", entry.getKey(), entry.getValue());
    }

    private static class None extends DeclAnnotation {

        public None() { super(null, null, null); }

        @Override
        protected DeclAnnotation add(String method, String formattedValue) { return this; }

        @Override
        public List<String> getScripts() { return Collections.emptyList(); }
    }
}
