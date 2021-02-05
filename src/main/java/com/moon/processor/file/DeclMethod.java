package com.moon.processor.file;

import com.moon.processor.holder.ConstManager;
import com.moon.processor.mapping.MappingType;
import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.String2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.moon.processor.file.Formatter2.formatGenericDeclared;
import static com.moon.processor.file.Formatter2.onlyColonTail;

/**
 * @author benshaoye
 */
public class DeclMethod extends DeclModifier<DeclMethod> implements ScriptsProvider {

    public final static DeclMethod EMPTY = new Scripts(Const2.EMPTY);

    private final List<DeclMarked> annotations = new ArrayList<>();
    private final Map<String, ScriptsProvider> scriptsMap = new LinkedHashMap<>();
    private final AtomicInteger indexer = new AtomicInteger();
    private final DeclParams params;
    private final String name;
    private String returnType;
    private String genericDeclared;

    DeclMethod(ConstManager importer, String name, DeclParams params) {
        super(importer);
        this.params = params;
        this.name = name;
    }

    private String nextKey() { return String2.format("<{}>", indexer.getAndIncrement()); }

    public static DeclMethod ofGetter(ConstManager importer, String name, String type) {
        return new DeclMethod(importer, String2.toGetterName(name, type), DeclParams.of()).withPublic()
            .returnTypeof(type);
    }

    public static DeclMethod ofSetter(ConstManager importer, String name, String type) {
        return ofSetter(importer, String2.toSetterName(name), name, type);
    }

    public static DeclMethod ofSetter(ConstManager importer, String setterName, String name, String type) {
        return new DeclMethod(importer, setterName, DeclParams.of(name, type)).withPublic();
    }

    public DeclMethod markedOf(Function<? super ConstManager, ? extends DeclMarked> consumer) {
        return markedOf(consumer.apply(getImportable()));
    }

    public DeclMarked markedOf(Class<?> annotationClass) {
        DeclMarked annotation = new DeclMarked(getImportable(), annotationClass);
        this.annotations.add(annotation);
        return annotation;
    }

    public DeclMethod markedOf(DeclMarked annotation) {
        if (annotation != null) {
            this.annotations.add(annotation);
        }
        return this;
    }

    public DeclMethod override() { return markedOf(DeclMarked::ofOverride); }

    public DeclMethod genericOf(String generics, Object... values) {
        this.genericDeclared = formatGenericDeclared(getImportable(), generics, values);
        return this;
    }

    public String getName() { return name; }

    public DeclParams getClonedParams() {
        return this.params.clone();
    }

    public String getUniqueDeclaredKey() { return getUniqueDeclaredKey(true); }

    public String getUniqueDeclaredKey(boolean simplify) {
        return String2.format("{}({})", name, Formatter2.toParamsDeclared(getImporter(), params, simplify));
    }

    public DeclMethod returnTypeof(Class<?> type) { return returnTypeof(type.getCanonicalName()); }

    public DeclMethod returnTypeof(String typePattern, Object... values) {
        this.returnType = onImported(Formatter2.toFormatted(typePattern, values));
        return this;
    }

    public DeclMapping convert(
        String name, DeclareProperty thisProp, DeclareProperty thatProp, DeclareMapping mapping, MappingType type
    ) {
        DeclMapping decl = DeclMapping.convert(getImportable(), thisProp, thatProp, mapping, type);
        scriptOf(name, decl);
        return decl;
    }

    public <T> DeclMethod scriptOf(
        String key, T customVal, BiFunction<? super ConstManager, T, ? extends ScriptsProvider> provider
    ) {
        scriptsMap.put(key, provider.apply(getImportable(), customVal));
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
        return String2.isBlank(genericDeclared) ? "" : String.format("%s ", genericDeclared);
    }

    public List<String> getScriptsForInterface() {
        List<String> methodScripts = new ArrayList<>(getCommentScripts());
        annotations.forEach(annotation -> methodScripts.addAll(annotation.getScripts()));
        String declared = getUniqueDeclaredKey(false);
        String methodLeading = getGenericDeclared();
        String methodDecl = String2.format("{} {} {};", methodLeading, getReturnType(), declared);
        methodScripts.add(methodDecl);
        return methodScripts;
    }

    @Override
    public List<String> getScripts() {
        List<String> methodScripts = new ArrayList<>(getCommentScripts());
        annotations.forEach(annotation -> methodScripts.addAll(annotation.getScripts()));

        String declared = getUniqueDeclaredKey(false);
        String methodLeading = getModifiersDeclared() + getGenericDeclared();
        String methodDecl = String2.format("{} {} {} {", methodLeading, getReturnType(), declared);
        List<String> contentScripts = extractScripts(this.scriptsMap);
        // 只有一行代码的方法不展开，只占用一行
        if (contentScripts.isEmpty()) {
            methodScripts.add(methodDecl + " }");
            return methodScripts;
        } else if (contentScripts.size() == 1) {
            String content = onlyColonTail(contentScripts.get(0).trim());
            String method = String2.format("{} {} }", methodDecl, content);
            if (method.length() < 160) {
                methodScripts.add(method);
                return methodScripts;
            }
        }
        methodScripts.add(methodDecl);
        methodScripts.addAll(contentScripts);
        methodScripts.add("}");
        return methodScripts;
    }

    private static List<String> extractScripts(Map<String, ScriptsProvider> statements) {
        List<String> methodScripts = new ArrayList<>();
        ScriptsProvider returning = statements.remove(null);
        statements.forEach((k, provider) -> methodScripts.addAll(padScripts(provider.getScripts())));
        if (returning != null) {
            methodScripts.addAll(padScripts(returning.getScripts()));
        }
        return methodScripts;
    }

    private static List<String> padScripts(List<String> scripts) {
        List<String> result = new ArrayList<>(scripts.size());
        for (String script : scripts) {
            result.add(onlyColonTail("    " + script));
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

