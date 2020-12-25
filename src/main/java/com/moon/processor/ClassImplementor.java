package com.moon.processor;

import com.moon.processor.model.DefMethod;
import com.moon.processor.utils.Collect2;
import com.moon.processor.utils.String2;

import java.util.*;

import static com.moon.processor.utils.String2.newLine;

/**
 * @author benshaoye
 */
public class ClassImplementor {

    private final Importer importer;
    private final Set<String> interfaces;
    private final Type type;

    private final String pkg, classname;
    private String[] enums;
    private String extend;

    private final Map<String, DefMethod> methodsDecl = new LinkedHashMap<>();

    public ClassImplementor(Type type, String pkg, String classname) {
        this.interfaces = new LinkedHashSet<>();
        this.importer = new Importer();
        this.classname = classname;
        this.type = type;
        this.pkg = pkg;
    }

    public enum Type {
        ENUM,
        CLASS
    }

    public Importer getImporter() { return importer; }

    public Set<String> getInterfaces() { return interfaces; }

    public String getPkg() { return pkg; }

    public String getClassname() { return classname; }

    public String getExtend() { return extend; }

    public Type getType() { return type; }

    public String[] getEnums() { return enums; }

    public Map<String, DefMethod> getMethodsDecl() { return methodsDecl; }

    public ClassImplementor enumsOf(String... names) {
        if (type == Type.ENUM) {
            this.enums = names;
        }
        return this;
    }

    public ClassImplementor extend(String superclass) {
        if (type == Type.CLASS) {
            this.extend = getImporter().onImported(superclass);
        }
        return this;
    }

    public ClassImplementor implement(String... interfaces) {
        if (interfaces != null) {
            Arrays.stream(interfaces).forEach(it -> this.interfaces.add(getImporter().onImported(it)));
        }
        return this;
    }

    public DefMethod publicMethod(
        boolean isStatic, String name, String returnClass, LinkedHashMap<String, String> parameters
    ) {
        String methodDecl = "{name}({params})";
        String declare = "public {static}{return} {name}({params})";

        String params = toParametersDecl(parameters);

        methodDecl = Holder.of(Holder.name, Holder.params).on(methodDecl, name, params);

        String staticStr = isStatic ? "static" : "";
        String returned = importer.onImported(returnClass);
        declare = Holder.of(Holder.STATIC, Holder.RETURN, Holder.name, Holder.params)
            .on(declare, staticStr, returned, name, params);

        DefMethod method = new DefMethod(declare, getImporter());
        methodsDecl.put(methodDecl, method);
        return method;
    }

    public DefMethod getDefMethod(String name, String returnClass, LinkedHashMap<String, String> parameters) {
        String methodDecl = "{name}({params})";
        String params = toParametersDecl(parameters);
        String key = Holder.of(Holder.name, Holder.params).on(methodDecl, name, params);
        return getMethodsDecl().get(key);
    }

    private String toParametersDecl(Map<String, String> parameters) {
        parameters = parameters == null ? Collections.emptyMap() : parameters;
        List<String> list = new ArrayList<>(parameters.size());
        parameters.forEach((name, type) -> {
            Holder.Group group = Holder.of(Holder.type, Holder.name);
            list.add(group.on("{type} {name}", getImporter().onImported(type), name));
        });
        return String.join(", ", list);
    }

    private String getClassDeclareScript() {
        final StringBuilder sb = new StringBuilder();
        sb.append("public ").append(getType().name().toLowerCase(Locale.ROOT));
        sb.append(getClassname());
        String superclass = getExtend();
        if (String2.isNotEmpty(superclass)) {
            sb.append(" extends ").append(superclass).append(' ');
        }
        if (Collect2.isNotEmpty(getInterfaces())) {
            sb.append(" implements ").append(String.join(", ", getInterfaces()));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        newLine(sb.append("package ").append(getPkg()).append(';'));
        newLine(newLine(sb).append(getImporter().toString("\n")));
        newLine(sb).append(getClassDeclareScript()).append(" {");
        if (getType() == Type.ENUM) {
            newLine(sb);
            if (getEnums() == null) {
                sb.append(String.join(", ", getEnums()));
            }
            sb.append(';');
        }
        getMethodsDecl().forEach((key, method) -> newLine(sb).append(method));
        return newLine(sb).append("}").toString();
    }
}
