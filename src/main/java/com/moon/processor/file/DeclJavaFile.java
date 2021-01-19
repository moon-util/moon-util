package com.moon.processor.file;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.holder.Importable;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 这种文件暂时未考虑会有泛型的情况，但可以实现有泛型的接口和继承有泛型的父类
 *
 * @author benshaoye
 */
public class DeclJavaFile extends DeclInterFile implements Importable, JavaFileWriteable {

    private String superclass;
    private final JavaType type;
    private final Set<String> enums = new LinkedHashSet<>();
    private final Map<String, DeclField> fieldMap = new LinkedHashMap<>();
    private final List<String> instanceBlocks = new ArrayList<>();
    private final List<String> staticBlocks = new ArrayList<>();
    private final Map<String, DeclConstruct> constructMap = new LinkedHashMap<>();

    private DeclJavaFile(JavaType type, String packageName, String simpleName) {
        super(packageName, simpleName);
        this.type = type;
    }

    public static DeclJavaFile classOf(String packageName, String simpleName) {
        return new DeclJavaFile(JavaType.CLASS, packageName, simpleName);
    }

    public static DeclJavaFile enumOf(String packageName, String simpleName) {
        return new DeclJavaFile(JavaType.ENUM, packageName, simpleName);
    }

    public DeclJavaFile extend(Class<?> superclass) {
        return extend(superclass.getCanonicalName());
    }

    public DeclJavaFile extend(String superclass, Object... types) {
        if (type == JavaType.CLASS) {
            this.superclass = Formatter2.toFormatted(superclass, types);
        } else {
            String info = String2.format("枚举({})不可继承任何类: {}", getSimpleName(), superclass);
            throw new IllegalStateException(info);
        }
        return this;
    }

    @Override
    public DeclJavaFile implement(String... interfaces) {
        super.implement(interfaces);
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

    public DeclConstruct construct(DeclParams params) {
        DeclConstruct construct = new DeclConstruct(getConstManager(), Modifier.PUBLIC, getSimpleName(), params);
        constructMap.put(construct.getUniqueDeclaredKey(), construct);
        return construct;
    }

    public Map<String, DeclConstruct> getConstructMap() { return constructMap; }

    /**
     * 类内容
     *
     * @return 类内容
     *
     * @see DeclInterFile#getClassContent()
     */
    @Override
    public String getClassContent() {
        final StringAddr addr = StringAddr.of();
        StringAddr.Mark importMark = predefineAntGetImportMark(addr);
        addr.next().add(toClassDeclared()).add(" {");
        // enum values
        addr.add(toEnumDeclared()).newTab(2, 1);
        StringAddr.Mark fieldsMark = addr.mark();
        StringAddr.Mark defaultsMark = addr.mark();
        StringAddr.Mark blockMark = addr.mark();
        // constructs
        if (!getConstructMap().isEmpty()) {
            getConstructMap().forEach((d, construct) -> addr.next().newTab(construct.getScripts()));
        }
        // methods
        if (!getMethodsMap().isEmpty()) {
            addr.next().addBlockComment(1, "declared methods");
            getMethodsMap().forEach((d, method) -> addr.next().newTab(method.getScripts()));
        }

        // imports
        importMark.with(getImporter().toString("\n"));

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
            staticBlock.sort(Comparator.comparingInt(String::length));
            instanceBlock.sort(Comparator.comparingInt(String::length));
            appendIfNotEmpty(constAddr, staticAddr);
            appendBlock(constAddr, staticBlock, "static ");
            appendIfNotEmpty(constAddr, finalAddr);
            appendIfNotEmpty(constAddr, fieldAddr);
            appendBlock(constAddr, instanceBlock, "");
            return constAddr.toString().trim();
        }
        return "";
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
        if (String2.isNotBlank(getGenericDeclared())) {
            declare.append(getGenericDeclared().trim());
        }
        if (superclass != null) {
            declare.append(" extends ").append(onImported(superclass));
        }
        if (!getInterfaces().isEmpty()) {
            String impl = getInterfaces().stream().map(this::onImported).collect(Collectors.joining(", "));
            declare.append(" implements ").append(impl);
        }
        return declare.toString();
    }
}
