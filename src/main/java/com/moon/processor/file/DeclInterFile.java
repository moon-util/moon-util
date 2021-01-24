package com.moon.processor.file;

import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.holder.ConstManager;
import com.moon.processor.holder.Importable;
import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.moon.processor.file.Formatter2.formatGenericDeclared;
import static com.moon.processor.file.Formatter2.onlyColonTail;

/**
 * @author benshaoye
 */
public class DeclInterFile implements Importable, JavaFileWriteable {

    private final String pkg, simpleName;
    private final Importer importer;
    private final ConstManager constManager;
    private final Set<String> interfaces = new LinkedHashSet<>();
    private final List<DeclMarked> annotations = new ArrayList<>();
    private final Map<String, DeclMethod> methodsMap = new LinkedHashMap<>();
    private String genericDeclared;

    public DeclInterFile(String pkg, String simpleName) {
        this.simpleName = simpleName;
        this.pkg = pkg;
        this.importer = new Importer(pkg);
        this.constManager = new ConstManager(importer);
        markedOf(DeclMarked::ofGenerated);
    }

    public static DeclInterFile interfaceOf(String pkg, String simpleName) {
        return new DeclInterFile(pkg, simpleName);
    }

    public String getPackageName() { return pkg; }

    public String getSimpleName() { return simpleName; }

    public Importer getImporter() { return importer; }

    public ConstManager getConstManager() { return constManager; }

    protected List<DeclMarked> getAnnotations() { return annotations; }

    public String getGenericDeclared() { return genericDeclared; }

    protected Map<String, DeclMethod> getMethodsMap() { return methodsMap; }

    protected Set<String> getInterfaces() { return interfaces; }

    public String getCanonicalName() { return String.join(".", getPackageName(), getSimpleName()); }

    @Override
    public String onImported(Class<?> classname) { return importer.onImported(classname); }

    @Override
    public String onImported(String classname) { return importer.onImported(classname); }

    public DeclInterFile genericOf(String generics, Object... values) {
        this.genericDeclared = formatGenericDeclared(getImporter(), generics, values);
        return this;
    }

    public DeclInterFile markedOf(Function<? super Importer, ? extends DeclMarked> consumer) {
        return markedOf(consumer.apply(importer));
    }

    public DeclInterFile markedOf(DeclMarked annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public DeclMarked markedOf(Class<?> annotationClass) {
        DeclMarked annotation = new DeclMarked(importer, annotationClass);
        this.annotations.add(annotation);
        return annotation;
    }

    public DeclInterFile implement(String... interfaces) {
        if (interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces));
        }
        return this;
    }

    public DeclMethod publicMethod(String name, DeclParams params) {
        DeclMethod method = new DeclMethod(getConstManager(), name, params);
        methodsMap.put(method.getUniqueDeclaredKey(), method);
        return method.withPublic();
    }

    protected StringAddr.Mark predefineAntGetImportMark(StringAddr addr) {
        addr.addPackage(getPackageName()).next(2);
        StringAddr.Mark importMark = addr.mark();
        addr.next(2).addDocComment(0, getCanonicalName(), getDocComments());
        String annotationsScript = toAnnotationDeclared();
        if (!annotationsScript.isEmpty()) {
            addr.addAll(0, annotationsScript);
        }
        return importMark;
    }

    /**
     * 类内容
     *
     * @return 类内容
     *
     * @see DeclJavaFile#getClassContent()
     */
    public String getClassContent() {
        final StringAddr addr = StringAddr.of();
        StringAddr.Mark importMark = predefineAntGetImportMark(addr);
        addr.next().add(toInterDeclared()).add(" {");

        // methods
        if (!methodsMap.isEmpty()) {
            methodsMap.forEach((d, method) -> addr.next().newTab(method.getScriptsForInterface()));
        }

        // imports
        importMark.with(importer.toString("\n"));

        return addr.next().add('}').toString();
    }

    protected String toDefaultFields() {
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

    private String toInterDeclared() {
        StringBuilder declare = new StringBuilder();
        declare.append("public interface ").append(getSimpleName());
        if (String2.isNotBlank(genericDeclared)) {
            declare.append(genericDeclared.trim());
        }
        if (!interfaces.isEmpty()) {
            String impl = interfaces.stream().map(this::onImported).collect(Collectors.joining(", "));
            declare.append(" extends ").append(impl);
        }
        return declare.toString();
    }

    protected final String toAnnotationDeclared() {
        List<String> annotations = new ArrayList<>();
        for (DeclMarked annotation : this.annotations) {
            annotations.addAll(annotation.getScripts());
        }
        return String.join("\n", annotations);
    }

    protected final String[] getDocComments() {
        return new String[]{"", "@author " + getClass().getCanonicalName()};
    }

    @Override
    public String toString() { return getClassContent(); }

    @Override
    public void writeJavaFile(JavaWriter filer) {
        filer.write(getCanonicalName(), writer -> writer.write(toString()));
    }
}
