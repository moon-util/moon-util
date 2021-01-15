package com.moon.processor.file;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.holder.ConstManager;
import com.moon.processor.holder.Importable;
import com.moon.processor.holder.Importer;
import com.moon.processor.utils.Log2;
import com.moon.processor.utils.String2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.moon.processor.file.Formatter2.onlyColonTail;

/**
 * 这种文件暂时未考虑会有泛型的情况，但可以实现有泛型的接口和继承有泛型的父类
 *
 * @author benshaoye
 */
public class DeclJavaFile implements Importable, JavaFileWriteable {

    private final JavaType type;

    private final String packageName, simpleName;

    private final List<DeclMarked> annotations = new ArrayList<>();
    private final Set<String> interfaces = new LinkedHashSet<>();
    private String superclass;

    private final Importer importer = new Importer();
    private final ConstManager constManager = new ConstManager(importer);
    private final Set<String> enums = new LinkedHashSet<>();
    private final Map<String, DeclField> fieldMap = new LinkedHashMap<>();
    private final Map<String, DeclMethod> methodsMap = new LinkedHashMap<>();
    private final List<String> instanceBlocks = new ArrayList<>();
    private final List<String> staticBlocks = new ArrayList<>();

    private DeclJavaFile(JavaType type, String packageName, String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.type = type;
        markedOf(DeclMarked::ofGenerated);
    }

    public static DeclJavaFile classOf(String packageName, String simpleName) {
        return new DeclJavaFile(JavaType.CLASS, packageName, simpleName);
    }

    public static DeclJavaFile enumOf(String packageName, String simpleName) {
        return new DeclJavaFile(JavaType.ENUM, packageName, simpleName);
    }

    @Override
    public String onImported(Class<?> classname) { return importer.onImported(classname); }

    @Override
    public String onImported(String classname) { return importer.onImported(classname); }

    public DeclJavaFile markedOf(Function<? super Importer, ? extends DeclMarked> consumer) {
        return markedOf(consumer.apply(importer));
    }

    public DeclJavaFile markedOf(DeclMarked annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public DeclMarked markedOf(Class<?> annotationClass) {
        DeclMarked annotation = new DeclMarked(importer, annotationClass);
        this.annotations.add(annotation);
        return annotation;
    }

    public ConstManager getConstManager() { return constManager; }

    public String getPackageName() { return packageName; }

    public String getSimpleName() { return simpleName; }

    public String getCanonicalName() { return String.join(".", getPackageName(), getSimpleName()); }

    public DeclJavaFile extend(String superclass) {
        if (type == JavaType.CLASS) {
            this.superclass = superclass;
        } else {
            String info = String2.format("枚举({})不可继承任何类: {}", getSimpleName(), superclass);
            throw new IllegalStateException(info);
        }
        return this;
    }

    public DeclJavaFile implement(String... interfaces) {
        if (interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces));
        }
        return this;
    }

    public DeclJavaFile enumNamesOf(String... enumNames) {
        if (enumNames != null) {
            for (String enumName : enumNames) {
                fieldMap.remove(enumName);
                enums.add(enumName);
            }
        }
        return this;
    }

    public DeclField declareField(String name, String typePattern, Object... values) {
        DeclField field = new DeclField(getConstManager(), name, typePattern, values);
        fieldMap.put(name, field);
        enums.remove(name);
        return field;
    }

    public DeclJavaFile addInstanceBlock(String pattern, Object... values) {
        instanceBlocks.add(Formatter2.toFormatted(pattern, values));
        return this;
    }

    public DeclJavaFile addStaticBlock(String pattern, Object... values) {
        staticBlocks.add(Formatter2.toFormatted(pattern, values));
        return this;
    }

    public DeclField privateField(String name, String typePattern, Object... values) {
        return declareField(name, typePattern, values).withPrivate();
    }

    public DeclField privateEnumRef(String name, String classname, String enumValue) {
        return privateConstField(name, classname).valueOf("{}.{}", onImported(classname), enumValue);
    }

    public DeclField privateConstField(String name, String typePattern, Object... values) {
        return privateField(name, typePattern, values).withFinal().withStatic();
    }

    public DeclField publicFinalField(String name, String typePattern, Object... values) {
        return privateField(name, typePattern, values).withPublic().withFinal();
    }

    public DeclField publicConstField(String name, String typePattern, Object... values) {
        return privateConstField(name, typePattern, values).withPublic();
    }

    public DeclMethod publicMethod(String name, DeclParams params) {
        DeclMethod method = new DeclMethod(getConstManager(), name, params);
        methodsMap.put(method.getUniqueDeclaredKey(), method);
        return method.withPublic();
    }

    @Override
    public String toString() {
        final StringAddr addr = StringAddr.of();
        addr.addPackage(getPackageName()).next(2);
        StringAddr.Mark importMark = addr.mark();
        // class declare
        addr.next(2).addDocComment(0, getCanonicalName(), getDocComments());
        String annotations = toAnnotationDeclared();
        if (!annotations.isEmpty()) {
            addr.addAll(0, annotations);
        }
        addr.next().add(toClassDeclared()).add(" {");
        // enum values
        addr.add(toEnumDeclared()).newTab(2, 1);
        StringAddr.Mark fieldsMark = addr.mark();
        StringAddr.Mark defaultsMark = addr.mark();
        StringAddr.Mark blockMark = addr.mark();
        // methods
        if (!methodsMap.isEmpty()) {
            addr.next().addBlockComment(1, "declared methods");
            methodsMap.forEach((d, method) -> addr.next().newTab(method.getScripts()));
        }

        // imports
        importMark.with(importer.toString("\n"));

        defaultsMark.with(toDefaultFields());

        // fields
        fieldsMark.with(toFieldDeclared());
        blockMark.with(toBlockDeclared());
        // getters & setters
        addr.add(toGetterSetterMethodsDeclared());
        return addr.next().add('}').toString();
    }

    private String toGetterSetterMethodsDeclared() {
        StringAddr addr = StringAddr.of();
        StringAddr.Mark commentMark = addr.mark();
        for (Map.Entry<String, DeclField> entry : fieldMap.entrySet()) {
            DeclField decl = entry.getValue();
            List<String> scripts = decl.getGetterMethod().getScripts();
            if (!scripts.isEmpty()) {
                commentMark.with("\n\n    /* getters & setters */");
                addr.next().newTab(scripts);
            }
            scripts = decl.getSetterMethod().getScripts();
            if (!scripts.isEmpty()) {
                commentMark.with("\n\n    /* getters & setters */");
                addr.next().newTab(scripts);
            }
        }
        return addr.isEmpty() ? "" : addr.toString();
    }

    private String toBlockDeclared() {
        StringAddr blockAddr = StringAddr.of();
        if (!staticBlocks.isEmpty()) {
            blockAddr.next().newTab("static {");
            if (staticBlocks.size() == 1) {
                blockAddr.add(' ').addScript(staticBlocks.get(0)).add(" }");
            } else {
                for (String block : staticBlocks) {
                    blockAddr.newTab(1, 2).addScript(block);
                }
                blockAddr.newTab("}");
            }
        }
        if (!instanceBlocks.isEmpty()) {
            blockAddr.next().newTab("{");
            if (instanceBlocks.size() == 1) {
                blockAddr.add(' ').addScript(instanceBlocks.get(0)).add(" }");
            } else {
                for (String block : instanceBlocks) {
                    blockAddr.newTab(1, 2).addScript(block);
                }
                blockAddr.newTab("}");
            }
        }
        return blockAddr.isEmpty() ? "" : blockAddr.toString();
    }

    private String toFieldDeclared() {
        if (!fieldMap.isEmpty()) {
            StringAddr constAddr = StringAddr.of().next(2), staticAddr = StringAddr.of();
            StringAddr finalAddr = StringAddr.of(), fieldAddr = StringAddr.of();
            List<String> instanceBlock = new ArrayList<>();
            List<String> staticBlock = new ArrayList<>();
            for (DeclField declField : fieldMap.values()) {
                StringAddr selectedAddr;
                if (declField.isFinal()) {
                    selectedAddr = declField.isStatic() ? constAddr : finalAddr;
                } else {
                    selectedAddr = declField.isStatic() ? staticAddr : fieldAddr;
                }
                selectedAddr.newTab(declField.getDeclareFieldScripts());
                staticBlock.addAll(Arrays.asList(declField.getStaticBlock()));
                instanceBlock.addAll(Arrays.asList(declField.getInstanceBlock()));
                selectedAddr.next();
            }
            appendIfNotEmpty(constAddr, staticAddr);
            appendBlock(constAddr, staticBlock, "static ");
            appendIfNotEmpty(constAddr, finalAddr);
            appendIfNotEmpty(constAddr, fieldAddr);
            appendBlock(constAddr, instanceBlock, "");
            return constAddr.toString().trim();
        }
        return "";
    }

    private String toDefaultFields() {
        List<String> list = constManager.getScripts();
        if (list.isEmpty()) {
            return "";
        }
        StringAddr addr = StringAddr.of();
        for (String script : list) {
            addr.newTab(onlyColonTail(script));
        }
        return addr.toString();
    }

    private static void appendBlock(StringAddr appender, List<String> blocks, String starting) {
        if (!blocks.isEmpty()) {
            appender.new2Tab().add(starting).add("{");
            appender.addAll(2, blocks);
            appender.newTab().add("}");
        }
    }

    private static void appendIfNotEmpty(StringAddr appender, StringAddr current) {
        if (!current.isEmpty()) {
            appender.newTab(2, 1).add(current.toString().trim());
        }
    }

    private String toEnumDeclared() {
        StringAddr addr = StringAddr.of();
        if (!enums.isEmpty()) {
            if (type == JavaType.ENUM) {
                addr.addDocComment(1, "declared table reference");
                addr.newTab(String.join(",", enums)).add(';');
            } else if (type == JavaType.CLASS) {
                addr.next();
                String type = getCanonicalName();
                for (String anEnum : enums) {
                    String imported = onImported(type);
                    String value = String2.format("new {}()", imported);
                    String script = String2.publicConstField(imported, anEnum, value);
                    addr.new2Tab().addScript(script);
                }
            }
            return addr.toString();
        }
        return type == JavaType.ENUM ? addr.newTab(";").toString() : "";
    }

    private String toClassDeclared() {
        StringBuilder declare = new StringBuilder();
        declare.append("public ").append(type.name().toLowerCase());
        declare.append(" ").append(getSimpleName());
        if (superclass != null) {
            declare.append(" extends ").append(onImported(superclass));
        }
        if (!interfaces.isEmpty()) {
            String impl = interfaces.stream().map(this::onImported).collect(Collectors.joining(", "));
            declare.append(" implements ").append(impl);
        }
        return declare.toString();
    }

    private String toAnnotationDeclared() {
        List<String> annotations = new ArrayList<>();
        for (DeclMarked annotation : this.annotations) {
            annotations.addAll(annotation.getScripts());
        }
        return String.join("\n", annotations);
    }

    private String[] getDocComments() {
        return new String[]{"", "@author " + getClass().getCanonicalName()};
    }

    @Override
    public void writeJavaFile(JavaWriter filer) {
        filer.write(getCanonicalName(), writer -> writer.write(toString()));
    }
}
