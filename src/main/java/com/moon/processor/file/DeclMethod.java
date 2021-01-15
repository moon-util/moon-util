package com.moon.processor.file;

import com.moon.processor.manager.Importable;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.String2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author benshaoye
 */
public class DeclMethod extends DeclModifier<DeclMethod> implements ScriptsProvider {

    public final static DeclMethod EMPTY = new Scripts(Const2.EMPTY);

    private final List<DeclAnnotation> annotations = new ArrayList<>();
    private final Map<String, ScriptsProvider> scriptsMap = new LinkedHashMap<>();
    private final AtomicInteger indexer = new AtomicInteger();
    private final DeclParams params;
    private final String name;
    private String returnType;
    private String genericDeclared;

    public DeclMethod(Importable importer, String name, DeclParams params) {
        super(importer);
        this.params = params;
        this.name = name;
    }

    private String nextKey() {
        return String2.format("<{}>", indexer.getAndIncrement());
    }

    public static DeclMethod ofGetter(Importable importer, String name, String type) {
        return new DeclMethod(importer, String2.toGetterName(name, type), DeclParams.of()).returnTypeof(type);
    }

    public static DeclMethod ofSetter(Importable importer, String name, String type) {
        return new DeclMethod(importer, String2.toSetterName(name), DeclParams.of(name, type));
    }

    public DeclAnnotation annotatedOf(Class<?> annotationClass) {
        DeclAnnotation annotation = new DeclAnnotation(getImportable(), annotationClass);
        this.annotations.add(annotation);
        return annotation;
    }

    public DeclMethod annotatedOf(DeclAnnotation annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public DeclMethod override() { return annotatedOf(DeclAnnotation.ofOverride(getImportable())); }

    public DeclMethod genericOf(String generics, Object... values) {
        this.genericDeclared = String2.format(generics, Arrays.stream(values).map(value -> {
            if (value instanceof Class<?>) {
                return onImported((Class<?>) value);
            } else {
                return onImported(value.toString());
            }
        }).toArray(Object[]::new));
        return this;
    }

    public String getUniqueDeclaredKey() {
        return getUniqueDeclaredKey(true);
    }

    public String getUniqueDeclaredKey(boolean simplify) {
        return String2.format("{}({})", name, Formatter2.toParamsDeclared(getImporter(), params, simplify));
    }

    public DeclMethod returnTypeof(Class<?> type) { return returnTypeof(type.getCanonicalName()); }

    public DeclMethod returnTypeof(String typePattern, Object... values) {
        this.returnType = onImported(Formatter2.toFormatted(typePattern, values));
        return this;
    }

    public DeclMethod scriptOf(String key, ScriptsProvider scripts) {
        scriptsMap.put(key, scripts);
        return this;
    }

    public DeclMethod scriptOf(String script, String... values) {
        return scriptOf(nextKey(), new Scripts(String2.format(script, values)));
    }

    public DeclMethod returning(String script, Object... values) {
        String fmt = Formatter2.toFormatted(script, values);
        return scriptOf(null, new Scripts("return " + fmt));
    }

    private String getReturnType() { return String2.isBlank(returnType) ? "void" : returnType; }

    public String getGenericDeclared() {
        return String2.isBlank(genericDeclared) ? "" : String.format(" %s", genericDeclared);
    }

    @Override
    public List<String> getScripts() {
        List<String> methodScripts = new ArrayList<>();
        annotations.forEach(annotation -> methodScripts.addAll(annotation.getScripts()));
        String declared = getUniqueDeclaredKey(false);

        String methodLeading = getModifiersDeclared() + getGenericDeclared();
        methodScripts.add(String2.format("{} {} {} {", methodLeading, getReturnType(), declared));
        ScriptsProvider returning = scriptsMap.remove(null);
        scriptsMap.forEach((k, provider) -> methodScripts.addAll(padScripts(provider.getScripts())));
        if (returning != null) {
            methodScripts.addAll(padScripts(returning.getScripts()));
        }
        methodScripts.add("}");
        return methodScripts;
    }

    private List<String> padScripts(List<String> scripts) {
        List<String> result = new ArrayList<>(scripts.size());
        for (String script : scripts) {
            result.add(Formatter2.onlyColonTail("    " + script));
        }
        return result;
    }

    private final static class Scripts extends DeclMethod {

        private final String[] scripts;

        private Scripts(String... scripts) {
            super(null, null, null);
            this.scripts = scripts;
        }

        @Override
        public List<String> getScripts() { return Arrays.asList(scripts); }
    }
}

