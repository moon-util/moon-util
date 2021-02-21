package com.moon.processing.file;

import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaField extends JavaBlockCommentable implements Appender {

    private final String classname;
    private final MethodsScoped methodsScoped;
    private final String fieldType;
    private final String fieldName;
    private JavaFieldValue value;
    private LineScripter scripter;
    private boolean forceInline;

    public JavaField(
        Importer importer,
        String classname,
        MethodsScoped methodsScoped,
        String fieldName,
        String fieldTypeTemplate,
        Object... types
    ) {
        super(importer);
        this.fieldType = Formatter.with(fieldTypeTemplate, types);
        this.methodsScoped = methodsScoped;
        this.classname = classname;
        this.fieldName = fieldName;
    }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_FIELD; }

    public LineScripter getLineScripter() { return scripter; }

    public String getFieldName() { return fieldName; }

    public void withForceInline() { this.forceInline = true; }

    public boolean isForceInline() { return forceInline; }

    public JavaField valueOf(Consumer<JavaFieldValue> valueBuilder) {
        JavaFieldValue value = new JavaFieldValue(getImporter(), classname);
        valueBuilder.accept(value);
        this.value = value;
        return this;
    }

    public JavaMethod useMethod(String name) { return useMethod(name, p -> {}); }

    public JavaMethod useMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaMethod method = methodsScoped.useMethod(name, parametersBuilder);
        method.withPublic();
        return method.withPropertyMethod();
    }

    public JavaMethod publicMethod(String name) { return publicMethod(name, p -> {}); }

    public JavaMethod publicMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaMethod method = methodsScoped.declareMethod(name, parametersBuilder);
        method.withPublic();
        return method.withPropertyMethod();
    }

    public JavaMethod withGetterMethod() { return withGetterMethod(fieldType); }

    public JavaMethod withGetterMethod(String typeTemplate, Object... types) {
        String getterType = Formatter.with(typeTemplate, types);
        String getterName = String2.toGetterName(fieldName, getterType);
        return publicMethod(getterName).typeOf(getterType);
    }

    public JavaMethod withSetterMethod() { return withSetterMethod(fieldType); }

    public JavaMethod withSetterMethod(String typeTemplate, Object... types) {
        return publicMethod(String2.toSetterName(fieldName), parameters -> {
            parameters.add(fieldName, typeTemplate, types);
        });
    }

    public JavaMethod useSetterMethod() { return useSetterMethod(fieldType); }

    public JavaMethod useSetterMethod(String typeTemplate, Object... types) {
        return useMethod(String2.toSetterName(fieldName), parameters -> {
            parameters.add(fieldName, typeTemplate, types);
        });
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
            if (!isForceInline() && addr.willOverLength(script)) {
                if (isStatic()) {
                    String line = String2.format("{} = {}", getFieldName(), script);
                    this.scripter = new FieldScripterImpl(this, line);
                } else {
                    String line = String2.format("this.{} = {}", getFieldName(), script);
                    this.scripter = new FieldScripterImpl(this, line);
                }
            } else {
                addr.add(" = ").addScript(script);
            }
        }
        addr.scriptEnd();
    }

    private static class FieldScripterImpl extends LineScripterImpl {

        private final JavaField field;

        public FieldScripterImpl(JavaField field, String line) {
            super(line);
            this.field = field;
        }

        @Override
        public void appendTo(JavaAddr addr) {
            super.appendTo(addr);
            field.scripter = null;
        }
    }
}
