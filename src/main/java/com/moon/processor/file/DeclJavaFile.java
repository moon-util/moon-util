package com.moon.processor.file;

import com.moon.processor.manager.Importable;
import com.moon.processor.manager.Importer;
import com.moon.processor.utils.String2;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 这种文件暂时未考虑会有泛型的情况，但可以实现有泛型的接口和继承有泛型的父类
 *
 * @author benshaoye
 */
public class DeclJavaFile implements Importable {

    private final JavaType type;

    private final String packageName, simpleName;

    private final Importer importer = new Importer();

    private final Set<String> enums = new LinkedHashSet<>();
    private final Map<String, DeclField> fieldMap = new LinkedHashMap<>();

    private Set<String> interfaces = new LinkedHashSet<>();
    private String superclass;

    private DeclJavaFile(JavaType type, String packageName, String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.type = type;
    }

    public static DeclJavaFile classOf(String packageName, String simpleName) {
        return new DeclJavaFile(JavaType.CLASS, packageName, simpleName);
    }

    public static DeclJavaFile enumOf(String packageName, String simpleName) {
        return new DeclJavaFile(JavaType.ENUM, packageName, simpleName);
    }

    @Override
    public String onImported(Class<?> classname) {
        return importer.onImported(classname);
    }

    @Override
    public String onImported(String classname) {
        return importer.onImported(classname);
    }

    public String getPackageName() { return packageName; }

    public String getSimpleName() { return simpleName; }

    public String getCanonicalName() {
        return String.join(".", getPackageName(), getSimpleName());
    }

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
            for (String anInterface : interfaces) {
                this.interfaces.add(anInterface);
            }
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

    public DeclField declareField(String name) {
        DeclField field = new DeclField(importer, name);
        fieldMap.put(name, field);
        enums.remove(name);
        return field;
    }

    public DeclField privateField(String name, String typePattern, Object... values) {
        return declareField(name).withPrivate().withFieldType(typePattern, values);
    }

    public DeclField privateConstField(String name, String typePattern, Object... values) {
        return privateField(name, typePattern, values).withFinal();
    }

    public DeclField publicFinalField(String name, String typePattern, Object... values) {
        return privateField(name, typePattern, values).withPublic().withFinal();
    }

    public DeclField publicConstField(String name, String typePattern, Object... values) {
        return privateConstField(name, typePattern, values).withPublic();
    }

    @Override
    public String toString() {
        final int indent = 4;
        final StringAddr addr = StringAddr.of();
        addr.addPackage(getPackageName()).next(2);
        StringAddr.Mark importMark = addr.mark();
        // class declare
        addr.next().add(toClassDeclared()).add(" {");
        // enum values
        addr.add(toEnumDeclared(indent));
        StringAddr.Mark fieldsMark = addr.mark();
        // methods

        importMark.with(importer.toString("\n"));
        fieldsMark.with(toFieldDeclared(4));
        addr.add(toGetterSetterMethodsDeclared());
        return addr.next().add('}').toString();
    }

    private String toGetterSetterMethodsDeclared() {
        return "";
    }

    private String toFieldDeclared(int indent) {
        if (fieldMap.isEmpty()) {
            StringAddr constAddr = StringAddr.of().next(2), staticAddr = StringAddr.of();
            StringAddr finalAddr = StringAddr.of(), fieldAddr = StringAddr.of();
            for (DeclField declField : fieldMap.values()) {
                StringAddr selectedAddr;
                if (declField.isFinal()) {
                    selectedAddr = declField.isStatic() ? constAddr : finalAddr;
                } else {
                    selectedAddr = declField.isStatic() ? staticAddr : fieldAddr;
                }
                String declare = declField.getDeclareFieldScript();
                selectedAddr.next().indent(indent).addScript(declare);
            }
            appendIfNotEmpty(constAddr, staticAddr);
            appendIfNotEmpty(constAddr, finalAddr);
            appendIfNotEmpty(constAddr, fieldAddr);
            return constAddr.toString();
        }
        return "";
    }

    private static void appendIfNotEmpty(StringAddr appender, StringAddr current) {
        if (!current.isEmpty()) {
            appender.next(2).add(current);
        }
    }

    private String toEnumDeclared(int indent) {
        if (!enums.isEmpty()) {
            StringAddr addr = StringAddr.of();
            if (type == JavaType.ENUM) {
                addr.indent(indent).addScript(String.join(",", enums));
            } else if (type == JavaType.CLASS) {
                addr.next();
                String type = getCanonicalName();
                for (String anEnum : enums) {
                    String value = String2.format("new {}()", type);
                    String script = String2.toConstField(type, anEnum, value);
                    addr.next(2).indent(indent).addScript(script);
                }
            }
        }
        return "";
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
}
