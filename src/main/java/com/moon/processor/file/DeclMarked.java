package com.moon.processor.file;

import com.moon.processor.holder.Importable;
import com.moon.processor.utils.Imported;
import com.moon.processor.utils.Log2;
import com.moon.processor.utils.String2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.moon.processor.utils.Imported.*;

/**
 * @author benshaoye
 */
public class DeclMarked implements ImporterAware, ScriptsProvider {

    private final static LocalDateTime NOW = LocalDateTime.now();
    public final static DeclMarked EMPTY = new None();

    private final Importable importable;
    private final String annotationClass;
    private final Map<String, String> values;

    DeclMarked(Importable importable, Class<?> annotationClass) {
        this(importable, annotationClass.getCanonicalName());
    }

    DeclMarked(Importable importable, String annotationClass) {
        this(importable, annotationClass, new LinkedHashMap<>());
    }

    DeclMarked(Importable importable, String annotationClass, Map<String, String> values) {
        this.annotationClass = annotationClass;
        this.importable = importable;
        this.values = values;
    }

    public static DeclMarked ofGenerated(Importable importable) {
        if (Imported.GENERATED) {
            DeclMarked marked = new DeclMarked(importable, Generated.class);
            marked.stringOf("value", DeclJavaFile.class).stringOf("date", NOW);
            return marked;
        }
        return EMPTY;
    }

    public static DeclMarked ofSafeVarargs(Importable importable) {
        return SAFEVARARGS ? new DeclMarked(importable, SafeVarargs.class) : EMPTY;
    }

    public static DeclMarked ofComponent(Importable importable) {
        return COMPONENT ? new DeclMarked(importable, Component.class) : EMPTY;
    }

    public static DeclMarked ofAutowired(Importable importable) {
        return AUTOWIRED ? new DeclMarked(importable, Autowired.class) : EMPTY;
    }

    public static DeclMarked ofBean(Importable importable) {
        return BEAN ? new DeclMarked(importable, Bean.class) : EMPTY;
    }

    public static DeclMarked ofOverride(Importable importable) {
        return new DeclMarked(importable, Override.class);
    }

    @Override
    public Importable getImportable() { return importable; }

    protected DeclMarked add(String method, String formattedValue) {
        values.put(method, formattedValue);
        return this;
    }

    public DeclMarked stringsOf(String method, Object... values) {
        if (values == null || values.length == 0) {
            return add(method, "{}");
        } else {
            String joined = Arrays.stream(values)
                .map(v -> String2.format("\"{}\"", v))
                .collect(Collectors.joining(", "));
            return add(method, String2.format("{{}}", joined));
        }
    }

    public DeclMarked stringOf(String method, Class<?> value) {
        return stringOf(method, value.getCanonicalName());
    }

    public DeclMarked stringOf(String method, Object value) {
        return add(method, String2.format("\"{}\"", value));
    }

    public DeclMarked enumOf(String method, Class<?> enumClass, String enumValue) {
        return enumOf(method, enumClass.getCanonicalName(), enumValue);
    }

    public DeclMarked enumOf(String method, String enumClass, String enumValue) {
        return add(method, String2.format("{}.{}", onImported(enumClass), enumValue));
    }

    public DeclMarked enumsOf(String method, Class<?> enumClass, String... enumValues) {
        return enumsOf(method, enumClass.getCanonicalName(), enumValues);
    }

    public DeclMarked enumsOf(String method, String enumClass, String... enumValues) {
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

    public DeclMarked numberOf(String method, long value) {
        return add(method, String.valueOf(value));
    }

    public DeclMarked numberOf(String method, double value) {
        return add(method, String.valueOf(value));
    }

    public DeclMarked classOf(String method, Class<?> valueClass) {
        return classOf(method, valueClass.getCanonicalName());
    }

    public DeclMarked classOf(String method, String valueClass) {
        return add(method, onImported(valueClass));
    }

    public DeclMarked classesOf(String method, Class<?>... valueClasses) {
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
        String marked = "@" + onImported(annotationClass);
        String declValues = getSimpleValues();
        if (declValues == null) {
            return Arrays.asList(marked);
        }
        if (Formatter2.isOverLength(marked.length() + declValues.length())) {
            LinkedList<String> declares = new LinkedList<>();
            declares.add(marked + '(');
            for (Map.Entry<String, String> entry : values.entrySet()) {
                declares.add("    " + toDeclared(entry) + ',');
            }
            // 去掉最后一个的末尾逗号
            String last = declares.removeLast();
            declares.add(last.substring(0, last.length() - 1));
            declares.add(")");
            return declares;
        } else {
            return Arrays.asList(marked + declValues);
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

    private static class None extends DeclMarked {

        public None() { super(null, null, null); }

        @Override
        protected DeclMarked add(String method, String formattedValue) { return this; }

        @Override
        public List<String> getScripts() { return Collections.emptyList(); }
    }
}
