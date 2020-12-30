package com.moon.processor.model;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.*;

import java.util.*;

import static com.moon.processor.utils.String2.newLine;

/**
 * @author benshaoye
 */
public class DefJavaFiler implements JavaFileWriteable {

    private final Importer importer;
    private final Set<String> interfaces;
    private final Type type;

    private final String pkg, classname, simpleClassname;
    private Set<String> enums = new LinkedHashSet<>();
    private String superclass;

    private final Map<String, DefMethod> methodsDecl = new LinkedHashMap<>();

    private DefJavaFiler(Type type, String pkg, String classname) {
        this.interfaces = new LinkedHashSet<>();
        this.importer = new Importer();
        this.simpleClassname = Element2.getSimpleName(classname);
        this.classname = classname;
        this.type = type;
        this.pkg = pkg;
    }

    public static DefJavaFiler enumOf(String pkg, String classname) {
        return new DefJavaFiler(Type.ENUM, pkg, classname);
    }

    public static DefJavaFiler classOf(String pkg, String classname) {
        return new DefJavaFiler(Type.CLASS, pkg, classname);
    }

    public enum Type {
        /**
         * 枚举
         */
        ENUM,
        /**
         * 类
         */
        CLASS
    }

    public Importer getImporter() { return importer; }

    public Set<String> getInterfaces() { return interfaces; }

    public String getPkg() { return pkg; }

    public String getClassname() { return classname; }

    public String getSimpleClassname() { return simpleClassname; }

    public String getSuperclass() { return superclass; }

    public Type getType() { return type; }

    public Set<String> getEnums() { return enums; }

    public Map<String, DefMethod> getMethodsDecl() { return methodsDecl; }

    public DefJavaFiler enumsOf(String... names) {
        if (enums != null) {
            for (String name : names) {
                this.enums.add(name);
            }
        }
        return this;
    }

    public DefJavaFiler extend(String superclass) {
        if (type == Type.CLASS) {
            this.superclass = onImported(superclass);
        }
        return this;
    }

    public DefJavaFiler implement(String... interfaces) {
        if (interfaces != null) {
            Arrays.stream(interfaces).forEach(it -> this.interfaces.add(onImported(it)));
        }
        return this;
    }

    public DefMethod publicMethod(
        String name, String returnClass, DefParameters parameters
    ) { return publicMethod(false, name, returnClass, parameters); }

    public DefMethod publicMethod(
        boolean isStatic, String name, String returnClass, DefParameters parameters
    ) {
        String methodDecl = "{name}({params})";
        String declare = "public {static}{return} {name}({params})";

        String params = toParametersDecl(parameters);

        methodDecl = Holder.of(Holder.name, Holder.params).on(methodDecl, name, params);

        String staticStr = isStatic ? "static " : "";
        String returned = importer.onImported(returnClass);
        HolderGroup group = Holder.of(Holder.static_, Holder.return_, Holder.name, Holder.params);
        declare = group.on(declare, staticStr, returned, name, params);

        DefMethod method = new DefMethod(declare, getImporter());
        methodsDecl.put(methodDecl, method);
        return method;
    }

    public DefMethod getDefMethod(String name, DefParameters parameters) {
        String methodDecl = "{name}({params})";
        String params = toParametersDecl(parameters);
        String key = Holder.of(Holder.name, Holder.params).on(methodDecl, name, params);
        return getMethodsDecl().get(key);
    }

    private String onImported(String fulled) { return getImporter().onImported(fulled); }

    private String toParametersDecl(Map<String, String> parameters) {
        parameters = parameters == null ? Collections.emptyMap() : parameters;
        List<String> list = new ArrayList<>(parameters.size());
        parameters.forEach((name, type) -> {
            HolderGroup group = Holder.of(Holder.type, Holder.name);
            list.add(group.on("{type} {name}", onImported(type), name));
        });
        return String.join(", ", list);
    }

    private String getClassDeclareScript() {
        final StringBuilder sb = new StringBuilder();
        sb.append("public ").append(getType().name().toLowerCase());
        sb.append(' ').append(getSimpleClassname());
        String superclass = getSuperclass();
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
        String indented = String2.indent(4);
        if (getType() == Type.ENUM) {
            newLine(sb, indented);
            if (Collect2.isNotEmpty(getEnums())) {
                sb.append(String.join(", ", getEnums()));
            }
            sb.append(';');
        } else if (Collect2.isNotEmpty(getEnums())) {
            String template = "private final static {type} {name} = new {type}();";
            template = Holder.type.on(template, getSimpleClassname());
            for (String anEnum : getEnums()) {
                newLine(sb, indented).append(Holder.name.on(template, anEnum));
            }
        }
        getMethodsDecl().forEach((key, method) -> newLine(sb).append(method.toString()));
        return newLine(sb).append("}").toString();
    }

    private String getFullClassname() { return String.format("%s.%s", getPkg(), getClassname()); }

    @Override
    public void writeJavaFile(JavaWriter filer) {
        filer.write(getFullClassname(), writer -> writer.write(toString()));
    }
}
