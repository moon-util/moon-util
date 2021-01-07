package com.moon.processor.model;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.manager.ConstManager;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.*;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.*;

import static com.moon.processor.utils.String2.newLine;

/**
 * @author benshaoye
 */
public class DefJavaFiler implements JavaFileWriteable {

    private final Type type;
    private final Importer importer;
    private final Set<String> interfaces;
    private final ConstManager constManager;

    private String superclass;
    private final String pkg, classname, simpleClassname;
    private final Set<String> enums = new LinkedHashSet<>();

    private final Map<String, String> fieldsMap = new LinkedHashMap<>();
    private final Map<String, String> constantsMap = new LinkedHashMap<>();
    private final Map<String, DefMethod> methodsDecl = new LinkedHashMap<>();

    private boolean component = true;

    private DefJavaFiler(Type type, String pkg, String classname) {
        this.interfaces = new LinkedHashSet<>();
        this.importer = new Importer();
        this.constManager = new ConstManager(importer);
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

    public boolean isComponent() { return component; }

    public void setComponent(boolean component) { this.component = component; }

    public Importer getImporter() { return importer; }

    public String onImported(String classname) { return getImporter().onImported(classname); }

    public String onImported(Class<?> classname) { return getImporter().onImported(classname); }

    public String getPkg() { return pkg; }

    public Type getType() { return type; }

    public DefJavaFiler component() {
        return component(true);
    }

    public DefJavaFiler component(boolean component) {
        setComponent(component);
        return this;
    }

    public Set<String> getEnums() { return enums; }

    public String getClassname() { return classname; }

    public String getSuperclass() { return superclass; }

    public Set<String> getInterfaces() { return interfaces; }

    public String getSimpleClassname() { return simpleClassname; }

    public Map<String, String> getConstantsMap() { return constantsMap; }

    public Map<String, DefMethod> getMethodsDecl() { return methodsDecl; }

    public DefJavaFiler enumsOf(String... names) {
        if (names != null) {
            this.enums.addAll(Arrays.asList(names));
        }
        return this;
    }

    private final static HolderGroup GROUP = Holder.of(Holder.type, Holder.var, Holder.value);
    private final static String ENUM_REF = "private final static {type} {var} = {type}.{value};";

    public DefJavaFiler enumRef(String type, String var, String enumValue) {
        if (String2.isNotBlank(type) && String2.isNotBlank(var) && String2.isNotBlank(enumValue)) {
            constantsMap.put(var, GROUP.on(ENUM_REF, onImported(type), var, enumValue));
        }
        return this;
    }

    public DefJavaFiler constOf(String type, String name, String value) {
        if (String2.isNotBlank(type) && String2.isNotBlank(name) && String2.isNotBlank(value)) {
            constantsMap.put(name, String2.toConstField(onImported(type), name, value));
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

    public DefJavaFiler privateField(String name, String type) {
        if (String2.isNotBlank(type) && String2.isNotBlank(name)) {
            fieldsMap.put(name, String2.toDeclareField(name, onImported(type)));
        }
        return this;
    }

    private final static DefParameters PARAMS = DefParameters.of();

    public DefMethod publicGetterMethod(String fieldName, String type) {
        String getterName = String2.toGetterName(fieldName, type);
        return publicGetterMethod(getterName, fieldName, type);
    }

    public DefMethod publicSetterMethod(String fieldName, String type) {
        String setterName = Const2.SET + String2.capitalize(fieldName);
        return publicSetterMethod(setterName, fieldName, type);
    }

    public DefMethod publicGetterMethod(String getterName, String fieldName, String type) {
        return publicMethod(getterName, type, PARAMS).returning(fieldName);
    }

    public DefMethod publicSetterMethod(String setterName, String fieldName, String type) {
        DefParameters params = DefParameters.of(fieldName, type);
        return publicMethod(setterName, "void", params).scriptOfAssign(fieldName);
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

        DefMethod method = new DefMethod(declare, getImporter(), constManager);
        methodsDecl.put(methodDecl, method);
        return method;
    }

    public DefMethod getDefMethod(String name, DefParameters parameters) {
        String methodDecl = "{name}({params})";
        String params = toParametersDecl(parameters);
        String key = Holder.of(Holder.name, Holder.params).on(methodDecl, name, params);
        return getMethodsDecl().get(key);
    }

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
        String indented = String2.indent(4);
        final StringBuilder sb = new StringBuilder();
        newLine(sb.append("package ").append(getPkg()).append(';'));
        final int importIndex = sb.length();
        autoGenerated(newLine(sb), indented);
        newLine(sb).append(getClassDeclareScript()).append(" {");
        // enums
        newLine(sb);
        if (getType() == Type.ENUM) {
            newLine(sb, indented);
            if (Collect2.isNotEmpty(getEnums())) {
                sb.append(String.join(", ", getEnums()));
            }
            sb.append(';');
        } else if (Collect2.isNotEmpty(getEnums())) {
            String template = "public final static {type} {name} = new {type}();";
            template = Holder.type.on(template, getSimpleClassname());
            for (String anEnum : getEnums()) {
                newLine(sb, indented).append(Holder.name.on(template, anEnum));
            }
        }

        // const of default & format
        final int constIndex = sb.length();

        // methods
        getMethodsDecl().forEach((key, method) -> newLine(sb).append(method.toString()));

        int appendedLength = 0;

        // import scripts
        StringBuilder importsBuilder = newLine(new StringBuilder()).append(getImporter().toString("\n"));
        sb.insert(importIndex + appendedLength, importsBuilder);
        appendedLength += importsBuilder.length();

        // fields
        StringBuilder constBuilder = newLine(new StringBuilder());
        for (String value : fieldsMap.values()) {
            newLine(constBuilder, indented).append(value);
        }

        // const of custom
        newLine(constBuilder);
        for (String value : constantsMap.values()) {
            newLine(constBuilder, indented).append(value);
        }

        // final static vars
        String[] scripts = constManager.getScripts();
        if (scripts != null && scripts.length > 0) {
            newLine(constBuilder);
            for (String script : scripts) {
                newLine(constBuilder, indented).append(script);
            }
        }
        sb.insert(constIndex + appendedLength, constBuilder);
        return newLine(sb).append("}").toString();
    }

    private final static LocalDateTime NOW = LocalDateTime.now();

    private void autoGenerated(StringBuilder sb, String indented) {
        String author = getClass().getCanonicalName();
        newLine(sb).append("/**");
        newLine(sb).append(" * ").append(getFullClassname());
        newLine(sb).append(" * ");
        newLine(sb).append(" * @author ").append(author);
        newLine(sb).append(" */");
        if (Imported.GENERATED) {
            newLine(sb).append('@').append(onImported(Generated.class)).append('(');
            newLine(sb, indented).append("value = \"").append(author).append("\",");
            newLine(sb, indented).append("date = \"").append(NOW).append("\"");
            newLine(sb).append(')');
        }
        if (isComponent() && Imported.COMPONENT) {
            newLine(sb).append('@').append(onImported(Component.class));
        }
    }

    private String getFullClassname() { return classname; }

    @Override
    public void writeJavaFile(JavaWriter filer) {
        filer.write(getFullClassname(), writer -> writer.write(toString()));
    }
}
