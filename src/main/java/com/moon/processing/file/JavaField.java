package com.moon.processing.file;

import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaField extends JavaBlockCommentable implements Appender {

    private final String classname;
    private final String fieldType;
    private final String fieldName;
    private final boolean inInterface;
    private JavaFieldValue value;
    /** 这里的 getter & setter 不受类全局管理，使用时需要注意 */
    private JavaMethod getter;
    private Map<String, JavaMethod> typedSettersMap;
    private LineScripter scripter;

    private final Map<String, JavaMethod> declaredMethodsMap = new LinkedHashMap<>();

    public JavaField(
        Importer importer,
        boolean inInterface,
        String classname,
        String fieldName,
        String fieldTypeTemplate,
        Object... types
    ) {
        super(importer);
        this.fieldType = Formatter.with(fieldTypeTemplate, types);
        this.inInterface = inInterface;
        this.classname = classname;
        this.fieldName = fieldName;
    }

    private Map<String, JavaMethod> getTypedSettersMap() {
        return typedSettersMap == null ? (typedSettersMap = new LinkedHashMap<>()) : typedSettersMap;
    }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_FIELD; }

    public boolean hasBlockScript() { return scripter != null; }

    public LineScripter getLineScripter() { return scripter; }

    public String getFieldName() { return fieldName; }

    public JavaField valueOf(Consumer<JavaFieldValue> valueBuilder) {
        JavaFieldValue value = new JavaFieldValue(getImporter(), classname);
        valueBuilder.accept(value);
        this.value = value;
        return this;
    }

    public JavaMethod withPublicMethod(String name) {
        return withPublicMethod(name, p -> {});
    }

    public JavaMethod withPublicMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaParameters parameters = new JavaParameters(getImporter());
        parametersBuilder.accept(parameters);
        JavaMethod method = new JavaMethod(getImporter(), name, parameters, inInterface);
        declaredMethodsMap.put(method.getUniqueKey(), method);
        method.withPublic();
        return method;
    }

    public JavaMethod withGetterMethod() { return withGetterMethod(fieldType); }

    public JavaMethod withGetterMethod(String typeTemplate, Object... types) {
        String getterType = Formatter.with(typeTemplate, types);
        String getterName = String2.toGetterName(fieldName, getterType);
        JavaParameters params = new JavaParameters(getImporter());
        JavaMethod getter = new JavaMethod(getImporter(), getterName, params, inInterface);
        this.getter = getter;
        return getter;
    }

    public JavaMethod withSetterMethod() { return withSetterMethod(fieldType); }

    public JavaMethod withSetterMethod(String typeTemplate, Object... types) {
        String setterName = String2.toSetterName(fieldName);
        JavaParameters params = new JavaParameters(getImporter());
        JavaParameter parameter = params.add(fieldName, typeTemplate, types);
        JavaMethod setter = new JavaMethod(getImporter(), setterName, params, inInterface);
        typedSettersMap.put(parameter.getTypeSimplify(), setter);
        return setter;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        addr.next();
        final int beforeLineNumber = addr.lineNumber();
        super.appendTo(addr);
        if (beforeLineNumber != addr.lineNumber()) {
            addr.next();
        }
        addr.padAdd("");
        for (Modifier modifier : getModifiers()) {
            addr.add(modifier.name().toLowerCase()).add(' ');
        }
        addr.add(onImported(fieldType)).add(" ").add(fieldName);
        if (value != null) {
            String script = value.toString();
            if (addr.willOverLength(script)) {
                if (isStatic()) {
                    String line = String2.format("{} = {}", getFieldName(), script);
                    this.scripter = new LineScripterImpl(this, line);
                } else {
                    String line = String2.format("this.{} = {}", getFieldName(), script);
                    this.scripter = new LineScripterImpl(this, line);
                }
            } else {
                addr.add(" = ").addScript(script);
            }
        }
        addr.scriptEnd();
    }

    public void appendGetterSetterMethods(JavaAddr addr) {
        if (getter != null) {
            getter.appendTo(addr);
        }
        if (typedSettersMap != null) {
            for (JavaMethod setterMethod : getTypedSettersMap().values()) {
                setterMethod.appendTo(addr);
            }
        }
        if (!declaredMethodsMap.isEmpty()) {
            for (JavaMethod method : declaredMethodsMap.values()) {
                method.appendTo(addr);
            }
        }
    }

    private static class LineScripterImpl implements LineScripter {

        private final JavaField field;
        private final String line;

        public LineScripterImpl(JavaField field, String line) {
            this.field = field;
            this.line = line;
        }

        @Override
        public int length() { return line == null ? 0 : line.length(); }

        @Override
        public void addJavaScript(JavaAddr addr) {
            addr.newScript(line);
            field.scripter = null;
        }
    }
}
