package com.moon.processor.def;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.file.StringAddr;
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

    private final Map<String, String> staticBlock = new LinkedHashMap<>();
    private final Map<String, String> instanceBlock = new LinkedHashMap<>();
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

    private final static HolderGroup GROUP = Holder.of(Holder.type, Holder.var, Holder.value);
    private final static String ENUM_REF = "private final static {type} {var} = {type}.{value};";

    public DefJavaFiler privateEnumRef(String type, String var, String enumValue) {
        if (String2.isNotBlank(type) && String2.isNotBlank(var) && String2.isNotBlank(enumValue)) {
            constantsMap.put(var, GROUP.on(ENUM_REF, onImported(type), var, enumValue));
        }
        return this;
    }

    public DefJavaFiler privateConstField(String type, String name, String value) {
        if (String2.isNotBlank(type) && String2.isNotBlank(name) && String2.isNotBlank(value)) {
            constantsMap.put(name, String2.privateConstField(onImported(type), name, value));
        }
        return this;
    }

    public DefJavaFiler privateField(String name, String type) {
        if (String2.isNotBlank(type) && String2.isNotBlank(name)) {
            fieldsMap.put(name, String2.toPrivateField(name, onImported(type)));
        }
        return this;
    }

    public DefJavaFiler publicFinalField(String type, String name, String value) {
        String template = "public final {type} {name} = {value};";
        HolderGroup group = Holder.of(Holder.type, Holder.name, Holder.value);
        String declared = group.on(template, onImported(type), name, value);
        if (declared.length() > 80) {
            String t0 = "public final {} {};", t1 = "{} = {};";
            String decl = String2.format(t0, onImported(type), name);
            String assign = String2.format(t1, name, value);
            instanceBlock.put(decl, assign);
            fieldsMap.put(name, decl);
        } else {
            fieldsMap.put(name, declared);
        }
        return this;
    }

    public DefJavaFiler publicConstField(String type, String name, String value) {
        String template = "public final static {type} {name} = {value};";
        HolderGroup group = Holder.of(Holder.type, Holder.name, Holder.value);
        String declared = group.on(template, onImported(type), name, value);
        if (declared.length() > 80) {
            String t0 = "public final static {} {};", t1 = "{} = {};";
            String decl = String2.format(t0, onImported(type), name);
            String assign = String2.format(t1, name, value);
            constantsMap.put(name, decl);
            staticBlock.put(decl, assign);
        } else {
            constantsMap.put(name, declared);
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
        String name, Class<?> returnClass, DefParameters parameters
    ) { return publicMethod(false, name, returnClass.getCanonicalName(), parameters); }

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
        StringAddr addr = StringAddr.of();
        addr.addPackage(getPkg()).next();
        StringAddr.Mark importMark = addr.mark();
        autoGenerated(addr);
        addr.next().add(getClassDeclareScript()).add(" {");
        // enums
        if (getType() == Type.ENUM) {
            addr.newTab();
            if (Collect2.isNotEmpty(getEnums())) {
                addr.add(String.join(", ", getEnums()));
            }
            addr.add(';');
        } else if (Collect2.isNotEmpty(getEnums())) {
            String template = "public final static {type} {name} = new {type}();";
            template = Holder.type.on(template, getSimpleClassname());
            addr.next();
            for (String anEnum : getEnums()) {
                addr.newTab().append(Holder.name.on(template, anEnum));
            }
        }
        StringAddr.Mark fieldsMark = addr.mark();

        getMethodsDecl().forEach((key, method) -> addr.next().add(method.toString()));


        // import scripts
        StringBuilder importsBuilder = newLine(new StringBuilder()).append(getImporter().toString("\n"));

        StringAddr constAddr = StringAddr.of();

        // const of custom
        if (!constantsMap.isEmpty()) {
            for (String value : constantsMap.values()) {
                constAddr.next(2).tab().addScript(value);
            }
        }

        // final static vars
        String[] scripts = constManager.getScripts();
        if (scripts != null && scripts.length > 0) {
            constAddr.next(2);
            for (String script : scripts) {
                constAddr.next(2).tab().addScript(script);
            }
        }

        // fields
        if (!fieldsMap.isEmpty()) {
            constAddr.next(2);
            for (String value : fieldsMap.values()) {
                constAddr.next(2).tab().addScript(value);
            }
        }

        // static block
        buildBlock("static {", addr, staticBlock);

        // static block
        buildBlock("{", addr, instanceBlock);

        importMark.with(importsBuilder);
        fieldsMark.with(constAddr);
        return addr.next().add('}').toString();
    }

    private static void buildBlock(
        String blockStarting, StringAddr addr, Map<String, String> block
    ) {
        List<String> scripts = new ArrayList<>(block.values());
        if (!scripts.isEmpty()) {
            addr.next(2).tab().add(blockStarting);
            for (String value : scripts) {
                addr.newTab().tab().addScript(value);
            }
            addr.newTab().add('}');
        }
    }

    private final static LocalDateTime NOW = LocalDateTime.now();

    private void autoGenerated(StringAddr addr) {
        String author = getClass().getCanonicalName();
        addr.next().add("/**");
        addr.next().add(" * ").add(getFullClassname());
        addr.next().add(" *");
        addr.next().add(" * @author ").add(author);
        addr.next().add(" */");
        if (Imported.GENERATED) {
            addr.next().add('@').add(onImported(Generated.class)).add('(');
            addr.newTab().add("value = \"").add(author).add("\",");
            addr.newTab().add("date = \"").add(NOW).add("\"");
            addr.next().add(')');
        }
        if (isComponent() && Imported.COMPONENT) {
            addr.next().add('@').add(onImported(Component.class));
        }
    }

    private String getFullClassname() { return classname; }

    @Override
    public void writeJavaFile(JavaWriter filer) {
        filer.write(getFullClassname(), writer -> writer.write(toString()));
    }
}
