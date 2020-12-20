package com.moon.data.jdbc.processing;

import com.moon.data.jdbc.annotation.Provided;
import org.springframework.stereotype.Repository;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

import static com.moon.data.jdbc.processing.StringUtils.*;
import static com.moon.data.jdbc.processing.Const.*;

/**
 * @author benshaoye
 */
public class AccessorWriter {

    private final Nameable nameable = new Nameable();

    private String pkg;

    private String comment;

    private Modifier access;

    private String classname;

    private String extend;

    private final Importer importer = new Importer();

    private final List<String> interfaces = new ArrayList<>();

    private final Map<String, String> fields = new HashMap<>();

    private final Map<String, String[]> getters = new HashMap<>();

    private final Map<String, String[]> setters = new HashMap<>();

    private final Map<String, ProvidedMethod> providedMap = new HashMap<>();

    private final List<MethodModel> methods = new ArrayList<>();

    public AccessorWriter() { }

    public Modifier getAccess() { return access == null ? Modifier.PUBLIC : access; }

    public AccessorWriter access(Modifier modifier) {
        access = modifier;
        return this;
    }

    public AccessorWriter pkg(String pkg) {
        this.pkg = pkg;
        return this;
    }

    public AccessorWriter classname(String classname) {
        this.classname = classname;
        return this;
    }

    public AccessorWriter extend(String classname) {
        this.extend = onImported(classname);
        return this;
    }

    public AccessorWriter impl(String anInterface) {
        this.interfaces.add(onImported(anInterface));
        return this;
    }

    public AccessorWriter comment(String classname) {
        this.comment = classname;
        return this;
    }

    public String onImported(String classname) { return importer.onImported(classname); }

    public String onImported(Class<?> classname) { return importer.onImported(classname); }

    public String onImported(TypeElement element) { return importer.onImported(element); }

    public AccessorWriter addMemberField(String name, Class<?> fieldType) {
        return addMemberField(name, fieldType.getCanonicalName());
    }

    public AccessorWriter addProvidedMethod(ProvidedMethod model) {
        this.providedMap.put(model.getFieldDeclaration(importer), model);
        return this;
    }

    public AccessorWriter addImplMethod(MethodModel model) {
        this.methods.add(model);
        return this;
    }

    public AccessorWriter addMemberField(String name, String fieldTypeClassname) {
        String type = onImported(fieldTypeClassname);
        this.fields.put(name, StringUtils.toDeclareField(name, type));
        String[] getter = {StringUtils.toGetterMethod(name, type)};
        String[] setter = {StringUtils.toSetterMethod(name, type)};
        this.setters.put(name, setter);
        this.getters.put(name, getter);
        return this;
    }

    public void write(Filer filer) throws IOException {
        JavaFileObject javaFile = filer.createSourceFile(getCanonicalName());
        try (Writer jw = javaFile.openWriter(); PrintWriter writer = new PrintWriter(jw)) {
            writer.write(toString());
        }
    }

    public String getCanonicalName() { return StringUtils.isBlank(pkg) ? classname : String.join(".", pkg, classname); }

    @Override
    public String toString() {
        final StringBuilder sb = newStringBuilder(importer);
        if (StringUtils.isNotBlank(pkg)) {
            sb.append("package ").append(pkg).append(";\n\n");
        }
        nextLine(sb.append("{IMPORTS}"));

        addAnnotatedImpl(sb, comment);
        addClassDeclaration(sb).append(" {");

        int indent = 4;
        addMemberFields(sb, indent, fields);
        addProvidedMethods(sb, indent, importer, providedMap);
        addEmptyConstructor(sb, indent, classname);
        addGettersMethod(sb, indent, getters);
        addSettersMethod(sb, indent, setters);

        addAbstractMethods(sb, indent, methods, importer);

        String declaration = nextLine(sb).append('}').toString();
        return Replacer.IMPORTS.replace(declaration, importer.toString("\n"));
    }

    private static void addAbstractMethods(
        StringBuilder sb, int indent, Collection<MethodModel> models, Importer importer
    ) {
        if (models == null || models.isEmpty()) {
            return;
        } else {
            nextLine(sb);
        }
        for (MethodModel model : models) {
            nextLine(sb);
            sb.append(model.toString(indent, importer));
            nextLine(sb);
        }
    }

    private StringBuilder addClassDeclaration(StringBuilder sb) {
        nextLine(sb);
        sb.append(getAccess().toString()).append(" class ").append(classname);
        if (StringUtils.isNotBlank(extend)) {
            sb.append(" extends ").append(extend);
        }
        if (CollectUtils.isNotEmpty(interfaces) && StringUtils.isNotAnyBlank(interfaces)) {
            sb.append(" implements ").append(String.join(", ", interfaces));
        }
        return sb;
    }

    private static void addGettersMethod(StringBuilder sb, int indent, Map<String, String[]> getters) {
        if (!getters.isEmpty()) {
            addInlineComment(sb, indent, "getters");
            addMethod(sb, indent, getters);
        }
    }

    private static void addSettersMethod(StringBuilder sb, int indent, Map<String, String[]> setters) {
        if (!setters.isEmpty()) {
            addInlineComment(sb, indent, "setters");
            addMethod(sb, indent, setters);
        }
    }

    private static void addMethod(StringBuilder sb, int indent, Map<String, String[]> setters) {
        String space = indent(indent);
        nextLine(sb);
        for (Map.Entry<String, String[]> entry : setters.entrySet()) {
            nextLine(sb);
            for (String scriptPart : entry.getValue()) {
                sb.append(space).append(scriptPart).append('\n');
            }
        }
    }

    private static void addProvidedMethods(
        StringBuilder sb, int indent, Importer importer, Map<String, ProvidedMethod> providedMap
    ) {
        newLine(sb);
        String space = indent(indent);
        for (Map.Entry<String, ProvidedMethod> providedEntry : providedMap.entrySet()) {
            nextLine(sb).append(space).append(providedEntry.getKey());
            newLine(sb).append(providedEntry.getValue().toString(importer, indent));
        }
    }

    private static void addMemberFields(StringBuilder sb, int indent, Map<String, String> fields) {
        if (!fields.isEmpty()) {
            String space = indent(indent);
            nextLine(nextLine(sb));
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                sb.append(space).append(entry.getValue()).append('\n');
            }
        }
    }

    private static void addEmptyConstructor(StringBuilder sb, int indent, String classname, String... scripts) {
        String space = indent(indent);
        nextLine(sb);
        nextLine(sb).append(space).append("public ").append(classname).append("() {");
        if (scripts != null && scripts.length > 0) {
            nextLine(sb);
            for (String script : scripts) {
                sb.append(space).append(space).append(script).append('\n');
            }
            sb.append(space).append('}');
        } else {
            sb.append(" }");
        }
    }

    private void addAnnotatedImpl(StringBuilder sb, String targetClass) {
        String space = indent(4);
        nextLine(sb);
        String generated = onImported(Generated.class);
        sb.append("@").append(generated).append("(");
        nextLine(sb);
        String cls = AccessorProcessor.class.getCanonicalName();
        sb.append(space).append("value = \"").append(cls).append("\",");
        nextLine(sb);
        sb.append(space).append("date = \"").append(new Date()).append("\",");
        nextLine(sb);
        String name = targetClass == null ? "" : targetClass;
        sb.append(space).append("comments = \"").append(name).append("\"");
        nextLine(sb).append(")");
        if (Imported.REPOSITORY) {
            String repository = onImported(Repository.class);
            nextLine(sb).append('@').append(repository);
        }
    }

    public static StringBuilder newStringBuilder(Importer importer) {
        if (Imported.REPOSITORY) {
            importer.onImported(Repository.class);
        }
        importer.onImported(Generated.class);
        return new StringBuilder();
    }
}
