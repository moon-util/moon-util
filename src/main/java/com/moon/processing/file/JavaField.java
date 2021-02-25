package com.moon.processing.file;

import com.moon.processing.util.String2;

import javax.lang.model.element.Modifier;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaField extends JavaBlockCommentable implements Appender {

    private final String classname;
    private final ScopedMethods scopedMethods;
    private final String fieldType;
    private final String fieldName;
    private final boolean inInterface;
    private JavaFieldValue fieldValue;
    private LineScripter scripter;
    private boolean forceInline;

    public JavaField(
        Importer importer,
        String classname,
        ScopedMethods scopedMethods,
        String fieldName,
        String fieldTypeTemplate,
        Object... types
    ) {
        super(importer);
        this.fieldType = Formatter2.with(fieldTypeTemplate, types);
        this.inInterface = scopedMethods.inInterface();
        this.scopedMethods = scopedMethods;
        this.classname = classname;
        this.fieldName = fieldName;
    }

    @Override
    public Set<Modifier> getAllowedModifiers() {
        return inInterface ? Collections.emptySet() : Modifier2.FOR_FIELD;
    }

    public LineScripter getLineScripter() { return scripter; }

    public String getFieldName() { return fieldName; }

    public void withForceInline() { this.forceInline = true; }

    public boolean isForceInline() { return forceInline; }

    /**
     * 内容签名，返回字段类型和字段值组成的内容，可用于反向获取字段名
     *
     * @return
     */
    public String getContentSymbol() { return fieldType + ">>" + fieldValue; }

    public JavaField valueOf(Consumer<JavaFieldValue> valueBuilder) {
        JavaFieldValue value = new JavaFieldValue(getImporter(), classname);
        valueBuilder.accept(value);
        if (value.isValueAvailable()) {
            this.fieldValue = value;
        }
        return this;
    }

    public JavaMethod useMethod(String name) { return useMethod(name, p -> {}); }

    public JavaMethod useMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaMethod method = scopedMethods.useMethod(name, parametersBuilder);
        method.withPublic();
        return method.withPropertyMethod();
    }

    public JavaMethod publicMethod(String name) { return publicMethod(name, p -> {}); }

    public JavaMethod publicMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaMethod method = scopedMethods.declareMethod(name, parametersBuilder);
        method.withPublic();
        return method.withPropertyMethod();
    }

    public JavaMethod withGetterMethod() { return withGetterMethod(fieldType); }

    public JavaMethod withGetterMethod(String typeTemplate, Object... types) {
        String getterType = Formatter2.with(typeTemplate, types);
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

        JavaFieldValue value = this.fieldValue;
        if (value != null && value.isValueAvailable()) {
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
